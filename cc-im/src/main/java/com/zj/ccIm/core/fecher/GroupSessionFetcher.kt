package com.zj.ccIm.core.fecher

import com.zj.api.base.BaseRetrofit
import com.zj.ccIm.core.Constance
import com.zj.ccIm.core.IMHelper
import com.zj.ccIm.core.api.ImApi
import com.zj.ccIm.core.bean.FetchResult
import com.zj.ccIm.core.impl.ClientHubImpl
import com.zj.ccIm.core.sp.SPHelper
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

    override fun getPayload(): String {
        return ClientHubImpl.PAYLOAD_FETCH_GROUP_SESSION
    }

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
                            DbHelper.get(Constance.app)?.db?.sessionDao()?.deleteAll()
                            finishAFetch(prop, FetchResult(true, isFirstFetch, true))
                        } else {
                            ImLogs.d("GroupSessionFetcher", "fetch sessions is null ,trying to fetch last msg by localed !")
                            fetchLastMsg(prop, isFirstFetch)
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
                        fetchLastMsg(prop, isFirstFetch)
                    }
                } else {
                    finishAFetch(prop, FetchResult(false, isFirstFetch, true, "fetch sessions group failed with:${e?.message} !!"))
                }
            } catch (e: Exception) {
                cancel(prop)
                IMHelper.postError(e)
            }
        }
    }

    private fun fetchLastMsg(prop: FetchType, isFirstFetch: Boolean) {
        val sessions = DbHelper.get(Constance.app)?.db?.sessionDao()?.allSessions
        if (!sessions.isNullOrEmpty()) {
            val gIds = sessions.map { ms -> ms.groupId }
            ImLogs.d("GroupSessionFetcher", "start fetch session last message info by  ${prop.flags} , groups = $gIds")
            prop.compo = ImApi.getFunctionApi().call({ it.fetchSessionLastMessage(gIds) }, Schedulers.io(), Schedulers.newThread()) { _, d, _ ->
                try {
                    val fmDao = DbHelper.get(Constance.app)?.db?.sessionMsgDao()
                    d?.forEach { fi ->
                        fi.key = com.zj.database.ut.Constance.generateKey(com.zj.database.ut.Constance.KEY_OF_SESSIONS, groupId = fi.groupId)
                        fmDao?.insertOrUpdateSessionMsgInfo(fi)
                    }
                    mergeSessionAndPushToUi(prop, isFirstFetch)
                } finally {
                    cancel(prop)
                }
            }
        } else {
            finishAFetch(prop, FetchResult(true, isFirstFetch, true, "the sessions is null"))
        }
    }

    private fun mergeSessionAndPushToUi(prop: FetchType, isFirstFetch: Boolean) {
        val sessions = DbHelper.get(Constance.app)?.db?.sessionDao()?.allSessions
        val fmDao = DbHelper.get(Constance.app)?.db?.sessionMsgDao()
        sessions?.forEach { s ->
            val key = com.zj.database.ut.Constance.generateKey(com.zj.database.ut.Constance.KEY_OF_SESSIONS, groupId = s.groupId)
            s?.sessionMsgInfo = fmDao?.findSessionMsgInfoByKey(key)
        }
        IMHelper.postToUiObservers(sessions, null) {
            finishAFetch(prop, FetchResult(true, isFirstFetch, false))
        }
    }
}