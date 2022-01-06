package com.zj.ccIm.core.db

import com.zj.ccIm.core.ExtMsgKeys
import com.zj.ccIm.core.IMHelper
import com.zj.ccIm.core.api.ImApi
import com.zj.ccIm.core.bean.SendMessageRespEn
import com.zj.ccIm.core.impl.ClientHubImpl
import com.zj.ccIm.core.sender.Converter
import com.zj.database.dao.MessageDao
import com.zj.database.dao.SendMsgDao
import com.zj.database.entity.MessageInfoEntity
import com.zj.database.entity.SendMessageReqEn
import com.zj.im.chat.enums.SendMsgState
import com.zj.im.utils.cast

internal object SendingDbOperator {

    fun onDealSendMsgReqInfo(d: SendMessageReqEn?, sendingState: SendMsgState?, callId: String?): Pair<MessageInfoEntity?, String?>? {
        return if (d != null) {
            val sst = sendingState ?: SendMsgState.SENDING
            if (sst == SendMsgState.SENDING) d.diamondNum?.let {
                AssetsChangedOperator.onAssetsChanged(callId, -it, null)
            }
            onDealMsgSendInfo(d, sst, callId)
        } else null
    }

    fun onDealSendMsgRespInfo(d: SendMessageRespEn?, sendingState: SendMsgState?, callId: String?): Pair<MessageInfoEntity?, String?>? {
        return if (d != null) {
            run assets@{
                if (sendingState != SendMsgState.SUCCESS && sendingState != SendMsgState.NONE) {
                    val diamond = d.diamondNum ?: return@assets
                    AssetsChangedOperator.onAssetsChanged(callId, diamond, null)
                } else {
                    val sparks = d.sparkNum ?: return@assets
                    AssetsChangedOperator.onAssetsChanged(callId, null, sparks)
                }
            }
            onDealMsgSentInfo(d, sendingState, callId)
        } else null
    }

    fun onDealMsgSendInfo(d: SendMessageReqEn, sendingState: SendMsgState, callId: String?): Pair<MessageInfoEntity?, String?>? {
        val msg = Converter.exchangeMsgInfoBySendingInfo(d, sendingState)
        return MessageDbOperator.onDealMessages(msg, callId, sendingState)
    }

    /**
     * When the message is sent, the state callback event,
     * here is the message that the database already exists in the database and is sent to the UI.
     * */
    fun onDealMsgSentInfo(d: SendMessageRespEn, sendingState: SendMsgState?, callId: String?): Pair<MessageInfoEntity?, String?>? {
        if (sendingState == null) return null
        var msgDb: MessageDao? = null
        var sendDb: SendMsgDao? = null
        IMHelper.withDb {
            msgDb = it.messageDao()
            sendDb = it.sendMsgDao()
        }
        val localMsg = getLocalMsgInfo(msgDb?.findMsgByClientId(callId), d, sendingState)
        return when (sendingState) {
            SendMsgState.SENDING -> null

            //Callback when the operation of the coroutine is completed
            SendMsgState.ON_SEND_BEFORE_END -> {
                msgDb?.insertOrChangeMessage(localMsg)
                Pair(localMsg, ClientHubImpl.PAYLOAD_CHANGED_SEND_STATE)
            }
            SendMsgState.FAIL, SendMsgState.TIME_OUT -> {
                msgDb?.deleteMsgByClientId(d.clientMsgId)
                if (d.black) sendDb?.deleteAllBySessionId(d.groupId)
                if (d.msgStatus != 0) sendDb?.deleteByCallId(d.clientMsgId)
                val pl = when {
                    d.black -> ClientHubImpl.PAYLOAD_DELETE_FROM_BLOCKED
                    /**
                     * 暂时没有使用，Payload 保留
                     * */
                    d.msgStatus == ImApi.EH.SENSITIVE_WORD -> ClientHubImpl.PAYLOAD_DELETE_FROM_SENSITIVE_WORDS
                    d.msgStatus == ImApi.EH.NOT_ENOUGH -> ClientHubImpl.PAYLOAD_DELETE_NOT_ENOUGH
                    d.msgStatus == ImApi.EH.NOT_OWNER -> ClientHubImpl.PAYLOAD_DELETE_NOT_OWNER
                    d.msgStatus == ImApi.EH.GROUP_MEMBER_NOT_EXIST -> ClientHubImpl.PAYLOAD_DELETE_FROM_GROUP_MEMBER_NOT_EXIST
                    d.msgStatus == ImApi.EH.GROUP_STOPPED -> ClientHubImpl.PAYLOAD_DELETE_GROUP_STOPPED
                    d.msgStatus == ImApi.EH.REPEAT_ANSWER -> ClientHubImpl.PAYLOAD_DELETE_REPEAT_ANSWER
                    d.msgStatus == ImApi.EH.DIAMOND_NOT_ENOUGH -> ClientHubImpl.PAYLOAD_DELETE_NOT_ENOUGH
                    else -> ClientHubImpl.PAYLOAD_CHANGED_SEND_STATE
                }
                Pair(localMsg, pl)
            }
            SendMsgState.NONE, SendMsgState.SUCCESS -> {
                sendDb?.deleteByCallId(d.clientMsgId)
                msgDb?.deleteMsgByClientId(d.clientMsgId)
                val pl = when (d.msgStatus) {
                    ImApi.EH.SENSITIVE_WORD_ERROR -> {
                        var pl = ClientHubImpl.PAYLOAD_REFUSE_FROM_SENSITIVE_WORDS
                        if (d.extContent?.containsKey(ExtMsgKeys.SENSITIVE_OTHER) == true && cast<Any?, Boolean>(d.extContent?.get(ExtMsgKeys.SENSITIVE_OTHER)) == true) {
                            pl = ClientHubImpl.PAYLOAD_REFUSE_FROM_SENSITIVE_WORDS_OTHER
                        }
                        pl
                    }
                    else -> ClientHubImpl.PAYLOAD_CHANGED_SEND_STATE
                }
                Pair(localMsg, pl)
            }
        }
    }

    private fun getLocalMsgInfo(localMsg: MessageInfoEntity?, d: SendMessageRespEn, sendingState: SendMsgState): MessageInfoEntity? {
        var result = localMsg
        if (result == null && sendingState == SendMsgState.FAIL) {
            result = MessageInfoEntity()
        }
        result?.channelKey = d.channelKey
        result?.clientMsgId = d.clientMsgId
        result?.sendingState = sendingState.type
        result?.msgId = d.msgId
        result?.ownerId = d.ownerId
        result?.sendTime = if (d.sendTime <= 0) System.currentTimeMillis() else d.sendTime
        result?.questionContent?.published = d.published
        result?.questionContent?.expireTime = d.expireTime
        result?.extContent = d.extContent
        return result
    }
}