package com.zj.ccIm.core.fecher

import com.zj.api.base.BaseRetrofit
import com.zj.ccIm.core.Constance
import com.zj.ccIm.core.IMHelper
import com.zj.ccIm.core.api.ImApi
import com.zj.ccIm.core.sp.SPHelper
import com.zj.ccIm.error.FetchPrivateChatSessionResult
import com.zj.ccIm.logger.ImLogs
import com.zj.database.DbHelper
import com.zj.database.entity.SessionLastMsgInfo
import io.reactivex.schedulers.Schedulers

internal object PrivateOwnerSessionFetcher : BaseFetcher() {

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
                            IMHelper.postToUiObservers(FetchPrivateChatSessionResult(success = true, isFirstFetch = true, isNullData = true))
                            DbHelper.get(Constance.app)?.db?.privateChatOwnerDao()?.deleteAll()
                            finishAFetch(prop, true)
                        } else {
                            ImLogs.d("PrivateOwnerSessionFetcher", "fetch owner group is null ,trying to fetch last msg by localed !")
                            mergeSessionAndPushToUi(prop)
                        }
                    } else {
                        ImLogs.d("PrivateOwnerSessionFetcher", "fetch owner group success, new ts is ${d.timeStamp}, changed group is [${sessions.joinToString { "${it.ownerName} , " }}]")
                        SPHelper.put(Fetcher.SP_FETCH_PRIVATE_OWNER_CHAT_SESSIONS_TS, d.timeStamp)
                        val sessionDao = DbHelper.get(Constance.app)?.db?.privateChatOwnerDao()
                        sessions.forEach { s ->
                            sessionDao?.insertOrUpdate(s)
                        }
                        mergeSessionAndPushToUi(prop)
                    }
                } else {
                    finishAFetch(prop, isSuccess = false, "PrivateOwnerSessionFetcher: fetch owner group failed with:${e?.message} !!")
                }
            } catch (e: Exception) {
                cancel(prop)
                IMHelper.postError(e)
            }
        }
    }

    private fun mergeSessionAndPushToUi(prop: FetchType) {
        val sessions = DbHelper.get(Constance.app)?.db?.privateChatOwnerDao()?.findAll()
        val fmDao = DbHelper.get(Constance.app)?.db?.sessionMsgDao()
        sessions?.forEach { s ->
            val key = SessionLastMsgInfo.generateKey(com.zj.database.ut.Constance.KEY_OF_PRIVATE_OWNER, ownerId = s.ownerId)
            s?.sessionMsgInfo = fmDao?.findSessionMsgInfoByKey(key)
        }
        IMHelper.postToUiObservers(sessions, null) {
            finishAFetch(prop, isSuccess = true)
        }
    }
}