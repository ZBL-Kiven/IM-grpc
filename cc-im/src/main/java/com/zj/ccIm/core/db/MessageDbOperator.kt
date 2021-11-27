package com.zj.ccIm.core.db

import com.zj.ccIm.core.ExtMsgType.EXTENDS_TYPE_RECALL
import com.zj.ccIm.CcIM
import com.zj.ccIm.core.IMHelper
import com.zj.ccIm.core.MessageType
import com.zj.ccIm.core.MsgType
import com.zj.ccIm.core.SystemMsgType
import com.zj.ccIm.core.impl.ClientHubImpl
import com.zj.database.entity.MessageInfoEntity
import com.zj.im.chat.enums.SendMsgState

internal object MessageDbOperator {

    /**
     * Parse Msg and refresh the contents of the local database cache according to different situations
     * */
    fun onDealMessages(msg: MessageInfoEntity?, callId: String?, sendingState: SendMsgState?): Pair<MessageInfoEntity?, String?>? {
        if (msg != null) {
            return IMHelper.withDb {
                if (msg.sendTime <= 0) msg.sendTime = System.currentTimeMillis()
                val msgDb = it.messageDao()
                val sendMsgDao = it.sendMsgDao()
                val hasLocal = (if (callId.isNullOrEmpty()) null else msgDb.findMsgByClientId(callId)) != null
                val sendingInfo = if (callId.isNullOrEmpty()) null else sendMsgDao.findByCallId(callId)
                if (sendingState == SendMsgState.ON_SEND_BEFORE_END) {
                    msgDb.insertOrChangeMessage(msg)
                }
                if (!hasLocal && sendingState == SendMsgState.SENDING) {
                    msgDb.insertOrChangeMessage(msg)
                    if (msg.msgType == MsgType.QUESTION.type && msg.ownerId != CcIM.imConfig?.getUserId() ?: "--") {

                        //private chat is not exists , then create an object to db
                        PrivateOwnerDbOperator.createPrivateChatInfoIfNotExits(it, msg)
                    }

                    //if (msg.ownerId != IMHelper.imConfig.getUserId() && msg.replyMsg?.msgType == MsgType.QUESTION.type) {
                    //    private fans chat is not exists , deal in [onDealPrivateFansSessionLastMsgInfo] when last msg patched
                    //}

                    //if (msg.sender?.senderId == IMHelper.imConfig.getUserId()) {
                    //   session is not exists
                    //}
                }
                msg.sendingState = sendingState?.type ?: SendMsgState.NONE.type
                val recalled = msg.extContent?.containsKey(EXTENDS_TYPE_RECALL) == true
                if (sendingInfo != null && sendingInfo.key == msg.channelKey) {
                    if (hasLocal && (recalled || sendingState == SendMsgState.SUCCESS || sendingState == SendMsgState.NONE)) {
                        sendMsgDao.deleteByCallId(callId)
                        msgDb.deleteMsgByClientId(callId)
                        msg.sendingState = SendMsgState.SUCCESS.type
                    }
                }
                Pair(msg, getMessagePayload(msg, hasLocal))
            }
        } else return null
    }

    fun deleteMsg(clientId: String) {
        IMHelper.withDb {
            it.messageDao().deleteMsgByClientId(clientId)
        }
    }

    private fun getMessagePayload(msg: MessageInfoEntity, hasLocal: Boolean): String {
        return when {
            msg.sendingState == SendMsgState.SUCCESS.type -> ClientHubImpl.PAYLOAD_CHANGED_SEND_STATE
            msg.systemMsgType == SystemMsgType.RECALLED.type -> ClientHubImpl.PAYLOAD_CHANGED
            !hasLocal -> ClientHubImpl.PAYLOAD_ADD
            else -> ClientHubImpl.PAYLOAD_CHANGED
        }
    }
}