package com.zj.ccIm.core.db

import com.zj.ccIm.core.IMHelper

internal object BadgeDbOperator {

    fun clearGroupBadge(groupId: Long): Pair<String, Any?>? {
        return IMHelper.withDb {
            val last = it.sessionMsgDao().findSessionMsgInfoBySessionId(groupId)
            last?.ownerMsgId = null
            last?.ownerReplyMsgId = null
            last?.replyMeQuesMsgId = null
            last?.replyMeMsgId = null
            last?.msgNum = 0
            last
        }?.let {
            SessionLastMsgDbOperator.onDealSessionLastMsgInfo(it)
        }
    }
}