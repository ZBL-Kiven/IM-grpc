package com.zj.database.entity

abstract class BaseMessageInfo {

    abstract var groupId: Long
    abstract var ownerId: Int?
    abstract var msgId: Long
    abstract var clientMsgId: String
    abstract var messageType: String
    abstract var systemMsgType: String?
    abstract var msgType: String?
    abstract var channelKey: String
    abstract var replyMsg: MessageInfoEntity?
    abstract var extContent: Map<String, String>?
    abstract var sendingState: Int

    @Suppress("unused")
    fun <T : BaseMessageInfo?> copyTo(other: T?): T? {
        if (other == null) return null
        other.groupId = groupId
        other.ownerId = ownerId
        other.msgId = msgId
        other.clientMsgId = clientMsgId
        other.messageType = messageType
        other.systemMsgType = systemMsgType
        other.msgType = msgType
        other.channelKey = channelKey
        other.replyMsg = replyMsg
        other.extContent = extContent
        other.sendingState = sendingState
        return other
    }
}