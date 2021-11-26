package com.zj.im.sender

import com.zj.im.chat.enums.SendMsgState
import com.zj.im.chat.modle.BaseMsgInfo
import com.zj.im.chat.modle.SendingUp
import com.zj.im.main.dispatcher.DataReceivedDispatcher
import com.zj.im.utils.cusListOf
import com.zj.im.utils.log.logger.printInFile

/**
 * Created by ZJJ
 */
internal class SendingPool<T> : OnStatus<T> {

    private var sending = false

    private val sendMsgQueue = cusListOf<BaseMsgInfo<T>>()

    fun setSendState(state: SendingUp, callId: String, data: T? = null, payloadInfo: Any?) {
        sendMsgQueue.getFirst { obj -> obj.callId == callId }?.apply {
            this.sendingUp = state
            this.data = data
            val sendState: SendMsgState
            if (state != SendingUp.CANCEL) {
                this.onSendBefore = null
                sendState = SendMsgState.ON_SEND_BEFORE_END
            } else {
                sendState = SendMsgState.FAIL.setSpecialBody(payloadInfo)
            }
            val notifyState = BaseMsgInfo.sendingStateChange(sendState, callId, data, isResend)
            DataReceivedDispatcher.pushData(notifyState)
        }
    }

    fun push(info: BaseMsgInfo<T>) {
        sendMsgQueue.add(info)
        if (info.onSendBefore != null) info.onSendBefore?.call(this)
    }

    fun lock() {
        sending = true
    }

    fun unLock() {
        sending = false
    }

    fun pop(): BaseMsgInfo<T>? {
        if (sending) return null
        if (sendMsgQueue.isEmpty()) return null
        if (!DataReceivedDispatcher.isDataEnable()) {
            val grouped = sendMsgQueue.group {
                it.ignoreConnecting
            } ?: return null
            sendMsgQueue.clear()
            sendMsgQueue.addAll(grouped[true])
            grouped[false]?.forEach { ds ->
                ds.joinInTop = true
                DataReceivedDispatcher.pushData(ds)
            }
            if (sendMsgQueue.isEmpty()) return null
        }
        var firstInStay = sendMsgQueue.getFirst()
        if (firstInStay?.sendingUp == SendingUp.WAIT) {
            firstInStay = sendMsgQueue.getFirst {
                it.sendingUp == SendingUp.NORMAL
            }
        }
        firstInStay?.let {
            sendMsgQueue.remove(it)
            return it
        }
        return null
    }

    fun deleteFormQueue(callId: String?) {
        callId?.let {
            sendMsgQueue.removeIf { m ->
                m.callId == callId
            }
        }
    }

    fun queryInSendingQueue(predicate: (BaseMsgInfo<T>) -> Boolean): Boolean {
        return sendMsgQueue.contains(predicate)
    }

    fun clear() {
        sendMsgQueue.clear()
        sending = false
    }

    override fun call(isFinish: Boolean, callId: String, progress: Int, data: T, isOK: Boolean, e: Throwable?, payloadInfo: Any?) {
        if (isFinish) {
            if (isOK) {
                printInFile("SendExecutors.send", "$callId before sending task success\npayload = $payloadInfo")
            } else {
                printInFile("SendExecutors.send", "$callId before sending task error,case:\n${e?.message}\npayload = $payloadInfo")
            }
            setSendState(if (isOK) SendingUp.READY else SendingUp.CANCEL, callId, data, payloadInfo)
        } else {
            sendMsgQueue.getFirst { obj -> obj.callId == callId }?.apply {
                customSendingCallback?.let {
                    it.onSendingUploading(progress, callId)
                    if (it.pending) DataReceivedDispatcher.pushData(BaseMsgInfo.onProgressChange<T>(progress, callId))
                } ?: DataReceivedDispatcher.pushData(BaseMsgInfo.onProgressChange<T>(progress, callId))
            }
        }
    }
}
