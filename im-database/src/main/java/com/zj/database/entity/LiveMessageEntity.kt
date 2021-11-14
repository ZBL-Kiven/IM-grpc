package com.zj.database.entity

class LiveMessageEntity {

    var id: Long = 0

    /**
     * 开播状态: true-直播中,false-已下播
     */
    var status: Boolean = false

    /**
     * 房间id
     */
    var roomId: Int = 0

    /**
     * 房间名
     */
    var name: String? = null


    var area: String? = null

    /**
     * 房间介绍
     */
    var introduce: String? = null

    /**
     * 房间封面
     */
    var cover: String? = null

    /**
     * 用户id
     */
    var userId: Int = 0

    /**
     * 声网频道id
     */
    var channelId: String? = null

    /**
     * 观看数量
     */
    var viewNum: Int = 0
}