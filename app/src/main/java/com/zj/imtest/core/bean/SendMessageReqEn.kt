package com.zj.imtest.core.bean

class SendMessageReqEn {

    var groupId: Long? = null

    /**
     * 客户端信息id
     */
    var clientMsgId: String? = null

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
    var replyMsgId: Long = 0

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
    var duration = 0

    /**
     * 是否公开消息 false否 tru是
     */
    var isPublic = true

    /**
     * 钻石数
     */
    var diamondNum: Int? = null

    /**
     * 上传的文件大小（用于记录，如果存在）
     */
    var uploadDataTotalByte: Long = 0

    /**
     * 文件
     */
    var localFilePath: String? = null
}