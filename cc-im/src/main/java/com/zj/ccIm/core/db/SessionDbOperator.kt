package com.zj.ccIm.core.db

import com.google.gson.Gson
import com.zj.api.interceptor.Interceptor.Companion.toMediaTypeOrNull
import com.zj.api.interceptor.Interceptor.Companion.toRequestBody
import com.zj.ccIm.core.IMHelper
import com.zj.ccIm.core.bean.FetchResult
import com.zj.ccIm.core.fecher.Fetcher
import com.zj.ccIm.core.impl.ClientHubImpl
import com.zj.ccIm.core.sp.SPHelper
import com.zj.ccIm.logger.ImLogs
import com.zj.database.IMDb
import com.zj.database.entity.MessageInfoEntity
import com.zj.database.entity.PrivateOwnerEntity
import com.zj.database.entity.SessionInfoEntity
import com.zj.database.entity.SessionLastMsgInfo
import com.zj.database.ut.Constance

internal object SessionDbOperator {

    fun onDealSessionInfo(d: String?): Pair<String, Any?>? {
        val info = try {
            Gson().fromJson(d, SessionInfoEntity::class.java)
        } catch (e: Exception) {
            ImLogs.requireToPrintInFile("onDealSessionInfo", "parse session error with : ${e.message} \n data = $d")
            return null
        }
        return IMHelper.withDb {
            val sessionDb = it.sessionDao()
            val lastMsgDb = it.sessionMsgDao()
            val local = sessionDb.findSessionById(info.groupId)
            val exists = local != null
            if (exists) {
                info.disturbStatus = local.disturbStatus
                info.top = local.top
            }
            val needDelete = info.groupStatus == 3
            if (!needDelete) {
                val key = Constance.generateKey(Constance.KEY_OF_SESSIONS, groupId = info.groupId)
                val lastMsgInfo = lastMsgDb.findSessionMsgInfoByKey(key)
                info.sessionMsgInfo = lastMsgInfo
            }
            if (needDelete) {
                if (exists) sessionDb.deleteSession(local)
            } else {
                sessionDb.insertOrChangeSession(info)
                PrivateOwnerDbOperator.updateSessionInfo(info)
            }
            Pair(if (exists) {
                if (needDelete) {
                    ClientHubImpl.PAYLOAD_DELETE
                } else {
                    ClientHubImpl.PAYLOAD_ADD
                }
            } else {
                ClientHubImpl.PAYLOAD_CHANGED
            }, info)
        }
    }

    fun notifyAllSession(callId: String?) {
        IMHelper.withDb {
            val sessions = it.sessionDao().allSessions
            val lastMsgDb = it.sessionMsgDao()
            sessions?.forEach { i ->
                val key = Constance.generateKey(Constance.KEY_OF_SESSIONS, groupId = i.groupId)
                i.sessionMsgInfo = lastMsgDb.findSessionMsgInfoByKey(key)
            }
            val isFirst = SPHelper[Fetcher.SP_FETCH_SESSIONS_TS, 0L] ?: 0L <= 0
            IMHelper.postToUiObservers(FetchResult(true, isFirst, sessions.isNullOrEmpty()))
            IMHelper.postToUiObservers(sessions, callId)
        }
    }
}