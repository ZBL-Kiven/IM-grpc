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
     * 钻石余额
     */
    val diamondNum: Int? = null

    /**
     * spark余额
     */
    val sparkNum: Int? = null

    /**
     * 是否公开消息
     * */
    var published: Boolean = true

}