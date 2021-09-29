package com.zj.ccIm.core.db

import com.zj.ccIm.core.IMHelper
import com.zj.ccIm.core.bean.AssetsChanged
import com.zj.ccIm.core.bean.SendMessageRespEn
import com.zj.ccIm.core.impl.ClientHubImpl
import com.zj.database.dao.MessageDao
import com.zj.database.dao.SendMsgDao
import com.zj.im.chat.enums.SendMsgState

internal object SendingDbOperator {


    /**
     * When the message is sent, the state callback event,
     * here is the message that the database already exists in the database and is sent to the UI.
     * */
    fun onDealMsgSentInfo(d: SendMessageRespEn, callId: String?, sendingState: SendMsgState?): Pair<Any?, String?>? {
        if (sendingState == null) return null

        var msgDb: MessageDao? = null
        var sendDb: SendMsgDao? = null
        IMHelper.withDb {
            msgDb = it.messageDao()
            sendDb = it.sendMsgDao()
        }
        val localMsg = msgDb?.findMsgByClientId(callId)
        localMsg?.sendingState = sendingState.type
        localMsg?.msgId = d.msgId
        localMsg?.sendTime = d.sendTime
        localMsg?.questionContent?.published = d.published
        localMsg?.questionContent?.expireTime = d.expireTime
        val spark = d.sparkNum
        val diamond = d.diamondNum
        if (spark != null || diamond != null) {
            IMHelper.postToUiObservers(AssetsChanged(spark, diamond), callId)
        }
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
                if (d.msgStatus == 1) sendDb?.deleteByCallId(d.clientMsgId)
                val pl = when {
                    d.black -> ClientHubImpl.PAYLOAD_DELETE
                    d.msgStatus == 1 -> ClientHubImpl.PAYLOAD_DELETE_FROM_SENSITIVE_WORDS
                    else -> ClientHubImpl.PAYLOAD_CHANGED_SEND_STATE
                }
                Pair(localMsg, pl)
            }
            SendMsgState.NONE, SendMsgState.SUCCESS -> {
                sendDb?.deleteByCallId(d.clientMsgId)
                msgDb?.deleteMsgByClientId(d.clientMsgId)
                Pair(localMsg, ClientHubImpl.PAYLOAD_CHANGED_SEND_STATE)
            }
        }
    }
}