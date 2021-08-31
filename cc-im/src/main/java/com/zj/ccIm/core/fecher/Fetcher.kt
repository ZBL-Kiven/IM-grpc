package com.zj.ccIm.core.fecher

import com.zj.api.base.BaseRetrofit
import com.zj.database.DbHelper
import com.zj.im.chat.enums.ConnectionState
import com.zj.im.chat.poster.log
import com.zj.ccIm.core.Constance
import com.zj.ccIm.core.IMHelper
import com.zj.ccIm.core.api.ImApi
import com.zj.ccIm.core.impl.ClientHubImpl
import com.zj.ccIm.core.sp.SPHelper
import com.zj.ccIm.error.IMError
import io.reactivex.schedulers.Schedulers

internal object Fetcher {

    private const val SP_FETCH_SESSIONS_TS = "fetch_sessions_ts"
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
        log("start fetch sessions  by onlyRefresh = $onlyRefresh ,with last ts : $lastTs")
        compo = ImApi.getFetcherApi().call({ it.fetchSessions(lastTs) }, Schedulers.io(), Schedulers.newThread()) { b, d, e ->
            try {
                if (b && d != null) {
                    log("fetch sessions success , new ts is ${d.timeStamp}, changed group is [${d.groupList?.joinToString { "${it.groupName} , " }}]")
                    SPHelper.put(SP_FETCH_SESSIONS_TS, d.timeStamp)
                    val sl = d.groupList
                    if (!sl.isNullOrEmpty()) {
                        Constance.app?.let {
                            val sessionDao = DbHelper.get(it)?.db?.sessionDao()
                            sl.forEach { s ->
                                if (s.groupStatus == 0) {
                                    sessionDao?.insertOrChangeSession(s)
                                } else {
                                    sessionDao?.deleteSession(s)
                                }
                            }
                            onFetchLastMessage(onlyRefresh)
                        } ?: onFetchLastMessage(onlyRefresh)
                    } else {
                        onFetchLastMessage(onlyRefresh)
                    }
                } else {
                    if (!b && !onlyRefresh) IMHelper.reconnect("fetch sessions group failed with:${e?.message} !!")
                    onFetching = false
                    if (b && d?.groupList.isNullOrEmpty()) {
                        IMHelper.postToUiObservers(IMError("Fetch session group is null"), ClientHubImpl.PAYLOAD_FETCH_SESSION_NULL) {}
                    }
                }
            } finally {
                compos.remove(compo)
            }
        }
        if (compo != null) compos.add(compo)
    }

    private fun onFetchLastMessage(onlyRefresh: Boolean) {
        var compo: BaseRetrofit.RequestCompo? = null
        val sessions = Constance.app?.let { DbHelper.get(it)?.db?.sessionDao()?.allSessions }
        if (!sessions.isNullOrEmpty()) {
            val gIds = sessions.map { ms -> ms.groupId }
            log("start fetch session last message info by onlyRefresh = $onlyRefresh , groups = $gIds")
            if (!gIds.isNullOrEmpty()) compo = ImApi.getFetcherApi().call({ it.fetchSessionLastMessage(gIds) }, Schedulers.io(), Schedulers.newThread()) { b, d, e ->
                try {
                    Constance.app?.let {
                        val fmDao = DbHelper.get(it)?.db?.sessionMsgDao()
                        d?.forEach { fi ->
                            fmDao?.insertOrUpdateSessionMsgInfo(fi)
                        }
                    }
                    if (b && !d.isNullOrEmpty()) {
                        mergeSessionAndPushToUi(onlyRefresh)
                    } else {
                        if (!b && !onlyRefresh) IMHelper.reconnect("fetch sessions group failed with:${e?.message} !!")
                        finishFetch(onlyRefresh)
                    }
                } finally {
                    compos.remove(compo)
                }
            } else {
                finishFetch(onlyRefresh)
            }
            if (compo != null) compos.add(compo)
        } else {
            finishFetch(onlyRefresh)
        }
    }

    private fun mergeSessionAndPushToUi(onlyRefresh: Boolean) {
        Constance.app?.let { app ->
            val sessions = DbHelper.get(app)?.db?.sessionDao()?.allSessions
            val fmDao = DbHelper.get(app)?.db?.sessionMsgDao()
            sessions?.forEach { s ->
                s.sessionMsgInfo = fmDao?.findSessionMsgInfoBySessionId(s.groupId)
            }
            IMHelper.postToUiObservers(sessions, null) {
                finishFetch(onlyRefresh)
            }
        } ?: finishFetch(onlyRefresh)
    }

    private fun finishFetch(onlyRefresh: Boolean) {
        if (!onlyRefresh) {
            log(" Fetch finished !!")
            if (!IMHelper.tryToRegisterAfterConnected()) {
                IMHelper.resume(Constance.FETCH_SESSION_CODE)
            }
        }
        onFetching = false
    }

    fun cancel() {
        compos.forEach { it.cancel() }
        compos.clear()
        onFetching = false
    }
}