package com.zj.imtest.ui

import com.zj.database.entity.MessageInfoEntity
import com.zj.im.chat.enums.SendMsgState
import com.zj.imUi.interfaces.ImMsgIn
import com.zj.imtest.IMConfig

class ImEntityConverter(private val info: MessageInfoEntity?) : ImMsgIn {

    override fun getSendState(): Int {
        return info?.sendingState ?: SendMsgState.NONE.type
    }

    override fun getSenderId(): Int {
        return IMConfig.getUserId()
    }

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

    override fun getThumb(): String? {
        return info?.imgContent?.url
    }

}