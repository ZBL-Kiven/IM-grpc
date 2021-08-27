package com.zj.database.entity

import androidx.room.Entity
import androidx.room.TypeConverters
import com.zj.database.converter.*
import java.util.*

@Entity(primaryKeys = ["serverMsgId", "clientMsgId"], tableName = "messages")
class MessageInfoEntity {

    /**
     * 客户端消息id
     */
    var clientMsgId: String = UUID.randomUUID().toString()

    /**
     * 群组id
     */
    var groupId: Long = -1

    /**
     * 群主id
     */
    var ownerId: Int? = null

    /**
     * 发送时间
     */
    var sendTime: Long = 0
        set(value) {
            if (createTs <= 0) createTs = value
            field = value
        }

    /**
     * 信息id
     */
    var msgId: Long = -1

    /**
     * 消息类型 text/img/audio/video/question/cc_video
     */
    var msgType: String? = null

    /**
     * 文本消息内容
     */
    @TypeConverters(TxtContentConverter::class) var textContent: TextContent? = null

    /**
     * 图片消息内容
     */
    @TypeConverters(ImgContentConverter::class) var imgContent: ImgContent? = null

    /**
     * 视频消息内容
     */
    @TypeConverters(VideoContentConverter::class) var videoContent: VideoContent? = null

    /**
     * 音频消息内容
     */
    @TypeConverters(AudioContentConverter::class) var audioContent: AudioContent? = null

    /**
     * cc_video消息内容
     */
    @TypeConverters(CCVideoContentConverter::class) var ccVideoContent: CCVideoContent? = null

    /**
     * 提问消息内容
     */
    @TypeConverters(QuestionContentConverter::class) var questionContent: QuestionContent? = null

    /**
     * 发送者信息
     */
    @TypeConverters(SenderContentConverter::class) var sender: SenderInfo? = null
        set(value) {
            senderId = value?.senderId;field = value
        }

    /**
     * 回复消息id
     */
    var replyMsgId: Long? = null

    /**
     * 回复消息
     */
    @TypeConverters(MessageConverter::class) var replyMsg: MessageInfoEntity? = null
        set(value) {
            replyId = value?.ownerId;field = value
        }

    //----------------------------------------------------------------- 本地辅助字段 ⬇️--------------------------------------------------------------

    /**
     * 合并服务端消息 id
     * */
    @Suppress("SuspiciousVarProperty") var serverMsgId: String = ""
        get() = "$groupId.$msgId"

    /**
     * 标记存储类型的 id
     * */
    var saveInfoId: String? = ""

    /**
     * 本地状态，是否发送成功 , 0 无状态，比如收到新消息
     * */
    var sendingState: Int = 0

    /**
     * 本地组装的重要参数，需要在发送、收到消息时都为其赋值。
     * */
    var senderId: Int? = null

    /**
     * 本地组装的重要参数，需要在发送、收到消息时都为其赋值。
     * */
    var replyId: Int? = null

    /**
     * 本地新增的辅助参数，用于在服务器时间和本地时间有较大偏差时，能保持正确的收发展示顺序
     * */
    var createTs: Long = 0
}