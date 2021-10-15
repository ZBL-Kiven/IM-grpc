package com.zj.ccIm.core.sender

import com.zj.ccIm.core.Constance
import com.zj.ccIm.core.IMHelper
import com.zj.ccIm.core.MsgType
import com.zj.database.entity.MessageInfoEntity
import com.zj.database.entity.SendMessageReqEn
import com.zj.im.sender.OnSendBefore

@Suppress("unused")
open class MsgSender internal constructor() {

    private lateinit var config: SendMsgConfig

    internal fun createConfig(config: SendMsgConfig): MsgSender {
        this.config = config
        return this
    }

    fun sendRewardTextMsg(content: String, groupId: Long, diamondNum: Int = 0, rewardMsgType: MsgType, isPublic: Boolean): String {
        val sen = SendMessageReqEn()
        sen.content = content
        sen.msgType = MsgType.QUESTION.type
        sen.groupId = groupId
        sen.clientMsgId = config.callId
        sen.answerMsgType = rewardMsgType.type
        sen.diamondNum = diamondNum
        sen.public = isPublic
        setRetryProp(sen)
        send(sen)
        return config.callId
    }

    fun sendText(content: String, groupId: Long, replyMsg: MessageInfoEntity? = null): String {
        val sen = SendMessageReqEn()
        sen.content = content
        sen.msgType = MsgType.TEXT.type
        sen.groupId = groupId
        sen.clientMsgId = config.callId
        sen.replyMsg = replyMsg
        setRetryProp(sen)
        send(sen)
        return config.callId
    }

    fun sendUrlImg(url: String, width: Int, height: Int, groupId: Long, replyMsg: MessageInfoEntity? = null): String {
        val sen = SendMessageReqEn()
        sen.url = url
        sen.clientMsgId = url
        sen.msgType = MsgType.IMG.type
        sen.groupId = groupId
        sen.clientMsgId = config.callId
        sen.width = width
        sen.height = height
        sen.replyMsg = replyMsg
        setRetryProp(sen)
        send(sen)
        return config.callId
    }

    fun sendImg(filePath: String, width: Int, height: Int, groupId: Long, replyMsg: MessageInfoEntity? = null): String {
        val sen = SendMessageReqEn()
        sen.localFilePath = filePath
        sen.msgType = MsgType.IMG.type
        sen.groupId = groupId
        sen.clientMsgId = config.callId
        sen.width = width
        sen.height = height
        sen.replyMsg = replyMsg
        setRetryProp(sen)
        send(sen)
        return config.callId
    }

    fun sendAudio(filePath: String, duration: Long, groupId: Long, replyMsg: MessageInfoEntity? = null): String {
        val sen = SendMessageReqEn()
        sen.localFilePath = filePath
        sen.msgType = MsgType.AUDIO.type
        sen.groupId = groupId
        sen.clientMsgId = config.callId
        sen.duration = duration
        sen.replyMsg = replyMsg
        setRetryProp(sen)
        send(sen)
        return config.callId
    }

    fun sendUrlAudio(url: String, duration: Long, groupId: Long, replyMsg: MessageInfoEntity? = null): String {
        val sen = SendMessageReqEn()
        sen.url = url
        sen.msgType = MsgType.AUDIO.type
        sen.groupId = groupId
        sen.clientMsgId = config.callId
        sen.duration = duration
        sen.replyMsg = replyMsg
        setRetryProp(sen)
        send(sen)
        return config.callId
    }

    fun sendVideo(filePath: String, width: Int, height: Int, duration: Long, groupId: Long, replyMsg: MessageInfoEntity? = null): String {
        val sen = SendMessageReqEn()
        sen.localFilePath = filePath
        sen.msgType = MsgType.VIDEO.type
        sen.groupId = groupId
        sen.clientMsgId = config.callId
        sen.width = width
        sen.height = height
        sen.duration = duration
        sen.replyMsg = replyMsg
        setRetryProp(sen)
        send(sen)
        return config.callId
    }

    fun sendUrlVideo(url: String, width: Int, height: Int, duration: Long, groupId: Long, replyMsg: MessageInfoEntity? = null): String {
        val sen = SendMessageReqEn()
        sen.url = url
        sen.msgType = MsgType.VIDEO.type
        sen.groupId = groupId
        sen.clientMsgId = config.callId
        sen.width = width
        sen.height = height
        sen.duration = duration
        sen.replyMsg = replyMsg
        setRetryProp(sen)
        send(sen)
        return config.callId
    }

    fun resendMessage(clientId: String) {
        IMHelper.withDb {
            it.sendMsgDao().findByCallId(clientId)
        }?.let { resendMessage(it) }
    }

    internal fun resendMessage(info: SendMessageReqEn) {
        val sendBefore = if (MsgType.hasUploadType(info.msgType)) {
            if (!info.url.isNullOrEmpty()) null else FileSender(info)
        } else null
        send(info, sendBefore)
    }

    private fun setRetryProp(sen: SendMessageReqEn) {
        sen.ignoreConnectionState = config.connectionStateCheck
        sen.ignoreSendConditionState = config.sendConditionCheck
        sen.autoRetryResend = !config.fromCustom && MsgType.canRetryType(sen.msgType)
        sen.autoResendWhenBootStart = !config.fromCustom && MsgType.canRetryWhenBootStartType(sen.msgType)
    }

    private fun send(sen: SendMessageReqEn, sendBefore: OnSendBefore<Any?>? = FileSender.getIfSupport(sen)) {
        IMHelper.sendMsgWithChannel(sen, sen.clientMsgId, Constance.SEND_MSG_DEFAULT_TIMEOUT, isSpecialData = sen.ignoreSendConditionState, ignoreConnecting = sen.ignoreConnectionState, sendBefore = sendBefore)
    }
}