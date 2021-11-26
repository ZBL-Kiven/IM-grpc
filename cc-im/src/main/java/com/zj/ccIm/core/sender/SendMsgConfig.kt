package com.zj.ccIm.core.sender

import com.zj.im.sender.CustomSendingCallback
import java.util.*

@Suppress("unused")
class SendMsgConfig internal constructor(internal val fromCustom: Boolean = false) {

    internal var callId = UUID.randomUUID().toString()
    internal var sendConditionCheck = false
    internal var connectionStateCheck = false
    internal var customSendCallback: CustomSendingCallback<Any?>? = null

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

    fun setCustomSendCallback(customSendCallback: CustomSendingCallback<Any?>?): SendMsgConfig {
        this.customSendCallback = customSendCallback
        return this
    }

    fun build(): MsgSender {
        return MsgSender(this)
    }

}