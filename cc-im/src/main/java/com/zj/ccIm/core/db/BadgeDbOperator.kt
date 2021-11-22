package com.zj.ccIm.core.db

import com.zj.ccIm.CcIM
import com.zj.ccIm.core.IMHelper
import com.zj.ccIm.core.bean.ChannelRegisterInfo
import com.zj.ccIm.core.fecher.FetchMsgChannel
import com.zj.ccIm.core.impl.ClientHubImpl
import com.zj.database.entity.SessionLastMsgInfo
import com.zj.database.ut.Constance

internal object BadgeDbOperator {

    fun clearGroupBadge(info: ChannelRegisterInfo) {
        when (info.mChannel) {
            FetchMsgChannel.FANS_PRIVATE -> {
                val key = Constance.generateKey(Constance.KEY_OF_PRIVATE_OWNER, ownerId = info.ownerId ?: 0)
                val sessionBadgeInfo = changeBadge(key)
                SessionLastMsgDbOperator.onDealPrivateOwnerSessionLastMsgInfo(sessionBadgeInfo?.second)
            }
            FetchMsgChannel.OWNER_PRIVATE -> {
                val last = notifyOwnerSessionBadgeWithFansSessionChanged(info.targetUserid, info.groupId)
                SessionLastMsgDbOperator.onDealPrivateFansSessionLastMsgInfo(last)
            }
            FetchMsgChannel.OWNER_CLAP_HOUSE, FetchMsgChannel.FANS_CLAP_HOUSE -> {
                val key = Constance.generateKey(Constance.KEY_OF_SESSIONS, groupId = info.groupId)
                val sessionBadgeInfo = changeBadge(key)
                SessionLastMsgDbOperator.onDealSessionLastMsgInfo(sessionBadgeInfo?.second)
            }
            else -> null
        }?.let { p ->
            CcIM.postToUiObservers(null, p.second, p.first)
        }
    }

    fun notifyOwnerSessionBadgeWithFansSessionChanged(targetUserId: Int?, groupId: Long): SessionLastMsgInfo? {
        val key = Constance.generateKey(Constance.KEY_OF_PRIVATE_FANS, userId = targetUserId ?: 0)
        val sessionBadgeInfo = changeBadge(key) ?: return null
        IMHelper.withDb { db ->
            val ownerKey = Constance.generateKey(Constance.KEY_OF_SESSIONS, groupId = groupId)
            val ownerLastMsgDao = db.sessionMsgDao()
            val ownerLastMsg = ownerLastMsgDao.findSessionMsgInfoByKey(ownerKey)
            val unreadQuesNum = ownerLastMsg.unreadQuesNum ?: 0
            ownerLastMsg.unreadQuesNum = (unreadQuesNum - (sessionBadgeInfo.first)).coerceAtLeast(0)
            ownerLastMsgDao.insertOrUpdateSessionMsgInfo(ownerLastMsg)
            val ownerSession = db.sessionDao().findSessionById(groupId)
            ownerSession?.sessionMsgInfo = ownerLastMsg
            CcIM.postToUiObservers(ownerSession, ClientHubImpl.PAYLOAD_CHANGED)
        }
        return sessionBadgeInfo.second
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