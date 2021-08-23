package com.zj.im.sender

import com.zj.im.chat.modle.BaseMsgInfo
import com.zj.im.chat.modle.SendingUp
import com.zj.im.main.dispatcher.DataReceivedDispatcher
import com.zj.im.utils.cusListOf

/**
 * Created by ZJJ
 */
internal class SendingPool<T>(private val onStateChange: OnStatus<T>) {

    private var sending = false

    private val sendMsgQueue = cusListOf<BaseMsgInfo<T>>()

    fun setSendState(state: SendingUp, isFinish: Boolean, callId: String, data: T? = null) {
        sendMsgQueue.getFirst { obj -> obj.callId == callId }?.apply {
            this.sendingUp = state
            this.data = data
            if (isFinish) this.onSendBefore = null else DataReceivedDispatcher.pushData(this)
        }
    }

    fun push(info: BaseMsgInfo<T>) {
        sendMsgQueue.add(info)
        if (info.onSendBefore != null) info.onSendBefore?.call(onStateChange)
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
        sendMsgQueue.getFirst { it.ignoreConnecting }?.let {
            return it
        }
        if (!DataReceivedDispatcher.isDataEnable()) {
            sendMsgQueue.forEach {
                it.joinInTop = true
                DataReceivedDispatcher.pushData(it)
            }
            sendMsgQueue.clear()
            return null
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
}
