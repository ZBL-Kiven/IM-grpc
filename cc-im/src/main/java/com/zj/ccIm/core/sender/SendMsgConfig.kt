package com.zj.ccIm.core.sender

import com.zj.ccIm.core.sender.exchange.CustomSendListener
import java.util.*

@Suppress("unused")
class SendMsgConfig internal constructor(internal val fromCustom: Boolean = false) {

    internal var callId = UUID.randomUUID().toString()
    internal var sendWithoutState = false
    internal var sendConditionCheck = false
    internal var connectionStateCheck = false
    internal var customSendListener: CustomSendListener<*>? = null

    fun sendWithoutState(): SendMsgConfig {
        sendWithoutState = true
        return this
    }

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

    fun <O, E : CustomSendListener<O>> setCustomSendCallback(customSendListener: E?): SendMsgConfig {
        this.customSendListener = customSendListener
        return this
    }

    fun build(): MsgSender {
        return MsgSender(this)
    }

}