package com.zj.ccIm.core.bean

class SendMessageRespEn {

    /**
     * true 黑名单
     */
    var black = false

    /**
     * 群组id
     */
    var groupId: Long = -1

    /**
     * 回复的信息id
     */
    var msgId: Long = -1

    /**
     * 客户端信息id
     */
    var clientMsgId: String? = null

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
     * send msg state , 0: normal , else see [com.zj.ccIm.core.api.ImApi.EH]
     * */
    var msgStatus: Int = 0

}