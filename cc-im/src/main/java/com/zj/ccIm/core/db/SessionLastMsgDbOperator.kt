package com.zj.ccIm.core.db

import com.zj.ccIm.core.Constance
import com.zj.ccIm.core.IMHelper
import com.zj.ccIm.core.bean.MessageTotalDots
import com.zj.ccIm.core.bean.PrivateFansEn
import com.zj.ccIm.core.catching
import com.zj.ccIm.core.impl.ClientHubImpl
import com.zj.database.entity.PrivateOwnerEntity
import com.zj.database.entity.SessionLastMsgInfo
import java.lang.IllegalArgumentException
import java.lang.NullPointerException

internal object SessionLastMsgDbOperator : SessionOperateIn {

    fun dealSessionLastMsgInfo(callId: String, info: SessionLastMsgInfo): Pair<String, Any?>? {
        return catching {
            when (callId) {
                Constance.TOPIC_CHAT_OWNER_INFO -> {
                    val owner = info.ownerId
                    if (owner < 0) throw IllegalArgumentException("error case: the session last msg info ,owner id is invalid!")
                    info.key = com.zj.database.ut.Constance.generateKey(com.zj.database.ut.Constance.KEY_OF_PRIVATE_OWNER, ownerId = owner)
                    onDealPrivateOwnerSessionLastMsgInfo(info)
                }
                Constance.TOPIC_CHAT_FANS_INFO -> {
                    val target = info.targetUserId
                    if (target < 0) throw IllegalArgumentException("error case: the session last msg info ,target user id is invalid!")
                    info.key = com.zj.database.ut.Constance.generateKey(com.zj.database.ut.Constance.KEY_OF_PRIVATE_FANS, userId = target)
                    onDealPrivateFansSessionLastMsgInfo(info)
                }
                Constance.TOPIC_IM_MSG -> {
                    var groupId = info.groupId
                    if (groupId <= 0) groupId = info.newMsg?.groupId ?: -1L
                    if (groupId <= 0) throw IllegalArgumentException("error case: the session last msg info ,group id is invalid!")
                    info.key = com.zj.database.ut.Constance.generateKey(com.zj.database.ut.Constance.KEY_OF_SESSIONS, groupId = groupId)
                    onDealSessionLastMsgInfo(info)
                }
                else -> throw IllegalArgumentException("the callId $callId can not to parsed to a Key of lase session message")
            }
        }
    }

    override fun onDealSessionLastMsgInfo(info: SessionLastMsgInfo?): Pair<String, Any?>? {
        if (info == null) return null
        if (info.key.isEmpty()) throw NullPointerException("you must generate a MsgKey before update!")
        return IMHelper.withDb {
            val sessionDb = it.sessionDao()
            val lastMsgDb = it.sessionMsgDao()
            var groupId = info.groupId
            if (groupId <= 0) groupId = info.newMsg?.groupId ?: -1L
            if (groupId <= 0) throw IllegalArgumentException("error case: the session last msg info ,group id is invalid!")
            val sessionInfo = sessionDb.findSessionById(groupId)
            val exists = sessionInfo != null
            lastMsgDb.insertOrUpdateSessionMsgInfo(info)
            sessionInfo?.sessionMsgInfo = info
            notifyAllSessionDots()
            Pair(if (!exists) ClientHubImpl.PAYLOAD_ADD else ClientHubImpl.PAYLOAD_CHANGED, sessionInfo)
        }
    }

    override fun onDealPrivateFansSessionLastMsgInfo(info: SessionLastMsgInfo?): Pair<String, Any?>? {
        if (info == null) return null
        if (info.key.isEmpty()) throw NullPointerException("you must generate a MsgKey before update!")
        return IMHelper.withDb {
            val sessionInfo = PrivateFansEn()
            sessionInfo.lastMsgInfo = info
            sessionInfo.avatar = info.newMsg?.sender?.senderAvatar
            sessionInfo.userName = info.newMsg?.sender?.senderName
            sessionInfo.userId = info.targetUserId
            sessionInfo.groupId = info.groupId
            val lastMsgDb = it.sessionMsgDao()
            lastMsgDb.insertOrUpdateSessionMsgInfo(info)
            notifyAllSessionDots()
            Pair(ClientHubImpl.PAYLOAD_CHANGED, sessionInfo)
        }
    }

    override fun onDealPrivateOwnerSessionLastMsgInfo(info: SessionLastMsgInfo?): Pair<String, Any?>? {
        if (info == null) return null
        if (info.key.isEmpty()) throw NullPointerException("you must generate a MsgKey before update!")
        return IMHelper.withDb {
            val sessionDb = it.privateChatOwnerDao()
            val lastMsgDb = it.sessionMsgDao()
            var groupId = info.groupId
            if (groupId <= 0) groupId = info.newMsg?.groupId ?: -1L
            if (groupId <= 0) throw IllegalArgumentException("error case: the session last msg info ,group id is invalid!")
            var sessionInfo = sessionDb.findByGroupId(groupId)
            val exists = sessionInfo != null
            lastMsgDb.insertOrUpdateSessionMsgInfo(info)
            if (sessionInfo == null && info.newMsg?.sender?.senderId == info.ownerId) {
                sessionInfo = PrivateOwnerEntity()
                sessionInfo.ownerId = info.ownerId
                sessionInfo.ownerName = info.newMsg?.sender?.senderName
                sessionInfo.groupId = info.groupId
                sessionInfo.avatar = info.newMsg?.sender?.senderAvatar
                sessionDb.insertOrUpdate(sessionInfo)
            }
            sessionInfo.sessionMsgInfo = info
            notifyAllSessionDots()
            Pair(if (!exists) ClientHubImpl.PAYLOAD_ADD else ClientHubImpl.PAYLOAD_CHANGED, sessionInfo)
        }
    }

    fun notifyAllSessionDots(callId: String? = "") {
        IMHelper.withDb {
            val lastMsgDb = it.sessionMsgDao()
            val allDots = lastMsgDb.findAll()?.sumOf { c -> c.msgNum } ?: 0
            IMHelper.postToUiObservers(MessageTotalDots(allDots), callId)
        }
    }
}