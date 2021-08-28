package com.zj.imUi.interfaces

interface ImMsgIn {

    fun getSenderName(): String?

    fun getTextContent(): String?

    fun getType(): String?

    fun getSenderAvatar(): String?
}