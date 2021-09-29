package com.zj.ccIm.core.db

import com.zj.ccIm.core.IMHelper
import com.zj.ccIm.core.MsgType
import com.zj.ccIm.core.impl.ClientHubImpl
import com.zj.database.entity.MessageInfoEntity
import com.zj.im.chat.enums.SendMsgState
import com.zj.protocol.grpc.ImMessage
import com.zj.protocol.utl.ProtoBeanUtils

internal object MessageDbOperator {

    /**
     * Parse Msg and refresh the contents of the local database cache according to different situations
     * */
    fun onDealMessages(d: Any?, callId: String?, sendingState: SendMsgState?): Pair<MessageInfoEntity?, String?>? {
        val msg: MessageInfoEntity? = (d as? MessageInfoEntity) ?: (d as? ImMessage)?.let {
            ProtoBeanUtils.toPojoBean(MessageInfoEntity::class.java, d as? ImMessage)
        }
        if (msg != null) {
            return IMHelper.withDb {
                if (msg.sendTime <= 0) msg.sendTime = System.currentTimeMillis()
                val msgDb = it.messageDao()
                val localMsg = if (callId.isNullOrEmpty()) null else msgDb.findMsgByClientId(callId)
                if (localMsg == null && sendingState == SendMsgState.SENDING) {
                    msgDb.insertOrChangeMessage(msg)
                    if (msg.msgType == MsgType.QUESTION.type && msg.ownerId != IMHelper.imConfig.getUserId()) {

                        //if private chat is not exists , then create an object to db
                        PrivateOwnerDbOperator.createPrivateChatInfoIfNotExits(it, msg)
                    }
                }
                if (localMsg != null && (sendingState == SendMsgState.SUCCESS || sendingState == SendMsgState.NONE)) {
                    val sendDb = it.sendMsgDao()
                    val localSendCache = if (callId.isNullOrEmpty()) null else sendDb.findByCallId(callId)
                    sendDb.delete(localSendCache)
                    msgDb.deleteMsgByClientId(callId)
                }
                msg.sendingState = sendingState?.type ?: SendMsgState.NONE.type
                val pl = if (localMsg == null) ClientHubImpl.PAYLOAD_ADD else ClientHubImpl.PAYLOAD_CHANGED
                Pair(msg, pl)
            }
        } else return null
    }


}