package com.zj.ccIm.core.sender

import com.zj.ccIm.core.Constance
import com.zj.ccIm.core.IMHelper
import com.zj.ccIm.core.MsgType
import com.zj.database.entity.MessageInfoEntity
import com.zj.database.entity.SendMessageReqEn
import java.util.*

@Suppress("unused")
object Sender {

    fun sendRewardTextMsg(content: String, groupId: Long, diamondNum: Int = 0, rewardMsgType: MsgType, isPublic: Boolean, callId: String = UUID.randomUUID().toString()) {
        val sen = SendMessageReqEn()
        sen.content = content
        sen.msgType = MsgType.QUESTION.type
        sen.groupId = groupId
        sen.clientMsgId = callId
        sen.answerMsgType = rewardMsgType.type
        sen.diamondNum = diamondNum
        sen.public = isPublic
        sen.autoRetryResend = MsgType.canRetryType(sen.msgType)
        sen.autoResendWhenBootStart = MsgType.canRetryWhenBootStartType(sen.msgType)
        IMHelper.send(sen, callId, Constance.SEND_MSG_DEFAULT_TIMEOUT, isSpecialData = false, ignoreConnecting = false, sendBefore = null)
    }

    fun sendText(content: String, groupId: Long, replyMsg: MessageInfoEntity? = null, callId: String = UUID.randomUUID().toString()) {
        val sen = SendMessageReqEn()
        sen.content = content
        sen.msgType = MsgType.TEXT.type
        sen.groupId = groupId
        sen.clientMsgId = callId
        sen.replyMsg = replyMsg
        sen.autoRetryResend = MsgType.canRetryType(sen.msgType)
        sen.autoResendWhenBootStart = MsgType.canRetryWhenBootStartType(sen.msgType)
        IMHelper.send(sen, callId, Constance.SEND_MSG_DEFAULT_TIMEOUT, isSpecialData = false, ignoreConnecting = false, sendBefore = null)
    }

    fun sendUrlImg(url: String, width: Int, height: Int, groupId: Long, replyMsg: MessageInfoEntity? = null, callId: String = UUID.randomUUID().toString()) {
        val sen = SendMessageReqEn()
        sen.url = url
        sen.clientMsgId = url
        sen.msgType = MsgType.IMG.type
        sen.groupId = groupId
        sen.clientMsgId = callId
        sen.width = width
        sen.height = height
        sen.replyMsg = replyMsg
        sen.autoRetryResend = MsgType.canRetryType(sen.msgType)
        sen.autoResendWhenBootStart = MsgType.canRetryWhenBootStartType(sen.msgType)
        IMHelper.send(sen, callId, Constance.SEND_MSG_DEFAULT_TIMEOUT, isSpecialData = false, ignoreConnecting = false, sendBefore = null)
    }

    fun sendImg(filePath: String, width: Int, height: Int, groupId: Long, replyMsg: MessageInfoEntity? = null, callId: String = UUID.randomUUID().toString()) {
        val sen = SendMessageReqEn()
        sen.localFilePath = filePath
        sen.msgType = MsgType.IMG.type
        sen.groupId = groupId
        sen.clientMsgId = callId
        sen.width = width
        sen.height = height
        sen.replyMsg = replyMsg
        sen.autoRetryResend = MsgType.canRetryType(sen.msgType)
        sen.autoResendWhenBootStart = MsgType.canRetryWhenBootStartType(sen.msgType)
        IMHelper.send(sen, callId, Constance.SEND_MSG_DEFAULT_TIMEOUT, isSpecialData = false, ignoreConnecting = false, sendBefore = FileSender(sen))
    }

