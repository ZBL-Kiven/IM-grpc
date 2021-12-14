package com.zj.ccIm.core.sender

import com.zj.ccIm.CcIM
import com.zj.ccIm.core.IMHelper
import com.zj.ccIm.core.MsgType
import com.zj.database.entity.*
import com.zj.im.chat.enums.SendMsgState

internal object Converter {

    fun exchangeMsgInfoBySendingInfo(sen: SendMessageReqEn, sendingState: SendMsgState = SendMsgState.NONE): MessageInfoEntity {
        val msg = MessageInfoEntity()
        msg.channelKey = sen.key
        msg.groupId = sen.groupId
        msg.msgType = sen.msgType
        msg.replyMsg = sen.replyMsg
        msg.clientMsgId = sen.clientMsgId
        msg.sendingState = sendingState.type
        msg.sendTime = System.currentTimeMillis()
        msg.sender = SenderInfo().apply {
            this.senderId = CcIM.imConfig?.getUserId() ?: 0
            this.senderAvatar = CcIM.imConfig?.getUserAvatar()
            this.senderName = CcIM.imConfig?.getUserName()
        }
        when (sen.msgType) {
            MsgType.EMOTION.type -> {
                msg.emotionMessage = EmotionMessage().apply {
                    this.id = sen.emotionMessage?.id ?: 0
                    this.emotionId = sen.emotionMessage?.emotionId ?: 0
                    this.url = sen.emotionMessage?.url
                }
            }
            MsgType.TEXT.type -> {
                msg.textContent = TextContent().apply {
                    this.text = sen.content
                }
            }
            MsgType.IMG.type -> {
                msg.imgContent = ImgContent().apply {
                    this.url = selectValidString(sen.url, sen.localFilePath)
                    this.width = sen.width
                    this.height = sen.height
                }
            }
            MsgType.AUDIO.type -> {
                msg.audioContent = AudioContent().apply {
                    this.url = selectValidString(sen.url, sen.localFilePath)
                    this.duration = sen.duration
                }
            }
            MsgType.VIDEO.type -> {
                msg.videoContent = VideoContent().apply {
                    this.url = selectValidString(sen.url, sen.localFilePath)
                    this.thumbnail = selectValidString(sen.url, sen.localFilePath)
                    this.duration = sen.duration
                    this.width = sen.width
                    this.height = sen.height
                }
            }
            MsgType.QUESTION.type -> {
                msg.questionContent = QuestionContent().apply {
                    this.diamond = sen.diamondNum ?: 0
                    this.published = sen.public
                    this.textContent = TextContent().apply {
                        this.text = sen.content
                    }
                }
            }
        }
        if (sen.key.isNotEmpty()) IMHelper.withDb {
            it.sendMsgDao().insertOrChange(sen)
        }
        return msg
    }

    private fun selectValidString(s1: String?, s2: String?, default: String? = null): String? {
        return if (s1.isNullOrEmpty()) (if (s2.isNullOrEmpty()) default else s2) else s1
    }
}