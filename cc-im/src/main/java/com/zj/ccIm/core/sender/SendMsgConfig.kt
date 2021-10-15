package com.zj.ccIm.core.sender

import java.util.*

@Suppress("unused")
class SendMsgConfig internal constructor(internal val fromCustom: Boolean = false) {

    internal var callId = UUID.randomUUID().toString()
    internal var sendConditionCheck = false
    internal var connectionStateCheck = false

    fun customCallId(callId: String): SendMsgConfig {
        this.callId = callId
        return this
    }

    fun ignoreSendConditionCheck(b: Boolean): SendMsgConfig {
        this.sendConditionCheck = b
        return this
    }

    fun ignoreConnectionStateCheck(b: Boolean): SendMsgConfig {
        this.connectionStateCheck = b
        return this
    }

    fun build(): MsgSender {
        return MsgSender().createConfig(this)
    }

}