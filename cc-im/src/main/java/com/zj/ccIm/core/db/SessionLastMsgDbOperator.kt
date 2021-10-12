package com.zj.ccIm.core.db

import com.zj.ccIm.core.Constance
import com.zj.ccIm.core.IMHelper
import com.zj.ccIm.core.bean.MessageTotalDots
import com.zj.ccIm.core.catching
import com.zj.ccIm.core.impl.ClientHubImpl
import com.zj.ccIm.logger.ImLogs
import com.zj.database.entity.SessionLastMsgInfo
import com.zj.database.ut.LastMsgTabType
import java.lang.IllegalArgumentException
import java.lang.NullPointerException

internal object SessionLastMsgDbOperator {

    fun dealSessionLastMsgInfo(callId: String, info: SessionLastMsgInfo): Pair<String, Any?>? {
        return catching {
            info.key = when (callId) {
                Constance.TOPIC_CHAT_FANS_INFO -> {
                    val owner = info.ownerId
                    if (owner < 0) throw IllegalArgumentException("error case: the session last msg info ,owner id is invalid!")
                    SessionLastMsgInfo.generateKey(com.zj.database.ut.Constance.KEY_OF_PRIVATE_OWNER, ownerId = owner)
                }
                Constance.TOPIC_CHAT_OWNER_INFO -> {
                    val target = info.targetUserId
                    if (target < 0) throw IllegalArgumentException("error case: the session last msg info ,target user id is invalid!")
                    SessionLastMsgInfo.generateKey(com.zj.database.ut.Constance.KEY_OF_PRIVATE_FANS, userId = target)
                }
                Constance.TOPIC_IM_MSG -> {
                    var groupId = info.groupId
                    if (groupId <= 0) groupId = info.newMsg?.groupId ?: -1L
                    if (groupId <= 0) throw IllegalArgumentException("error case: the session last msg info ,group id is invalid!")
                    SessionLastMsgInfo.generateKey(com.zj.database.ut.Constance.KEY_OF_SESSIONS, groupId = groupId)
                }
                else -> throw IllegalArgumentException("the callId $callId can not to parsed to a Key of lase session message")
            }
            onDealSessionLastMsgInfo(info)
        }
    }

    fun onDealSessionLastMsgInfo(info: SessionLastMsgInfo): Pair<String, Any?>? {
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

    fun notifyAllSessionDots(callId: String? = "") {
        IMHelper.withDb {
            val lastMsgDb = it.sessionMsgDao()
            val allDots = lastMsgDb.findAll()?.sumOf { c -> c.msgNum } ?: 0
            IMHelper.postToUiObservers(MessageTotalDots(allDots), callId)
        }
    }
}