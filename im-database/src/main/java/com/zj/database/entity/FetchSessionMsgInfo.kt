package com.zj.database.entity


import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.zj.database.converter.MessageConverter

@Entity(tableName = "SessionMsgInfo")
class FetchSessionMsgInfo {

    @PrimaryKey var groupId: Long = -1

    // Last interactive message
    @TypeConverters(MessageConverter::class) var newMsg: MessageInfoEntity? = null

    // V reply to my Q&A message
    var replyMeQuesMsgId: Long? = null

    // Person reply to me
    var replyMeMsgId: Long? = null

    // unread messages count
    var msgNum: Int = 0

    // question message count
    var questionNum: Int = 0
}