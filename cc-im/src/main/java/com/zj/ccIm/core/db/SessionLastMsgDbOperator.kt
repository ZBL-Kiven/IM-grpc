package com.zj.ccIm.core.db

import com.zj.ccIm.core.Constance
import com.zj.ccIm.CcIM
import com.zj.ccIm.core.IMHelper
import com.zj.ccIm.core.MsgType
import com.zj.ccIm.core.bean.DotsInfo
import com.zj.ccIm.core.bean.MessageTotalDots
import com.zj.ccIm.core.bean.PrivateFansEn
import com.zj.ccIm.core.catching
import com.zj.ccIm.core.impl.ClientHubImpl
import com.zj.database.entity.PrivateOwnerEntity
import com.zj.database.entity.SessionLastMsgInfo
import com.zj.database.ut.Constance.generateKey
import java.lang.IllegalArgumentException
import java.lang.NullPointerException

internal object SessionLastMsgDbOperator : SessionOperateIn {

    fun dealSessionLastMsgInfo(callId: String, info: SessionLastMsgInfo): Pair<String, Any?>? {
        return catching {
            when (callId) {
                Constance.TOPIC_CHAT_OWNER_INFO -> {
                    val owner = info.ownerId
                    if (owner < 0) throw IllegalArgumentException("error case: the session last msg info ,owner id is invalid!")
                    info.key = generateKey(com.zj.database.ut.Constance.KEY_OF_PRIVATE_OWNER, ownerId = owner)
                    onDealPrivateOwnerSessionLastMsgInfo(info)
                }
                Constance.TOPIC_CHAT_FANS_INFO -> {
                    val target = info.targetUserId
                    if (target < 0) throw IllegalArgumentException("error case: the session last msg info ,target user id is invalid!")
                    info.key = generateKey(com.zj.database.ut.Constance.KEY_OF_PRIVATE_FANS, userId = target)
                    onDealPrivateFansSessionLastMsgInfo(info)
                }
                Constance.TOPIC_IM_MSG -> {
                    var groupId = info.groupId
                    if (groupId <= 0) groupId = info.newMsg?.groupId ?: -1L
                    if (groupId <= 0) throw IllegalArgumentException("error case: the session last msg info ,group id is invalid!")
                    info.key = generateKey(com.zj.database.ut.Constance.KEY_OF_SESSIONS, groupId = groupId)
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
            val fromV = info.newMsg?.sender?.senderId == info.ownerId
            val fromSend = info.newMsg?.sender?.senderId == CcIM.imConfig?.getUserId() ?: "--"
            val isQuestion = info.newMsg?.msgType == MsgType.QUESTION.type
            if (sessionInfo == null && (fromV || (fromSend && isQuestion))) {
                if (fromV) {
                    sessionInfo = PrivateOwnerEntity()
                    sessionInfo.ownerId = info.ownerId
                    sessionInfo.ownerName = info.newMsg?.sender?.senderName
                    sessionInfo.groupId = info.groupId
                    sessionInfo.avatar = info.newMsg?.sender?.senderAvatar
                    sessionDb.insertOrUpdate(sessionInfo)
                } else {
                    val group = it.sessionDao().findSessionById(info.groupId)
                    if (group != null) {
                        sessionInfo = PrivateOwnerEntity()
                        sessionInfo.ownerId = group.ownerId
                        sessionInfo.ownerName = group.ownerName
                        sessionInfo.groupId = group.groupId
                        sessionInfo.avatar = group.logo
                        sessionDb.insertOrUpdate(sessionInfo)
                    }
                }
            }
            sessionInfo.sessionMsgInfo = info
            notifyAllSessionDots()
            Pair(if (!exists) ClientHubImpl.PAYLOAD_ADD else ClientHubImpl.PAYLOAD_CHANGED, sessionInfo)
        }
    }

    fun notifyAllSessionDots(callId: String? = "") {
        IMHelper.withDb {
            val lastMsgDb = it.sessionMsgDao()
            val all = lastMsgDb.findAll()
            val sessions = it.sessionDao().allSessions
            val ownerSessions = it.privateChatOwnerDao().findAll()
            fun patchDotsInfo(key: String, info: DotsInfo, inAll: Boolean = false) {
                if (!inAll) {
                    val allContains = all.firstOrNull { a -> a.key == key }
                    if (allContains != null) all.remove(allContains)
                }
                lastMsgDb.findSessionMsgInfoByKey(key)?.let { i ->
                    info.questionNum += i.questionNum
                    info.unreadQuestions += i.unreadQuesNum ?: 0
                    info.unreadMessages += i.msgNum
                }
            }

            val sessionDots = DotsInfo()
            sessions.forEach { d ->
                val key = generateKey(com.zj.database.ut.Constance.KEY_OF_SESSIONS, groupId = d.groupId)
                patchDotsInfo(key, sessionDots)
            }
            val ownerDots = DotsInfo()
            ownerSessions.forEach { d ->
                val key = generateKey(com.zj.database.ut.Constance.KEY_OF_PRIVATE_OWNER, ownerId = d.ownerId)
                patchDotsInfo(key, ownerDots)
            }
            val fansDots = DotsInfo()
            all.forEach { d ->
                val key = generateKey(com.zj.database.ut.Constance.KEY_OF_PRIVATE_FANS, userId = d.targetUserId)
                patchDotsInfo(key, fansDots, true)
            }
            val totalUnreadMessages = sessionDots.unreadMessages + ownerDots.unreadMessages + fansDots.unreadMessages
            val totalQuestions = sessionDots.questionNum + ownerDots.questionNum + fansDots.questionNum
            val totalUnreadQuestions = sessionDots.unreadQuestions + ownerDots.unreadQuestions + fansDots.unreadQuestions
            val allDots = DotsInfo(totalUnreadMessages, totalUnreadQuestions, totalQuestions)
            val dots = MessageTotalDots(allDots, sessionDots, ownerDots, fansDots)
            CcIM.postToUiObservers(dots, callId)
        }
    }
}