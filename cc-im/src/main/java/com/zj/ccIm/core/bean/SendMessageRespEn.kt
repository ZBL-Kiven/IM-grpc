package com.zj.ccIm.core.bean

import com.zj.database.entity.BaseMessageInfo
import com.zj.database.entity.MessageInfoEntity
import com.zj.im.chat.enums.SendMsgState

class SendMessageRespEn : BaseMessageInfo() {

    override var ownerId: Int? = null

    override var groupId: Long = -1

    override var msgId: Long = -1

    override var clientMsgId: String = ""

    override var messageType: String = ""

    override var systemMsgType: String? = ""

    override var msgType: String? = ""

    override var channelKey: String = ""

    override var replyMsg: MessageInfoEntity? = null

    override var extContent: Map<String, String>? = null

    override var sendingState: Int = SendMsgState.SUCCESS.type

    /**
     * true 黑名单
     */
    var black = false

    /**
     * 服务器时间
     * */
    var sendTime: Long = 0

    /**
     * 钻石 变化
     */
    var diamondNum: Int? = null

    /**
     * spark 变化
     */
    var sparkNum: Int? = null

    /**
     * 是否公开消息
     * */
    var published: Boolean = true

    /**
     * 消息过期时间，仅限于有时限的消息
     * */
    var expireTime: Long = -1L

    /**
     * send msg state , 0: normal , 2 sensitive error , else see [com.zj.ccIm.core.api.ImApi.EH]
     * */
    var msgStatus: Int = 0
}