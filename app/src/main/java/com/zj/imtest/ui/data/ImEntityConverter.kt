package com.zj.imtest.ui.data

import android.util.Log
import com.zj.ccIm.core.IMHelper
import com.zj.database.entity.MessageInfoEntity
import com.zj.im.chat.enums.SendMsgState
import com.zj.imUi.interfaces.ImMsgIn
import com.zj.imtest.IMConfig

abstract class ImEntityConverter(val info: MessageInfoEntity?) : ImMsgIn {


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
        return info?.textContent?.text //        return "this is a normal message ???so what should i expression,emmm,dadada biubiubiubh h hg jj"
    }

    override fun getType(): String? { //        return "cc_video"
        //        return "audio"
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
        return null
    }

    override fun getCCVideoContentVideoDescribe(): String? {
        return null
    }

    override fun getCCVideoContentVideoTitle(): String? {
        return null
    }

    override fun getAnswerMsgType(): String? {
        return info?.questionContent?.answerMsgType
    }


    override fun getQuestionContentType(): String? {
        return info?.questionContent?.contentType
    }

    override fun getDiamonds(): Int {
        return info?.questionContent?.diamond ?: 0
    }

    override fun getExpireTime(): Long { //        return 3600000
        return info?.questionContent?.expireTime ?: 0 //        return  0
        //        return  if (a == 0) 60000
        //        else 36600000
    }


    override fun getSpark(): Int {
        return info?.questionContent?.spark ?: 0 //        return 10000
    }


    override fun getPublished(): Boolean {
        return info?.questionContent?.published ?: true //            return  false
    }

    override fun getQuestionId(): Int {
        return info?.questionContent?.questionId ?: 0
    }

    override fun getQuestionStatus(): Int {
        return 0 //        return info?.questionContent?.questionStatus ?: -2
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

    override fun getReplyMsgCCVideoCoverContent(): String? { //                return "https://img1.baidu.com/it/u=2057590437,3282076992&fm=26&fmt=auto"
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

    override fun getReplyMsgType(): String? { //        return "cc_video"
        return info?.replyMsg?.msgType
    }


    override fun getReplyMsgClientMsgId(): String? { //        return "asfaewdw"
        return info?.replyMsg?.clientMsgId
    }

    override fun getReplySenderId(): Int {
        return info?.replyMsg?.sender?.senderId ?: 0
    }

    override fun getReplySenderName(): String? { //        return  "123"
        return info?.replyMsg?.sender?.senderName
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

    override fun getAnswerContentMsgType(): String? {
        return "text"
    }

    override fun getAnswerContentSendTime(): Long? {
        return 170000000
    }

    override fun getAnswerContentSenderName(): String? {
        return "亚尔总"
    }

    override fun getAnswerContentSenderId(): Int? {
        return 2222
    }

    override fun getAnswerContentSenderAvatar(): String? {
        return null
    }

    override fun getAnswerContentTextContent(): String? {
        return "回答文本"
    }

    override fun getAnswerContentImgContentUrl(): String? {
        return null
    }

    override fun getAnswerContentImgContentWidth(): Int? {
        return 400
    }

    override fun getAnswerContentImgContentHeight(): Int? {
        return 400
    }

    override fun getAnswerContentAudioContentUrl(): String? {
        return null
    }

    override fun getAnswerContentAudioContentDuration(): Long? {
        return null
    }

    override fun getMsgIsRecalled(): Boolean? {
        return false
    }

    override fun getMsgIsSensitive(): Boolean? {
        return false
    }

    override fun getMsgIsReject(): Boolean? {
        return false
    }

    override fun getIsAdmin(): Boolean? {
        return false
    }

    override fun getSelfUserId(): Int {
        return IMConfig.getUserId()
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