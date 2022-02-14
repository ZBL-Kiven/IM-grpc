package com.zj.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.zj.database.converter.EmojiContentConverter
import com.zj.database.converter.GiftContentConverter
import com.zj.database.converter.MessageConverter

@Entity(tableName = "sendingMsg")
class SendMessageReqEn {

    var key: String = ""

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
     *回复的消息,不参与上传，但是参与存储
     */
    @TypeConverters(MessageConverter::class) var replyMsg: MessageInfoEntity? = null
        set(value) {
            replyMsgId = value?.msgId
            field = value
        }

    /**
     * emoji
     * */
    @TypeConverters(EmojiContentConverter::class) var emotionMessage: EmotionMessage? = null

    /**
     * gift
     * */
    @TypeConverters(GiftContentConverter::class) var giftMessage: GiftMessage? = null

    /*====================================================== 非参与上传字段 =============================================================*/

    /**
     * 文件
     */
    var localFilePath: String? = null

    /**
     * 失败后是否自动重试发送
     * */
    var autoRetryResend: Boolean = true

    /**
     * 发送时不需要推送
     * */
    var sendWithoutState: Boolean = false

    /**
     * 初始化是否自动重试发送发送中消息
     * */
    var autoResendWhenBootStart: Boolean = true

    /**
     * 是否忽略当前网络连接状态
     * */
    var ignoreConnectionState: Boolean = false

    /**
     * 是否忽略当前 GRPC 连接状态和 Fetch 状态
     * */
    var ignoreSendConditionState: Boolean = false

    /**
     * 文件进行 压缩、转储等操作后的待上传地址
     * */
    var tempFilePath: String? = null
}