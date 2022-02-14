package com.zj.database.entity

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.zj.database.converter.*
import java.util.*


@Suppress("unused")
@Entity(tableName = "messages")
class MessageInfoEntity : BaseMessageInfo() {

    /**
     * 订阅一条消息通道所需要的唯一键，由客户端生成
     * */
    override var channelKey: String = ""

    /**
     * 客户端消息id
     */
    @PrimaryKey override var clientMsgId: String = UUID.randomUUID().toString()

    /**
     * 群组id
     */
    override var groupId: Long = -1

    /**
     * 群主id
     */
    override var ownerId: Int? = null

    /**
     * 信息id
     */
    override var msgId: Long = -1

    /**
     * 发送时间
     */
    var sendTime: Long = 0

    /**
     * 本地字段 , 本次消息的解释类型 message / system
     * */
    override var messageType: String = "message"

    /**
     * 本地字段 , 用于系统通知的消息类型 , recall/sensitive/refused
     * */
    override var systemMsgType: String? = null

    /**
     * 消息类型 , 仅在解析类型为 message 的情况下生效 text/img/audio/video/question/cc_video
     *
     */
    override var msgType: String? = null

    /**
     * countryCode
     */
    var countryCode: String = ""

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
     * 直播消息类型
     * */
    @TypeConverters(LiveMessageContentConverter::class) var liveMessage: LiveMessageEntity? = null

    /**
     * 回答消息
     * */
    @TypeConverters(MessageConverter::class) var answerMsg: MessageInfoEntity? = null

    /**
     * 发送者信息
     */
    @TypeConverters(SenderContentConverter::class) var sender: SenderInfo? = null

    /**
     * 回复消息
     */
    @TypeConverters(MessageConverter::class) override var replyMsg: MessageInfoEntity? = null

    /**
     * 消息扩展字段
     * */
    @TypeConverters(ExtContentConverter::class) override var extContent: Map<String, String>? = null

    /**
     * emoji content
     * */
    @TypeConverters(EmojiContentConverter::class) var emotionMessage: EmotionMessage? = null

    /**
     * gift content
     * */
    @TypeConverters(GiftContentConverter::class) var giftMessage: GiftMessage? = null

    /**
     * 0 正常， 1 撤回
     * */
    @Deprecated("not recommend use it anymore", replaceWith = ReplaceWith("replace with", "com.zj.database.entity.MessageInfoEntity.extContent")) var status: Int = 0

    //----------------------------------------------------------------- 本地辅助字段 ⬇️--------------------------------------------------------------

    /**
     * 本地状态，是否发送成功 , 0 无状态，比如收到新消息
     * */
    override var sendingState: Int = 0

    /**
     * 本地新增的辅助参数，用于展示拥有差值的消息时间
     * */
    @Ignore var diffInCreateTime: String? = null
}