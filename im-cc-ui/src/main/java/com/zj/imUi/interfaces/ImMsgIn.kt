package com.zj.imUi.interfaces

interface ImMsgIn {

    /**================================ msg bean converter ===================================*/

    fun getOwnerId(): Int?

    fun getGroupId(): Long

    fun getMsgId(): String

    fun getReplyMsgId(): Long?

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

    fun getCCVideoContentImgPreviewRemoteStorageUrl():String?
    fun getCCVideoContentVideoId():String?
    fun getCCVideoContentVideoDescribe():String?
    fun getCCVideoContentVideoTitle():String?

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
    fun getReplyMsgImgContent(): String?
    fun getReplyMsgImgWidth(): Int?
    fun getReplyMsgImgHeight(): Int?
    fun getReplyMsgCCVideoCoverContent(): String?
    fun getReplyMsgCCVideoId(): String?
    fun getReplyMsgClientMsgId(): String?
    fun getReplyMsgCreateTs(): Long?
    fun getReplyMsgGroupId(): Long?
    fun getReplyMsgMsgId(): Long?
    fun getReplyMsgType(): String?
    fun getReplyMsgOwnerId(): Int?

    fun getReplySendState(): Int
    fun getReplySenderId(): Int
    fun getReplySenderName(): String?

    fun getReplyMsgQuestionContent(): String?
    fun getReplyMsgQuestionSpark(): Int?
    fun getReplyMsgQuestionIsPublished(): Boolean?

    fun getAnswerContentMsgType(): String?
    fun getAnswerContentSendTime(): Long?
    fun getAnswerContentSenderName(): String?
    fun getAnswerContentSenderId(): Int?
    fun getAnswerContentSenderAvatar(): String?
    fun getAnswerContentTextContent(): String?
    fun getAnswerContentImgContentUrl(): String?
    fun getAnswerContentImgContentWidth(): Int?
    fun getAnswerContentImgContentHeight(): Int?
    fun getAnswerContentAudioContentUrl(): String?
    fun getAnswerContentAudioContentDuration(): Long?


    /** ========================================= app interface ======================================== */


    fun getSelfUserId(): Int?

    fun isAudioPlaying(): Boolean

    fun playAudio()

    fun stopAudio()

    fun reply(id: String)

    fun block(userId: Int)

    fun resend()

    fun onReplyQuestion()

    fun onViewLargePic()

    fun jumpToVideoDetails()

    fun jumpToUserHomePage()

    fun jumpToSenderRewardsPage()

    fun questionStatusOverdueChange()

    //用户撤回
    fun userRetractRewardMsg()
    //删除发送失败的信息
    fun deleteSendLossMsg()
}