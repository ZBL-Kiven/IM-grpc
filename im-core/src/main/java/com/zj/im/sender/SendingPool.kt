package com.zj.im.sender

import com.zj.im.chat.enums.SendMsgState
import com.zj.im.chat.exceptions.IMException
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

    fun push(info: BaseMsgInfo<T>) {
        sendMsgQueue.add(info)
        if (info.onSendBefore.isNullOrEmpty().not()) {
            info.onSendBefore?.poll()?.call(this)
        }
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

    override fun call(callId: String, data: T) {
        printInFile("SendExecutors.send", "$callId before sending task success")
        sendMsgQueue.getFirst { obj -> obj.callId == callId }?.apply {
            if (onSendBefore.isNullOrEmpty()) {
                this.sendingUp = SendingUp.READY
                this.data = data
                val sendState = SendMsgState.ON_SEND_BEFORE_END
                val notifyState = BaseMsgInfo.sendingStateChange(sendState, callId, data, isResend, sendWithoutState)
                DataReceivedDispatcher.pushData(notifyState)
            } else {
                this.onSendBefore?.poll()?.call(this@SendingPool)
            }
        }
    }

    override fun error(callId: String, e: Throwable?, payloadInfo: Any?) {
        if (e != null) {
            val ime = if (e is IMException) {
                val body = e.getBodyData()
                IMException("SendExecutors.send $callId before sending task error,case:\n${e.message}\n payload = $payloadInfo", body)
            } else {
                IMException("SendExecutors.send $callId before sending task error,case:\n${e.message}\n payload = $payloadInfo", e)
            }
            DataReceivedDispatcher.postError(ime)
        }
        sendMsgQueue.getFirst { obj -> obj.callId == callId }?.apply {
            this.sendingUp = SendingUp.CANCEL
            this.onSendBefore?.clear()
            this.onSendBefore = null
            this.sendingState = SendMsgState.FAIL.setSpecialBody(payloadInfo)
        }
    }

    override fun onProgress(callId: String, progress: Int) {
        sendMsgQueue.getFirst { obj -> obj.callId == callId }?.apply {
            customSendingCallback?.let {
                it.onSendingUploading(progress, sendWithoutState, callId)
                if (it.pending) DataReceivedDispatcher.pushData(BaseMsgInfo.onProgressChange<T>(progress, callId))
            } ?: DataReceivedDispatcher.pushData(BaseMsgInfo.onProgressChange<T>(progress, callId))
        }
    }
}
