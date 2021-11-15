package com.zj.imtest.ui

import android.util.Log
import com.zj.ccIm.core.IMHelper
import com.zj.database.entity.MessageInfoEntity
import com.zj.im.chat.enums.SendMsgState
import com.zj.imUi.interfaces.ImMsgIn
import com.zj.imtest.IMConfig

class ImEntityConverter(private val info: MessageInfoEntity?) : ImMsgIn {


    override fun getOwnerId(): Int? {
        return info?.ownerId
    }

    override fun getGroupId(): Long {
        return info?.groupId ?: 0
    }

    override fun getMsgId(): String {
        return info?.clientMsgId ?: ""
    }


    override fun getSendState(): Int {
        return info?.sendingState ?: SendMsgState.NONE.type
    }

    override fun getSenderId(): Int? {
        return info?.sender?.senderId
    }

    override fun getSenderName(): String? {
        return info?.sender?.senderName
    }

    override fun getSendTime(): Long {
        return info?.sendTime ?: 0
    }

    override fun getTextContent(): String? {
        return info?.textContent?.text
    }

    override fun getType(): String? {
        return info?.msgType
    }

    override fun getSenderAvatar(): String? {
        return info?.sender?.senderAvatar
    }

    override fun getImgContentUrl(): String? {
        return info?.imgContent?.url
    }

    override fun getImgContentWidth(): Int? {
        return info?.imgContent?.width
    }

    override fun getImgContentHeight(): Int? {
        return info?.imgContent?.height
    }

    override fun getAudioContentUrl(): String? {
        return info?.audioContent?.url
    }

    override fun getAudioContentDuration(): Long? {
        return info?.audioContent?.duration
    }


    override fun getCCVideoContentImgPreviewRemoteStorageUrl(): String? {
        return info?.ccVideoContent?.imgPreviewRemoteStorageUrl

    }

    override fun getCCVideoContentVideoId(): String? {
        return info?.ccVideoContent?.videoId
    }

    override fun getCCVideoContentVideoDescribe(): String? {
        return info?.ccVideoContent?.videoDescribe
    }

    override fun getCCVideoContentVideoTitle(): String? {
        return info?.ccVideoContent?.videoTitle
    }


    //打赏相关

    override fun getAnswerMsgType(): String? {
        return info?.questionContent?.answerMsgType
    }


    override fun getQuestionContentType(): String? {
        return info?.questionContent?.contentType
    }

    override fun getDiamonds(): Int {
        return info?.questionContent?.diamond ?: 0
    }

    override fun getExpireTime(): Long {
        return info?.questionContent?.expireTime ?: 0
    }


    override fun getSpark(): Int {
        return info?.questionContent?.spark ?: 0
    }


    override fun getPublished(): Boolean {
        return info?.questionContent?.published ?: true
    }

    override fun getQuestionId(): Int {
        return info?.questionContent?.questionId ?: 0
    }

    override fun getQuestionStatus(): Int {
        return info?.questionContent?.questionStatus ?: 0
    }

    override fun getQuestionTextContent(): String? {
        return info?.questionContent?.textContent?.text
    }

    override fun getQuestionSendTime(): Long {
        return info?.questionContent?.sendTime ?: 0
    }


    override fun getReplyMsgTextContent(): String? {
        return info?.replyMsg?.textContent?.text
    }

    override fun getReplyMsgCCVideoCoverContent(): String? {
        return info?.replyMsg?.ccVideoContent?.imgPreviewRemoteStorageUrl
    }

    override fun getReplyMsgCCVideoId(): String? {
        return info?.replyMsg?.ccVideoContent?.videoId
    }

    override fun getReplyMsgImgContent(): String? {
        return info?.replyMsg?.imgContent?.url
    }

    override fun getReplyMsgImgWidth(): Int? {
        return info?.replyMsg?.imgContent?.width
    }

    override fun getReplyMsgImgHeight(): Int? {
        return info?.replyMsg?.imgContent?.height
    }

    override fun getReplyMsgType(): String? {
        return info?.replyMsg?.msgType
    }


    override fun getReplyMsgClientMsgId(): String? {
        return info?.replyMsg?.clientMsgId
    }

    override fun getReplySenderId(): Int {
        return info?.replyMsg?.sender?.senderId ?: 0
    }

    override fun getReplySenderName(): String? {
      return  info?.replyMsg?.sender?.senderName
    }

    override fun getReplyMsgCreateTs(): Long? {
        return info?.replyMsg?.sendTime
    }

    override fun getReplyMsgGroupId(): Long? {
        return info?.replyMsg?.groupId
    }

