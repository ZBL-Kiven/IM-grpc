package com.zj.ccIm.core.sender

import java.util.*

@Suppress("unused")
class SendMsgConfig internal constructor(internal val fromCustom: Boolean = false) {

    internal var callId = UUID.randomUUID().toString()
    internal var sendConditionCheck = false
    internal var connectionStateCheck = false

    fun customCallId(callId: String) {
        this.callId = callId
    }

    fun ignoreSendConditionCheck(b: Boolean) {
        this.sendConditionCheck = b
    }

    fun ignoreConnectionStateCheck(b: Boolean) {
        this.connectionStateCheck = b
    }

    fun build(): MsgSender {
        return MsgSender(this)
    }

}