package com.zj.imUi.interfaces

interface ImMsgIn {

    fun getSendState(): Int

    fun getSenderId(): Int

    fun getSenderName(): String?

    fun getTextContent(): String?

    fun getType(): String?

    fun getSenderAvatar(): String?

    fun getThumb(): String?
}