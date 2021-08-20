package com.zj.ccIm.core.bean

class SendMessageRespEn {

    /**
     * 发送状态
     */
    var success = false

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

}