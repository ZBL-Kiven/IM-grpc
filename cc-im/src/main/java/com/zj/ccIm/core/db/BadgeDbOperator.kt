package com.zj.ccIm.core.db

import com.zj.ccIm.core.IMHelper
import com.zj.ccIm.core.bean.GetMsgReqBean
import com.zj.ccIm.core.fecher.FetchMsgChannel
import com.zj.ccIm.core.impl.ClientHubImpl
import com.zj.database.entity.SessionLastMsgInfo
import com.zj.database.ut.Constance
import java.lang.IllegalArgumentException

internal object BadgeDbOperator {

    fun clearGroupBadge(info: GetMsgReqBean) {
        info.channels.forEach {
            when (it.classification) {
                FetchMsgChannel.FANS_PRIVATE.classification -> {
                    val key = Constance.generateKey(Constance.KEY_OF_PRIVATE_OWNER, ownerId = info.ownerId)
                    val sessionBadgeInfo = changeBadge(key)
                    notifyOwnerSessionBadge(sessionBadgeInfo?.first ?: 0, info.groupId)
                    SessionLastMsgDbOperator.onDealPrivateOwnerSessionLastMsgInfo(sessionBadgeInfo?.second)
                }
                FetchMsgChannel.OWNER_PRIVATE.classification -> {
                    val key = Constance.generateKey(Constance.KEY_OF_PRIVATE_FANS, userId = info.targetUserid ?: 0)
                    val sessionBadgeInfo = changeBadge(key)
                    SessionLastMsgDbOperator.onDealPrivateFansSessionLastMsgInfo(sessionBadgeInfo?.second)
                }
                0 -> {
                    val key = Constance.generateKey(Constance.KEY_OF_SESSIONS, groupId = info.groupId)
                    val sessionBadgeInfo = changeBadge(key)
                    SessionLastMsgDbOperator.onDealSessionLastMsgInfo(sessionBadgeInfo?.second)
                }
                else -> throw IllegalArgumentException("unknown classification!")
            }?.let { p ->
                IMHelper.postToUiObservers(p.second, p.first)
            }
        }
    }

    private fun notifyOwnerSessionBadge(unReadCount: Int, groupId: Long) {
        IMHelper.withDb { db ->
            val ownerKey = Constance.generateKey(Constance.KEY_OF_SESSIONS, groupId = groupId)
            val ownerLastMsgDao = db.sessionMsgDao()
            val ownerLastMsg = ownerLastMsgDao.findSessionMsgInfoByKey(ownerKey)
            val unreadQuesNum = ownerLastMsg.unreadQuesNum ?: 0
            ownerLastMsg.unreadQuesNum = (unreadQuesNum - unReadCount).coerceAtLeast(0)
            ownerLastMsgDao.insertOrUpdateSessionMsgInfo(ownerLastMsg)
            val ownerSession = db.sessionDao().findSessionById(groupId)
            ownerSession?.sessionMsgInfo = ownerLastMsg
            IMHelper.postToUiObservers(ownerSession, ClientHubImpl.PAYLOAD_CHANGED)
        }
    }

    private fun changeBadge(key: String): Pair<Int, SessionLastMsgInfo?>? {
        return IMHelper.withDb {
            val last = it.sessionMsgDao().findSessionMsgInfoByKey(key)
            val lstCount = last.msgNum
            last?.ownerMsgId = null
            last?.ownerReplyMsgId = null
            last?.replyMeQuesMsgId = null
            last?.replyMeMsgId = null
            last?.msgNum = 0
            Pair(lstCount, last)
        }
    }
}