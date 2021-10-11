package com.zj.ccIm.core.fecher

import com.zj.im.chat.enums.ConnectionState
import com.zj.ccIm.core.Constance
import com.zj.ccIm.core.IMHelper
import com.zj.ccIm.logger.ImLogs


internal object Fetcher {

    const val SP_FETCH_SESSIONS_TS = "fetch_last_modify_ts"
    const val SP_FETCH_PRIVATE_OWNER_CHAT_SESSIONS_TS = "fetch_last_modify_owner_ts"

    fun init() {
        IMHelper.registerConnectionStateChangeListener("main_fetcher_observer") {
            when (it) {
                ConnectionState.CONNECTED -> {
                    IMHelper.pause(Constance.FETCH_SESSION_CODE)
                    BaseFetcher.startFetch(GroupSessionFetcher, PrivateOwnerSessionFetcher)
                }
                ConnectionState.CONNECTED_ERROR, ConnectionState.NETWORK_STATE_CHANGE -> {
                    cancelAll()
                }
                else -> {
                }
            }
        }
    }

    fun refresh(f: BaseFetcher) {
        BaseFetcher.refresh(f)
    }

    fun abortOrFinishFetch(type: FetchType, isSuccess: Boolean) {
        ImLogs.d("Fetcher", "fetch type of ${type.dealCls} <== ${if (isSuccess) "success" else "failed"}")
    }

    fun endOfFetch() {
        ImLogs.d("Fetcher", " Fetch finished !!")
        if (!IMHelper.tryToRegisterAfterConnected()) {
            IMHelper.resume(Constance.FETCH_SESSION_CODE)
        }
    }

    fun cancelAll() {
        BaseFetcher.cancelAll()
    }
}