package com.zj.imUi.interfaces

import android.content.Context

interface ImMsgIn {

    /**================================ msg bean converter ===================================*/

    fun getOwnerId(): Int?

    fun getGroupId(): Long

    fun getMsgId(): String

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

    fun getCCVideoContentImgPreviewRemoteStorageUrl(): String?
    fun getCCVideoContentVideoId(): String?
    fun getCCVideoContentVideoDescribe(): String?
    fun getCCVideoContentVideoTitle(): String?

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

    fun getLiveMsgId(): Long?
    fun getLiveMsgStatus(): Boolean?
    fun getLiveMsgCover(): String?
    fun getLiveMsgRoomId(): Int?
    fun getLiveMsgIntroduce(): String?
    fun getLiveMsgChannel(): String?
    fun getLiveMsgViewNum(): Int?

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


    fun getEmotionUrl():String?

    //获取敏感消息内容
    fun getExtSensitiveMsgContent(): String?

    //撤回消息角色
    fun getMsgRecallRole(): Int?

    //消息是否被撤回
    fun getMsgIsRecalled(): Boolean

    //消息是否包含敏感词
    fun getMsgIsSensitive(): Boolean

    //打赏是否被拒绝
    fun getMsgIsReject(): Boolean
    //打赏是否被拒绝,用于UI
    fun getMsgUIIsReject(): Boolean

    //是否是管理员
    fun getIsAdmin(): Boolean

    fun getRecallContent(context: Context): String?

    fun getRefuseContent(context: Context): String?



    /** ========================================= app interface ======================================== */


    fun getSelfUserId(): Int?

    fun isAudioPlaying(): Boolean

    fun playAudio()

    fun stopAudio()

    fun reply()

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

    //群主撤回用户消息
    fun ownerRecallGroupMsg()

    //群主拒绝回答
    fun rejectRewardMsg(id: String)

    //跳直播间
    fun jumpToLiveRoom()

    //举报
    fun reportGroupUserMsg(id: String)


    fun getUiTypeWithMessageType(): String

}