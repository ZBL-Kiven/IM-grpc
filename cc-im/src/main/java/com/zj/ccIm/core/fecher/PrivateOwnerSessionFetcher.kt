package com.zj.ccIm.core.fecher

import com.zj.api.base.BaseRetrofit
import com.zj.ccIm.core.IMHelper
import com.zj.ccIm.core.api.ImApi
import com.zj.ccIm.core.bean.FetchResult
import com.zj.ccIm.core.impl.ClientHubImpl
import com.zj.ccIm.core.sp.SPHelper
import com.zj.ccIm.logger.ImLogs
import com.zj.database.entity.PrivateOwnerEntity
import io.reactivex.schedulers.Schedulers

internal object PrivateOwnerSessionFetcher : BaseFetcher() {

    override fun getPayload(): String {
        return ClientHubImpl.PAYLOAD_FETCH_OWNER_SESSION
    }

    override fun startFetch(prop: FetchType): BaseRetrofit.RequestCompo? {
        val lastTs = SPHelper[Fetcher.SP_FETCH_PRIVATE_OWNER_CHAT_SESSIONS_TS, 0L] ?: 0L
        ImLogs.d("PrivateOwnerSessionFetcher", "start fetch owner group by ${prop.flags} ,with last ts : $lastTs")
        val isFirstFetch = lastTs <= 0
        return ImApi.getFunctionApi().call({ it.fetchPrivateOwnerSessions(lastTs) }, Schedulers.io(), Schedulers.newThread()) { b, d, e ->
            try {
                val sessions = d?.ownerList
                if (b) {
                    if (sessions.isNullOrEmpty()) {
                        if (isFirstFetch) {
                            ImLogs.d("PrivateOwnerSessionFetcher", "fetch owner group is null result for first time !")
                            IMHelper.getDb()?.privateChatOwnerDao()?.deleteAll()
                            finishAFetch(prop, FetchResult(true, isFirstFetch, true))
                        } else {
                            ImLogs.d("PrivateOwnerSessionFetcher", "fetch owner group is null ,trying to fetch last msg by localed !")
                            fetchLastMsg(prop, isFirstFetch)
                        }
                    } else {
                        ImLogs.d("PrivateOwnerSessionFetcher", "fetch owner group success, new ts is ${d.timeStamp}, changed group is [${sessions.joinToString { "${it.ownerName} , " }}]")
                        SPHelper.put(Fetcher.SP_FETCH_PRIVATE_OWNER_CHAT_SESSIONS_TS, d.timeStamp)
                        val sessionDao = IMHelper.getDb()?.privateChatOwnerDao()
                        sessions.forEach { s ->
                            sessionDao?.insertOrUpdate(s)
                        }
                        fetchLastMsg(prop, isFirstFetch)
                    }
                } else {
                    finishAFetch(prop, FetchResult(false, isFirstFetch, true, "PrivateOwnerSessionFetcher: fetch owner group failed with:${e?.message} !!"))
                }
            } catch (e: Exception) {
                cancel(prop)
                IMHelper.postError(e)
            }
        }
    }

    private fun fetchLastMsg(prop: FetchType, isFirstFetch: Boolean) {
        val sessions = IMHelper.getDb()?.privateChatOwnerDao()?.findAll()
        if (!sessions.isNullOrEmpty()) {
            val oIds = sessions.map { ms -> ms.ownerId }
            ImLogs.d("GroupSessionFetcher", "start fetch private owner sessions last message info by  ${prop.flags} , owners = $oIds")
            prop.compo = ImApi.getFunctionApi().call({ it.fetchPrivateOwnerLastMessage(oIds) }, Schedulers.io(), Schedulers.newThread()) { _, d, _ ->
                try {
                    val fmDao = IMHelper.getDb()?.sessionMsgDao()
                    d?.forEach { fi ->
                        fi.key = com.zj.database.ut.Constance.generateKey(com.zj.database.ut.Constance.KEY_OF_PRIVATE_OWNER, ownerId = fi.ownerId)
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
        val sessions = IMHelper.getDb()?.privateChatOwnerDao()?.findAll()
        val fmDao = IMHelper.getDb()?.sessionMsgDao()
        sessions?.forEach { s ->
            val key = com.zj.database.ut.Constance.generateKey(com.zj.database.ut.Constance.KEY_OF_PRIVATE_OWNER, ownerId = s.ownerId)
            s?.sessionMsgInfo = fmDao?.findSessionMsgInfoByKey(key)
        }
        IMHelper.postToUiObservers(PrivateOwnerEntity::class.java, sessions, null) {
            finishAFetch(prop, FetchResult(true, isFirstFetch, false))
        }
    }
}