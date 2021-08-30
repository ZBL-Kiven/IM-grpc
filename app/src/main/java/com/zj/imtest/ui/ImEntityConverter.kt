package com.zj.imtest.ui

import com.zj.database.entity.MessageInfoEntity
import com.zj.im.chat.enums.SendMsgState
import com.zj.imUi.interfaces.ImMsgIn
import com.zj.imtest.IMConfig

class ImEntityConverter(private val info: MessageInfoEntity?) : ImMsgIn {

    override fun getSendState(): Int {
        return info?.sendingState ?: SendMsgState.NONE.type
    }

    override fun getSenderId(): Int {
        return IMConfig.getUserId()
    }

    override fun getSenderName(): String? {
        return info?.sender?.senderName
    }

    override fun getTextContent(): String? {
        return info?.textContent?.text
    }

    override fun getSendTime(): Long {
        TODO("Not yet implemented")
    }

    override fun getOwnerId(): Int? {
        TODO("Not yet implemented")
    }

    override fun getGroupId(): Long {
        TODO("Not yet implemented")
    }

    override fun getMsgId(): String {
        TODO("Not yet implemented")
    }

    override fun getReplyId(): Int? {
        TODO("Not yet implemented")
    }

    override fun getReplyMsgId(): Long? {
        TODO("Not yet implemented")
    }

    override fun getSendingState(): Int {
        TODO("Not yet implemented")
    }

    override fun getType(): String? {
        return info?.msgType
    }

    override fun getSenderAvatar(): String? {
        return info?.sender?.senderAvatar
    }

    override fun getImgContentUrl(): String? {
        TODO("Not yet implemented")
    }

    override fun getImgContentWidth(): Int? {
        TODO("Not yet implemented")
    }

    override fun getImgContentHeight(): Int? {
        TODO("Not yet implemented")
    }

    override fun getAudioContentUrl(): String? {
        TODO("Not yet implemented")
    }

    override fun getAudioContentDuration(): Long? {
        TODO("Not yet implemented")
    }

    override fun getAnswerMsgType(): String? {
        TODO("Not yet implemented")
    }

    override fun getQuestionContentType(): String? {
        TODO("Not yet implemented")
    }

    override fun getDiamonds(): Int {
        TODO("Not yet implemented")
    }

    override fun getExpireTime(): Long {
        TODO("Not yet implemented")
    }

    override fun getSpark(): Int {
        TODO("Not yet implemented")
    }

    override fun getPublished(): Boolean {
        TODO("Not yet implemented")
    }

    override fun getQuestionId(): Int {
        TODO("Not yet implemented")
    }

    override fun getQuestionStatus(): Int {
        TODO("Not yet implemented")
    }

    override fun getQuestionTextContent(): String? {
        TODO("Not yet implemented")
    }

    override fun getQuestionSendTime(): Long {
        TODO("Not yet implemented")
    }

    override fun getReplyMsgContent(): MessageReplyIn? {
        TODO("Not yet implemented")
    }

    override fun getReplyMsgTextContent(): String? {
        TODO("Not yet implemented")
    }

    override fun getReplyMsgClientMsgId(): String? {
        TODO("Not yet implemented")
    }

    override fun getReplyMsgCreateTs(): Long? {
        TODO("Not yet implemented")
    }

    override fun getReplyMsgGroupId(): Long? {
        TODO("Not yet implemented")
    }

    override fun getReplyMsgMsgId(): Long? {
        TODO("Not yet implemented")
    }

    override fun getReplyMsgType(): String? {
        TODO("Not yet implemented")
    }

    override fun getReplyMsgOwnerId(): Int? {
        TODO("Not yet implemented")
    }

    override fun getReplySendState(): Int {
        TODO("Not yet implemented")
    }

    override fun getReplySenderId(): Int {
        TODO("Not yet implemented")
    }

    override fun getReplySenderName(): String? {
        TODO("Not yet implemented")
    }


}