    fun sendAudio(filePath: String, duration: Long, groupId: Long, replyMsg: MessageInfoEntity? = null, callId: String = UUID.randomUUID().toString()) {
        val sen = SendMessageReqEn()
        sen.localFilePath = filePath
        sen.msgType = MsgType.AUDIO.type
        sen.groupId = groupId
        sen.clientMsgId = callId
        sen.duration = duration
        sen.replyMsg = replyMsg
        sen.autoRetryResend = MsgType.canRetryType(sen.msgType)
        sen.autoResendWhenBootStart = MsgType.canRetryWhenBootStartType(sen.msgType)
        IMHelper.send(sen, callId, Constance.SEND_MSG_DEFAULT_TIMEOUT, isSpecialData = false, ignoreConnecting = false, sendBefore = FileSender(sen))
    }

    fun sendUrlAudio(url: String, duration: Long, groupId: Long, replyMsg: MessageInfoEntity? = null, callId: String = UUID.randomUUID().toString()) {
        val sen = SendMessageReqEn()
        sen.url = url
        sen.msgType = MsgType.AUDIO.type
        sen.groupId = groupId
        sen.clientMsgId = callId
        sen.duration = duration
        sen.replyMsg = replyMsg
        sen.autoRetryResend = MsgType.canRetryType(sen.msgType)
        sen.autoResendWhenBootStart = MsgType.canRetryWhenBootStartType(sen.msgType)
        IMHelper.send(sen, callId, Constance.SEND_MSG_DEFAULT_TIMEOUT, isSpecialData = false, ignoreConnecting = false, sendBefore = FileSender(sen))
    }

    fun sendVideo(filePath: String, width: Int, height: Int, duration: Long, groupId: Long, replyMsg: MessageInfoEntity? = null, callId: String = UUID.randomUUID().toString()) {
        val sen = SendMessageReqEn()
        sen.localFilePath = filePath
        sen.msgType = MsgType.VIDEO.type
        sen.groupId = groupId
        sen.clientMsgId = callId
        sen.width = width
        sen.height = height
        sen.duration = duration
        sen.replyMsg = replyMsg
        sen.autoRetryResend = MsgType.canRetryType(sen.msgType)
        sen.autoResendWhenBootStart = MsgType.canRetryWhenBootStartType(sen.msgType)
        IMHelper.send(sen, callId, Constance.SEND_MSG_DEFAULT_TIMEOUT, isSpecialData = false, ignoreConnecting = false, sendBefore = null)
    }

    fun sendUrlVideo(url: String, width: Int, height: Int, duration: Long, groupId: Long, replyMsg: MessageInfoEntity? = null, callId: String = UUID.randomUUID().toString()) {
        val sen = SendMessageReqEn()
        sen.url = url
        sen.msgType = MsgType.VIDEO.type
        sen.groupId = groupId
        sen.clientMsgId = callId
        sen.width = width
        sen.height = height
        sen.duration = duration
        sen.replyMsg = replyMsg
        sen.autoRetryResend = MsgType.canRetryType(sen.msgType)
        sen.autoResendWhenBootStart = MsgType.canRetryWhenBootStartType(sen.msgType)
        IMHelper.send(sen, callId, Constance.SEND_MSG_DEFAULT_TIMEOUT, isSpecialData = false, ignoreConnecting = false, sendBefore = null)
    }

    fun resendMessage(clientId: String) {
        IMHelper.withDb {
            it?.sendMsgDao()?.findByCallId(clientId)
        }?.let { resendMessage(it) }
    }

    internal fun resendMessage(info: SendMessageReqEn) {
        val sendBefore = if (MsgType.hasUploadType(info.msgType)) {
            if (!info.url.isNullOrEmpty()) null else FileSender(info)
        } else null
        info.autoRetryResend = MsgType.canRetryType(info.msgType)
        info.autoResendWhenBootStart = MsgType.canRetryWhenBootStartType(info.msgType)
        IMHelper.resend(info, info.clientMsgId, Constance.SEND_MSG_DEFAULT_TIMEOUT, isSpecialData = false, ignoreConnecting = false, sendBefore = sendBefore)
    }
}