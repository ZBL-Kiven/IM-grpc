package com.zj.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.zj.database.converter.MessageConverter

@Entity(tableName = "sendingMsg")
class SendMessageReqEn {

    var groupId: Long = 0

    @PrimaryKey var clientMsgId: String = ""

    /**
     * text/img/audio/video
     */
    var msgType: String? = null

    /**
     * 文兵消息内容
     */
    var content: String? = null

    /**
     *回复的信息id
     */
    var replyMsgId: Long? = null

    /**
     * 需要回复消息体的类型
     * */
    var answerMsgType: String? = null

    /**
     * 文件
     */
    var url: String? = null

    /**
     * 文件（图片，视频）高
     */
    var height = 0

    /**
     * 文件（图片，视频）宽
     */
    var width = 0

    /**
     * 文件（语音，视频）时长
     */
    var duration = 0L

    /**
     * 是否公开消息 false否 tru是
     */
    var public = true

    /**
     * 钻石数
     */
    var diamondNum: Int? = null

    /**
     * 文件
     */
    var localFilePath: String? = null

    /**
     * 失败后是否自动重试发送
     * */
    var autoRetryResend: Boolean = true

    /**
     * 初始化是否自动重试发送发送中消息
     * */
    var autoResendWhenBootStart: Boolean = true

    /**
     *回复的消息,不参与上传，但是参与存储
     */
    @TypeConverters(MessageConverter::class) var replyMsg: MessageInfoEntity? = null
        set(value) {
            replyMsgId = value?.msgId
            field = value
        }
}