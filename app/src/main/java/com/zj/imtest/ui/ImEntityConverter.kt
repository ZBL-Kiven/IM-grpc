package com.zj.imtest.ui

import com.zj.database.entity.MessageInfoEntity
import com.zj.imUi.interfaces.ImMsgIn

class ImEntityConverter(private val info: MessageInfoEntity?) : ImMsgIn {

    override fun getSenderName(): String? {
        return info?.sender?.senderName
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

}