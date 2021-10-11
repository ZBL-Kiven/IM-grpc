package com.zj.ccIm.core.db

import com.zj.ccIm.core.IMHelper
import com.zj.ccIm.core.bean.GetMsgReqBean
import com.zj.database.entity.SessionLastMsgInfo
import com.zj.database.ut.Constance

internal object BadgeDbOperator {

    fun clearGroupBadge(info: GetMsgReqBean) {
        var hasGroupType = false
        var hasPrivateOwnerType = false
        var hasPrivateFansType = false
        info.channels.forEach {
            if (it.classification == 0) hasGroupType = true
            if (it.classification == 1) hasPrivateOwnerType = true
            if (it.classification == 2) hasPrivateFansType = true
        }
        if (hasGroupType) {
            val key = SessionLastMsgInfo.generateKey(Constance.KEY_OF_SESSIONS, groupId = info.groupId)
            changeBadge(key)
        }
        if (hasPrivateOwnerType) {
            val key = SessionLastMsgInfo.generateKey(Constance.KEY_OF_PRIVATE_OWNER, ownerId = info.ownerId.toInt())
            changeBadge(key)
        }
        if (hasPrivateFansType) {
            val key = SessionLastMsgInfo.generateKey(Constance.KEY_OF_PRIVATE_FANS, userId = info.targetUserid?.toInt() ?: -1)
            changeBadge(key)
        }
    }

    private fun changeBadge(key: String) {
        IMHelper.withDb {
            val last = it.sessionMsgDao().findSessionMsgInfoByKey(key)
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