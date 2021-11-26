package com.zj.im.main.impl

import com.zj.im.chat.modle.BaseMsgInfo
import com.zj.im.sender.CustomSendingCallback
import com.zj.im.sender.OnSendBefore

internal interface RunnerClientStub<T> {

    /**
     * Actively send messages to ServerHub
     * @param data The message type defined by
     * @param callId represents the unique ID of this communication.
     * @param isResend Whether this transmission is a retransmission or not, this mark will affect all passes [IMInterface.addReceiveObserver] The result of the registered listener
     * @param isSpecialData This tag will make this message ignore and send detection
     * @param ignoreConnecting This tag will make this message ignore connection status detection (including device network connection)
     * @param sendBefore The processing protocol that the message can carry Process, it will run in an independent thread of IM and wait for execution in the queue
     * */
    fun send(data: T, callId: String, timeOut: Long, isResend: Boolean, isSpecialData: Boolean, ignoreConnecting: Boolean, sendBefore: OnSendBefore<T>?, customSendingCallback: CustomSendingCallback<T>?)

    /**
     * Add a pending entry to the IM processing queue
     * */
    fun <R> enqueue(data: BaseMsgInfo<R>)
}