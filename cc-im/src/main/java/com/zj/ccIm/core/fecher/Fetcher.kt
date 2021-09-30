package com.zj.ccIm.core.fecher

import com.zj.api.base.BaseRetrofit
import com.zj.database.DbHelper
import com.zj.im.chat.enums.ConnectionState
import com.zj.ccIm.core.Constance
import com.zj.ccIm.core.IMHelper
import com.zj.ccIm.core.api.ImApi
import com.zj.ccIm.core.sp.SPHelper
import com.zj.ccIm.error.FetchSessionResult
import com.zj.ccIm.logger.ImLogs
import io.reactivex.schedulers.Schedulers

internal object Fetcher {

    const val SP_FETCH_SESSIONS_TS = "fetch_last_modify_ts"
    const val SP_FETCH_PRIVATE_OWNER_CHAT_SESSIONS_TS = "fetch_last_modify_owner_ts"
    private var onFetching = false
    private val compos = mutableListOf<BaseRetrofit.RequestCompo>()

    fun init() {
        IMHelper.registerConnectionStateChangeListener("main_fetcher_observer") {
            when (it) {
                ConnectionState.CONNECTED -> {
                    IMHelper.pause(Constance.FETCH_SESSION_CODE)
                    onFetchSessions()
                }
                ConnectionState.CONNECTED_ERROR, ConnectionState.NETWORK_STATE_CHANGE -> {
                    cancel()
                }
                else -> {
                }
            }
        }
    }

    /**
     * all fetch tasks are running in work thread.
     * */
    fun onFetchSessions(onlyRefresh: Boolean = false) {
        if (!onlyRefresh && onFetching) return
        onFetching = true
        val lastTs = SPHelper[SP_FETCH_SESSIONS_TS, 0L] ?: 0L
        var compo: BaseRetrofit.RequestCompo? = null
        ImLogs.d("Fetcher", "start fetch sessions  by onlyRefresh = $onlyRefresh ,with last ts : $lastTs")
        val isFirstFetch = lastTs <= 0
        compo = ImApi.getFunctionApi().call({ it.fetchSessions(lastTs) }, Schedulers.io(), Schedulers.newThread()) { b, d, e ->
            try {
                val sessions = d?.groupList
                if (b) {
                    if (sessions.isNullOrEmpty()) {
                        if (isFirstFetch) {
                            ImLogs.d("Fetcher", "fetch sessions is null result for first time !")
                            finishFetch(onlyRefresh, isSuccess = true, isFirstFetch, isEmptyData = true)
                        } else {
                            ImLogs.d("Fetcher", "fetch sessions is null ,trying to fetch last msg by localed !")
                            onFetchLastMessage(onlyRefresh, isFirstFetch)
                        }
                    } else {
                        ImLogs.d("Fetcher", "fetch sessions success , new ts is ${d.timeStamp}, changed group is [${d.groupList?.joinToString { "${it.groupName} , " }}]")
                        SPHelper.put(SP_FETCH_SESSIONS_TS, d.timeStamp)
                        val sessionDao = DbHelper.get(Constance.app)?.db?.sessionDao()
                        sessions.forEach { s ->
                            if (s.groupStatus == 0 || s.groupStatus == 1) {
                                sessionDao?.insertOrChangeSession(s)
                            } else {
                                sessionDao?.deleteSession(s)
                            }
                        }
                        onFetchLastMessage(onlyRefresh, isFirstFetch)
                    }
                } else {
                    if (!onlyRefresh) IMHelper.reconnect("fetch sessions group failed with:${e?.message} !!")
                    finishFetch(onlyRefresh, false, isFirstFetch, true)
                }
            } finally {
                compos.remove(compo)
            }
        }
        if (compo != null) compos.add(compo)
    }

    private fun onFetchLastMessage(onlyRefresh: Boolean, isFirstFetch: Boolean) {
        var compo: BaseRetrofit.RequestCompo? = null
        val sessions = DbHelper.get(Constance.app)?.db?.sessionDao()?.allSessions
        if (!sessions.isNullOrEmpty()) {
            val gIds = sessions.map { ms -> ms.groupId }
            ImLogs.d("Fetcher", "start fetch session last message info by onlyRefresh = $onlyRefresh , groups = $gIds")
            compo = ImApi.getFunctionApi().call({ it.fetchSessionLastMessage(gIds) }, Schedulers.io(), Schedulers.newThread()) { b, d, e ->
                try {
                    val fmDao = DbHelper.get(Constance.app)?.db?.sessionMsgDao()
                    d?.forEach { fi ->
                        fmDao?.insertOrUpdateSessionMsgInfo(fi)
                    }
                    if (b && !d.isNullOrEmpty()) {
                        mergeSessionAndPushToUi(onlyRefresh, isFirstFetch)
                    } else {
                        if (!b && !onlyRefresh) IMHelper.reconnect("fetch sessions group failed with:${e?.message} !!")
                        finishFetch(onlyRefresh, b, isFirstFetch, false)
                    }
                } finally {
                    compos.remove(compo)
                }
            }
            if (compo != null) compos.add(compo)
        } else {
            finishFetch(onlyRefresh, true, isFirstFetch, true)
        }
    }

    private fun mergeSessionAndPushToUi(onlyRefresh: Boolean, isFirstFetch: Boolean) {
        val sessions = DbHelper.get(Constance.app)?.db?.sessionDao()?.allSessions
        val fmDao = DbHelper.get(Constance.app)?.db?.sessionMsgDao()
        sessions?.forEach { s ->
            s.sessionMsgInfo = fmDao?.findSessionMsgInfoBySessionId(s.groupId)
        }
        IMHelper.postToUiObservers(sessions, null) {
            finishFetch(onlyRefresh, true, isFirstFetch, sessions.isNullOrEmpty())
        }
    }

    private fun finishFetch(onlyRefresh: Boolean, isSuccess: Boolean, isFirstFetch: Boolean, isEmptyData: Boolean) {
        if (!onlyRefresh) {
            ImLogs.d("Fetcher", " Fetch finished !!")
            if (!IMHelper.tryToRegisterAfterConnected()) {
                IMHelper.resume(Constance.FETCH_SESSION_CODE)
            }
        }
        onFetching = false
        IMHelper.postToUiObservers(FetchSessionResult(isSuccess, isFirstFetch, isEmptyData))
    }

    fun cancel() {
        compos.forEach { it.cancel() }
        compos.clear()
        onFetching = false
    }
}