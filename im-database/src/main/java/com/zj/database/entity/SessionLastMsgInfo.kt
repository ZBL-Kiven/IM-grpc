package com.zj.database.entity


import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.zj.database.converter.MessageConverter
import com.zj.database.ut.Constance
import com.zj.database.ut.LastMsgTabType
import java.lang.IllegalArgumentException

@Entity(tableName = "SessionMsgInfo")
class SessionLastMsgInfo {

    /**
     * the key set of , groupId in public chat , ownerId in private owner chat , -userId in private user chat.
     * */
    @PrimaryKey var key: String = ""

    var tabType: String = ""

    var groupId: Long = -1

    var ownerId: Int = -1

    var targetUserId: Int = -1

    // Last interactive message
    @TypeConverters(MessageConverter::class) var newMsg: MessageInfoEntity? = null

    // V reply to my Q&A message
    var replyMeQuesMsgId: Long? = null

    // Person reply to me
    var replyMeMsgId: Long? = null

    //V last common message
    var ownerMsgId: Long? = null

    //V @my message
    var ownerReplyMsgId: Long? = null

    // unread messages count
    var msgNum: Int = 0

    // question message count
    var questionNum: Int = 0

    //Spark total num
    var totalRewardNum: Int? = null

    //unread question num
    var unreadQuesNum: Int? = null
}