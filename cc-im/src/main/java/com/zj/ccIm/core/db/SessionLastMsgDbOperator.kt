package com.zj.ccIm.core.db

import com.zj.ccIm.core.IMHelper
import com.zj.ccIm.core.bean.MessageTotalDots
import com.zj.ccIm.core.impl.ClientHubImpl
import com.zj.ccIm.logger.ImLogs
import com.zj.database.entity.SessionLastMsgInfo

internal object SessionLastMsgDbOperator {

    fun onDealSessionLastMsgInfo(info: SessionLastMsgInfo): Pair<String, Any?>? {
        return IMHelper.withDb {
            val sessionDb = it.sessionDao()
            val lastMsgDb = it.sessionMsgDao()
            var groupId = info.groupId
            if (groupId <= 0) groupId = info.newMsg?.groupId ?: -1L
            if (groupId <= 0) ImLogs.requireToPrintInFile("onDealSessionLastMsgInfo", "error case: the session last msg info ,group id is invalid!")
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