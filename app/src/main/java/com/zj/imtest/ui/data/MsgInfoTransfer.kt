package com.zj.imtest.ui.data

import com.zj.ccIm.core.MessageType
import com.zj.ccIm.core.MsgType
import com.zj.ccIm.core.SystemMsgType
import com.zj.ccIm.core.impl.ClientHubImpl
import com.zj.database.entity.MessageInfoEntity
import com.zj.database.entity.TextContent
import com.zj.im.chat.enums.SendMsgState
import com.zj.im.chat.poster.DataHandler

class MsgInfoTransfer : DataHandler<MessageInfoEntity> {

    override fun handle(data: MessageInfoEntity, pl: String?): MutableList<Pair<Any?, String?>> {

        val lst = mutableListOf<Pair<Any?, String?>>(Pair(data, pl))
//        val msg = MessageInfoEntity().apply {
//            this.groupId = data.groupId
//            this.clientMsgId = data.clientMsgId + "-temp"
//            this.sendingState = SendMsgState.NONE.type
//            this.textContent = TextContent().apply {
//                this.text = "mock of system by transfer"
//            }
//            this.msgType = MsgType.TEXT.type
//            this.messageType = MessageType.SYSTEM.type
//            this.systemMsgType = SystemMsgType.REFUSED.type
//            this.sendTime = data.sendTime + 1
//            this.sender = data.sender
//            this.msgId = data.msgId + 100000
//        }
//
//        lst.add(Pair(msg, ClientHubImpl.PAYLOAD_ADD))
        return lst
    }

}