package com.zj.ccIm.core.bean

import com.zj.database.entity.SessionLastMsgInfo

class PrivateFansEn {

    /**
     * 用户id
     */
    var userId: Int? = null

    /**
     * 用户名
     */
    var userName: String? = null

    /**
     * code
     */
    var code: String? = null

    /**
     * 头像
     */
    var avatar: String? = null

    /**
     * 群id
     */
    var groupId: Long? = null

    /**
     * 群内最后一条消息，推送时拼装
     * */
    var lastMsgInfo: SessionLastMsgInfo? = null
}