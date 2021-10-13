package com.zj.ccIm.core.db

import com.zj.ccIm.core.IMHelper
import com.zj.ccIm.core.bean.GetMsgReqBean
import com.zj.ccIm.core.fecher.FetchMsgChannel
import com.zj.database.entity.SessionLastMsgInfo
import com.zj.database.ut.Constance
import java.lang.IllegalArgumentException

internal object BadgeDbOperator {

    fun clearGroupBadge(info: GetMsgReqBean) {
        info.channels.forEach {
            getSessionLastMsgKeyForClassification(it.classification, info)
        }
    }

    private fun getSessionLastMsgKeyForClassification(classification: Int, info: GetMsgReqBean) {
        when (classification) {
            FetchMsgChannel.FANS_PRIVATE.classification -> {
                val key = SessionLastMsgInfo.generateKey(Constance.KEY_OF_PRIVATE_OWNER, ownerId = info.ownerId)
                SessionLastMsgDbOperator.onDealPrivateOwnerSessionLastMsgInfo(changeBadge(key))
            }
            FetchMsgChannel.OWNER_PRIVATE.classification -> {
                val key = SessionLastMsgInfo.generateKey(Constance.KEY_OF_PRIVATE_FANS, userId = info.targetUserId ?: 0)
                SessionLastMsgDbOperator.onDealPrivateFansSessionLastMsgInfo(changeBadge(key))
            }
            0 -> {
                val key = SessionLastMsgInfo.generateKey(Constance.KEY_OF_SESSIONS, groupId = info.groupId)
                SessionLastMsgDbOperator.onDealSessionLastMsgInfo(changeBadge(key))
            }
            else -> throw IllegalArgumentException("unknown classification!")
        }?.let { p ->
            IMHelper.postToUiObservers(p.second, p.first)
        }
    }

    private fun changeBadge(key: String): SessionLastMsgInfo? {
        return IMHelper.withDb {
            val last = it.sessionMsgDao().findSessionMsgInfoByKey(key)
            last?.ownerMsgId = null
            last?.ownerReplyMsgId = null
            last?.replyMeQuesMsgId = null
            last?.replyMeMsgId = null
            last?.msgNum = 0
            last
        }
    }
}