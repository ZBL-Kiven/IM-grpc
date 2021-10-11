package com.zj.ccIm.core.fecher

import com.zj.api.base.BaseRetrofit
import com.zj.ccIm.core.Constance
import com.zj.ccIm.core.IMHelper
import com.zj.ccIm.core.api.ImApi
import com.zj.ccIm.core.sp.SPHelper
import com.zj.ccIm.error.FetchSessionResult
import com.zj.ccIm.logger.ImLogs
import com.zj.database.DbHelper
import com.zj.database.entity.SessionLastMsgInfo
import io.reactivex.schedulers.Schedulers

/**
 * The message pull of IM group conversation is divided into.
 * Step 1: Pull offline group information.
 * Step 2: Pull the last message of the group.
 * */
internal object GroupSessionFetcher : BaseFetcher() {

    override fun startFetch(prop: FetchType): BaseRetrofit.RequestCompo? {
        val lastTs = SPHelper[Fetcher.SP_FETCH_SESSIONS_TS, 0L] ?: 0L
        ImLogs.d("GroupSessionFetcher", "start fetch sessions  by ${prop.flags} ,with last ts : $lastTs")
        val isFirstFetch = lastTs <= 0
        return ImApi.getFunctionApi().call({ it.fetchSessions(lastTs) }, Schedulers.io(), Schedulers.newThread()) { b, d, e ->
            try {
                val sessions = d?.groupList
                if (b) {
                    if (sessions.isNullOrEmpty()) {
                        if (isFirstFetch) {
                            ImLogs.d("GroupSessionFetcher", "fetch sessions is null result for first time !")
                            IMHelper.postToUiObservers(FetchSessionResult(success = true, isFirstFetch = true, isNullData = true))
                            DbHelper.get(Constance.app)?.db?.sessionDao()?.deleteAll()
                            finishAFetch(prop, true)
                        } else {
                            ImLogs.d("GroupSessionFetcher", "fetch sessions is null ,trying to fetch last msg by localed !")
                            fetchLastMsg(prop)
                        }
                    } else {
                        ImLogs.d("GroupSessionFetcher", "fetch sessions success , new ts is ${d.timeStamp}, changed group is [${sessions.joinToString { "${it.groupName} , " }}]")
                        SPHelper.put(Fetcher.SP_FETCH_SESSIONS_TS, d.timeStamp)
                        val sessionDao = DbHelper.get(Constance.app)?.db?.sessionDao()
                        sessions.forEach { s ->
                            if (s.groupStatus == 0 || s.groupStatus == 1) {
                                sessionDao?.insertOrChangeSession(s)
                            } else {
                                sessionDao?.deleteSession(s)
                            }
                        }
                        fetchLastMsg(prop)
                    }
                } else {
                    finishAFetch(prop, isSuccess = false, "fetch sessions group failed with:${e?.message} !!")
                }
            } catch (e: Exception) {
                cancel(prop)
                IMHelper.postError(e)
            }
        }
    }

    private fun fetchLastMsg(prop: FetchType) {
        val sessions = DbHelper.get(Constance.app)?.db?.sessionDao()?.allSessions
        if (!sessions.isNullOrEmpty()) {
            val gIds = sessions.map { ms -> ms.groupId }
            ImLogs.d("GroupSessionFetcher", "start fetch session last message info by  ${prop.flags} , groups = $gIds")
            prop.compo = ImApi.getFunctionApi().call({ it.fetchSessionLastMessage(gIds) }, Schedulers.io(), Schedulers.newThread()) { _, d, _ ->
                try {
                    val fmDao = DbHelper.get(Constance.app)?.db?.sessionMsgDao()
                    d?.forEach { fi ->
                        fi.key = SessionLastMsgInfo.generateKey(com.zj.database.ut.Constance.KEY_OF_SESSIONS, groupId = fi.groupId)
                        fmDao?.insertOrUpdateSessionMsgInfo(fi)
                    }
                    mergeSessionAndPushToUi(prop)
                } finally {
                    cancel(prop)
                }
            }
        } else {
            finishAFetch(prop, true, "the sessions is null")
        }
    }

    private fun mergeSessionAndPushToUi(prop: FetchType) {
        val sessions = DbHelper.get(Constance.app)?.db?.sessionDao()?.allSessions
        val fmDao = DbHelper.get(Constance.app)?.db?.sessionMsgDao()
        sessions?.forEach { s ->
            val key = SessionLastMsgInfo.generateKey(com.zj.database.ut.Constance.KEY_OF_SESSIONS, groupId = s.groupId)
            s?.sessionMsgInfo = fmDao?.findSessionMsgInfoByKey(key)
        }
        IMHelper.postToUiObservers(sessions, null) {
            finishAFetch(prop, isSuccess = true)
        }
    }
}