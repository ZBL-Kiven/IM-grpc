package com.zj.imtest.ui

import android.util.Log
import com.zj.ccIm.core.IMHelper
import com.zj.ccIm.core.sender.Sender
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

    override fun getReplyMsgId(): Long? {
        return info?.replyMsgId
    }

    override fun getSendingState(): Int {
        return info?.sendingState ?: 0
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
        return ""
    }

    override fun getCCVideoContentVideoId(): String? {
        return ""
    }

    override fun getCCVideoContentVideoDescribe(): String? {
        return ""
    }

    override fun getCCVideoContentVideoTitle(): String? {
        return ""
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

    override fun getReplyMsgImgContent(): String? {
        return null
    }

    override fun getReplyMsgImgWidth(): Int? {
        return 0
    }

    override fun getReplyMsgImgHeight(): Int? {
        return 0
    }

    override fun getReplyMsgCCVideoCoverContent(): String? {
        return null
    }

    override fun getReplyMsgCCVideoId(): String? {
        return null
    }

    override fun getReplyMsgType(): String? {
        return info?.replyMsg?.msgType
    }


    override fun getReplyMsgClientMsgId(): String? {
        return info?.replyMsg?.clientMsgId
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

    override fun getReplySenderId(): Int {
        return info?.replyMsg?.sender?.senderId ?: 0
    }

    override fun getReplySenderName(): String? {
        return info?.replyMsg?.sender?.senderName
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
        Sender.resendMessage(getMsgId())
    }

    override fun onReplyQuestion() {

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

    }

}