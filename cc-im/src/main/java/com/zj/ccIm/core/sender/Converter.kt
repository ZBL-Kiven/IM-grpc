package com.zj.ccIm.core.sender

import com.zj.ccIm.core.IMHelper
import com.zj.ccIm.core.MsgType
import com.zj.database.entity.*
import com.zj.im.chat.enums.SendMsgState

object Converter {

    fun exchangeMsgInfoBySendingInfo(sen: SendMessageReqEn, sendingState: SendMsgState = SendMsgState.NONE): MessageInfoEntity {
        val msg = MessageInfoEntity()
        msg.groupId = sen.groupId
        msg.msgType = sen.msgType
        msg.sendingState = sendingState.type
        msg.clientMsgId = sen.clientMsgId
        msg.replyMsg = sen.replyMsg
        msg.sender = SenderInfo().apply {
            this.senderId = IMHelper.imConfig.getUserId()
            this.senderAvatar = IMHelper.imConfig.getUserAvatar()
            this.senderName = IMHelper.imConfig.getUserName()
        }
        when (sen.msgType) {
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
        IMHelper.withDb {
            it?.sendMsgDao()?.insertOrChange(sen)
        }
        return msg
    }

    private fun selectValidString(s1: String?, s2: String?, default: String? = null): String? {
        return if (s1.isNullOrEmpty()) (if (s2.isNullOrEmpty()) default else s2) else s1
    }
}