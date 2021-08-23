package com.zj.ccIm.core.sender

import com.zj.ccIm.core.IMHelper
import com.zj.database.entity.SendMessageReqEn
import java.util.*

object Sender {

    fun sendText(content: String, groupId: Long) {
        val callId = UUID.randomUUID().toString()
        val sen = SendMessageReqEn()
        sen.content = content
        sen.msgType = "text"
        sen.groupId = groupId
        sen.clientMsgId = callId
        IMHelper.resend(sen, callId, 3000, isSpecialData = false, ignoreConnecting = false, sendBefore = null)
    }

    fun resendText(info: SendMessageReqEn) {
        IMHelper.send(info, info.clientMsgId, 3000, isSpecialData = false, ignoreConnecting = false, sendBefore = null)
    }


}