    override fun getReplyMsgMsgId(): Long? {
        return info?.replyMsg?.msgId
    }

    override fun getReplyMsgOwnerId(): Int? {
        return info?.replyMsg?.ownerId
    }

    override fun getReplySendState(): Int {
        return info?.replyMsg?.sendingState ?: -2
    }


    override fun getReplyMsgQuestionContent(): String? {
        return info?.replyMsg?.questionContent?.textContent?.text
    }

    override fun getReplyMsgQuestionSpark(): Int? {
        return info?.replyMsg?.questionContent?.spark
    }

    override fun getReplyMsgQuestionIsPublished(): Boolean? {
        return info?.replyMsg?.questionContent?.published
    }

    override fun getLiveMsgId(): Long? {
        return info?.liveMessage?.id
    }

    override fun getLiveMsgStatus(): Boolean? {
        return info?.liveMessage?.status
    }

    override fun getLiveMsgCover(): String? {
        return info?.liveMessage?.cover
    }

    override fun getLiveMsgRoomId(): Int? {
        return info?.liveMessage?.roomId
    }

    override fun getLiveMsgIntroduce(): String? {
        return info?.liveMessage?.introduce
    }

    override fun getLiveMsgChannel(): String? {
        return info?.liveMessage?.channelId
    }

    override fun getLiveMsgViewNum(): Int? {
        return info?.liveMessage?.viewNum
    }

    override fun getAnswerContentAudioContentDuration(): Long? {
        return info?.answerMsg?.audioContent?.duration
    }

    override fun getAnswerContentAudioContentUrl(): String? {
        return info?.answerMsg?.audioContent?.url
    }

    override fun getAnswerContentImgContentHeight(): Int? {
        return info?.answerMsg?.imgContent?.height
    }

    override fun getAnswerContentImgContentUrl(): String? {
        return info?.answerMsg?.imgContent?.url
    }

    override fun getAnswerContentImgContentWidth(): Int? {
        return info?.answerMsg?.imgContent?.width
    }

    override fun getAnswerContentMsgType(): String? {
        return info?.answerMsg?.msgType
    }

    override fun getAnswerContentSendTime(): Long? {
        return info?.answerMsg?.sendTime
    }

    override fun getAnswerContentSenderAvatar(): String? {
        return info?.answerMsg?.sender?.senderAvatar
    }

    override fun getAnswerContentSenderId(): Int? {
        return info?.answerMsg?.sender?.senderId
    }

    override fun getAnswerContentSenderName(): String? {
        return info?.answerMsg?.sender?.senderName
    }

    override fun getAnswerContentTextContent(): String? {
        return info?.answerMsg?.textContent?.text
    }

    override fun getSelfUserId(): Int {
        return IMConfig.getUserId()
    }


    override fun getMsgIsReject(): Boolean {
        return info?.questionContent?.questionStatus == 3
    }


    //未完成
    override fun getMsgIsRecalled(): Boolean? {
        return false
    }

    override fun getMsgRecallRole(): Int? {
        return 1
    }

    override fun getMsgIsSensitive(): Boolean? {
        return false
    }

    override fun getExtSensitiveMsgContent(): String? {
        return "敏感消息内容"
    }

    override fun getIsAdmin(): Boolean? {
        return false
    }


    /** ==================================================== 主动数据接口 ⬇️ ======================================================*/

    override fun isAudioPlaying(): Boolean {
        return true
    }

    override fun playAudio() {
        Log.e("----- ", " playAudio")
    }

    override fun stopAudio() {
        Log.e("----- ", " stopAudio")
    }

    override fun reply(id: String) {
        Log.e("----- ", " reply")
    }

    override fun block(userId: Int) {
        Log.e("----- ", " block")
    }

    override fun resend() {
        IMHelper.Sender.resendMessage(getMsgId())
    }

    override fun onReplyQuestion() {
        IMHelper.Sender.sendText("ok thanks", getGroupId(), info)
    }

    override fun onViewLargePic() {
    }

    override fun jumpToVideoDetails() {
    }


    override fun jumpToUserHomePage() {

    }

    override fun jumpToSenderRewardsPage() {
    }

    override fun questionStatusOverdueChange() {
        info?.questionContent?.questionStatus = 2
    }

    override fun userRetractRewardMsg() {
    }

    override fun deleteSendLossMsg() {
    }


    override fun ownerRecallGroupMsg() {
    }

    override fun rejectRewardMsg(id: String) {
    }



    override fun jumpToLiveRoom() {
    }

    override fun reportGroupUserMsg(id: String) {
    }
}