package com.zj.ccIm.core.db

import com.google.gson.Gson
import com.zj.ccIm.CcIM
import com.zj.ccIm.core.IMHelper
import com.zj.ccIm.core.bean.FetchResult
import com.zj.ccIm.core.bean.RoleInfo
import com.zj.ccIm.core.fecher.Fetcher
import com.zj.ccIm.core.impl.ClientHubImpl
import com.zj.database.sp.SPHelper
import com.zj.ccIm.logger.ImLogs
import com.zj.database.entity.SessionInfoEntity
import com.zj.database.ut.Constance

internal object SessionDbOperator {

    fun onDealSessionInfo(d: String?): Pair<String, SessionInfoEntity?>? {
        val info = try {
            Gson().fromJson(d, SessionInfoEntity::class.java)
        } catch (e: Exception) {
            ImLogs.recordLogsInFile("onDealSessionInfo", "parse session error with : ${e.message} \n data = $d")
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
            val key = Constance.generateKey(Constance.KEY_OF_SESSIONS, groupId = info.groupId)
            if (!needDelete) {
                val lastMsgInfo = lastMsgDb.findSessionMsgInfoByKey(key)
                info.sessionMsgInfo?.let { lm ->
                    lm.key = key
                    lastMsgDb.insertOrUpdateSessionMsgInfo(lm)
                } ?: run {
                    info.sessionMsgInfo = lastMsgInfo
                }
            }
            if (needDelete) {
                if (exists) {
                    sessionDb.deleteSession(local)
                    lastMsgDb.deleteByKey(key)
                }
            } else {
                sessionDb.insertOrChangeSession(info)
                if (exists) {
                    PrivateOwnerDbOperator.updateSessionInfo(info)
                }
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
            CcIM.postToUiObservers(FetchResult(true, isFirst, sessions.isNullOrEmpty()))
            CcIM.postToUiObservers(SessionInfoEntity::class.java, sessions, callId)
        }
    }

    fun onDealSessionRoleInfo(d: String?): Pair<String, SessionInfoEntity?>? {
        val info = try {
            Gson().fromJson(d, RoleInfo::class.java)
        } catch (e: Exception) {
            ImLogs.recordLogsInFile("onDealSessionInfo", "parse session error with : ${e.message} \n data = $d")
            return null
        }
        return IMHelper.withDb {
            val sessionDb = it.sessionDao()
            val sessionInfo = sessionDb.findSessionById(info.groupId) ?: return@withDb null
            sessionInfo.role = info.role
            sessionDb.insertOrChangeSession(sessionInfo)
            val lastMsgDb = it.sessionMsgDao()
            val key = Constance.generateKey(Constance.KEY_OF_SESSIONS, groupId = info.groupId)
            val lastMsgInfo = lastMsgDb.findSessionMsgInfoByKey(key)
            sessionInfo.sessionMsgInfo?.let { lm ->
                lm.key = key
                lastMsgDb.insertOrUpdateSessionMsgInfo(lm)
            } ?: run {
                sessionInfo.sessionMsgInfo = lastMsgInfo
            }
            Pair(ClientHubImpl.PAYLOAD_CHANGED, sessionInfo)
        }
    }
}