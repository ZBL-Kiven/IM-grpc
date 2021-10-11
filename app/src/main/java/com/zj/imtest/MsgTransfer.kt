package com.zj.imtest

import com.zj.database.entity.MessageInfoEntity
import com.zj.im.chat.poster.DataHandler

data class WrapMsgInfo<T>(val info: MessageInfoEntity? = null, var type: T? = null)

abstract class MsgTransfer<A> : DataHandler<MessageInfoEntity, WrapMsgInfo<A>>


class AMsgTransfer(private val page: String) : MsgTransfer<String>() {

    override fun handle(data: MessageInfoEntity): WrapMsgInfo<String> {
        return WrapMsgInfo(data, page)
    }

}