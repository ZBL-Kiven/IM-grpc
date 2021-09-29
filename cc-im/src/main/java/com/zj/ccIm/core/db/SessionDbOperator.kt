package com.zj.ccIm.core.db

import com.google.gson.Gson
import com.zj.ccIm.core.IMHelper
import com.zj.ccIm.core.fecher.Fetcher
import com.zj.ccIm.core.impl.ClientHubImpl
import com.zj.ccIm.core.sp.SPHelper
import com.zj.ccIm.error.FetchSessionResult
import com.zj.ccIm.logger.ImLogs
import com.zj.database.entity.SessionInfoEntity

internal object SessionDbOperator {

    fun onDealSessionInfo(d: String?): Pair<String, Any?>? {
        val info = try {
            Gson().fromJson(d, SessionInfoEntity::class.java)
        } catch (e: Exception) {
            ImLogs.requireToPrintInFile("onDealSessionInfo", "parse session error with : ${e.message} \n data = $d"); return null
        }
        return IMHelper.withDb {
            val sessionDb = it.sessionDao()
            val lastMsgDb = it.sessionMsgDao()
            val exists = sessionDb.findSessionById(info.groupId) == null
            val local = sessionDb.findSessionById(info.groupId)
            if (local != null) {
                info.disturbStatus = local.disturbStatus
                info.top = local.top
            }
            val lastMsgInfo = lastMsgDb.findSessionMsgInfoBySessionId(info.groupId)
            info.sessionMsgInfo = lastMsgInfo
            sessionDb.insertOrChangeSession(info)
            Pair(if (exists) ClientHubImpl.PAYLOAD_ADD else ClientHubImpl.PAYLOAD_CHANGED, info)
        }
    }


    fun notifyAllSession(callId: String?) {
        IMHelper.withDb {
            val sessions = it.sessionDao().allSessions
            val lastMsgDb = it.sessionMsgDao()
            sessions?.forEach { i ->
                i.sessionMsgInfo = lastMsgDb.findSessionMsgInfoBySessionId(i.groupId)
            }
            val isFirst = SPHelper[Fetcher.SP_FETCH_SESSIONS_TS, 0L] ?: 0L <= 0
            IMHelper.postToUiObservers(FetchSessionResult(true, isFirst, sessions.isNullOrEmpty()))
            IMHelper.postToUiObservers(sessions, callId)
        }
    }
}