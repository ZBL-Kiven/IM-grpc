package com.zj.ccIm.core.sender.exchange

import com.zj.ccIm.core.bean.SendMessageRespEn
import com.zj.ccIm.core.db.SendingDbOperator
import com.zj.database.entity.MessageInfoEntity
import com.zj.database.entity.SendMessageReqEn
import com.zj.im.chat.enums.SendMsgState

abstract class MessageInfoEntityDataExchange : CustomSendListener<MessageInfoEntity>(MessageInfoEntity::class.java) {

    override fun changeDataToTarget(sendState: SendMsgState, callId: String, d: Any?, payloadInfo: Any?): Pair<MessageInfoEntity?, String?>? {
        when (d) {
            is SendMessageReqEn -> {
                return if (isPendingSet) {
                    SendingDbOperator.onDealMsgSendInfo(d, sendState, callId)
                } else {
                    SendingDbOperator.onDealSendMsgReqInfo(d, sendState, callId)
                }
            }
            is SendMessageRespEn -> {
                return if (isPendingSet) {
                    SendingDbOperator.onDealMsgSentInfo(d, sendState, callId)
                } else {
                    SendingDbOperator.onDealSendMsgRespInfo(d, sendState, callId)
                }
            }
        }
        return null
    }
}