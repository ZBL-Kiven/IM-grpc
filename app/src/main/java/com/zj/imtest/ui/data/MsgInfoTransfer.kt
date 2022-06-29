package com.zj.imtest.ui.data

import com.zj.database.entity.MessageInfoEntity
import com.zj.im.chat.poster.DataHandler

class MsgInfoTransfer : DataHandler<MessageInfoEntity> {

    override fun handle(data: MessageInfoEntity): Any {

        return data
    }

}