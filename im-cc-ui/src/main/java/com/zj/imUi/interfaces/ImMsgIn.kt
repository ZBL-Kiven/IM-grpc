package com.zj.imUi.interfaces

interface ImMsgIn {

    /**================================ msg bean converter ===================================*/

    fun getOwnerId(): Int?

    fun getGroupId(): Long

    fun getMsgId(): String

    fun getReplyMsgId(): Long?

    fun getSendingState(): Int

    fun getType(): String?

    fun getSendState(): Int

    fun getSenderId(): Int?

    fun getSenderName(): String?

    fun getSendTime(): Long

    fun getTextContent(): String?


    fun getSenderAvatar(): String?

    fun getImgContentUrl(): String?

    fun getImgContentWidth(): Int?

    fun getImgContentHeight(): Int?

    fun getAudioContentUrl(): String?

    fun getAudioContentDuration(): Long?

    //打赏相关
    fun getAnswerMsgType(): String?
    fun getQuestionContentType(): String?
    fun getDiamonds(): Int
    fun getExpireTime(): Long
    fun getSpark(): Int
    fun getPublished(): Boolean
    fun getQuestionId(): Int
    fun getQuestionStatus(): Int
    fun getQuestionTextContent(): String?
    fun getQuestionSendTime(): Long

    //回复相关
    fun getReplyMsgTextContent(): String?
    fun getReplyMsgClientMsgId(): String?
    fun getReplyMsgCreateTs(): Long?
    fun getReplyMsgGroupId(): Long?
    fun getReplyMsgMsgId(): Long?
    fun getReplyMsgType(): String?
    fun getReplyMsgOwnerId(): Int?

    fun getReplySendState(): Int
    fun getReplySenderId(): Int
    fun getReplySenderName(): String?

    /** ========================================= app interface ======================================== */

    fun getSelfUserId(): Int?

}