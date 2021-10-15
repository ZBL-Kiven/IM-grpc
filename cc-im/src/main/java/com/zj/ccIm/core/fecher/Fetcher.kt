package com.zj.ccIm.core.fecher

import android.os.Handler
import android.os.Looper
import com.zj.im.chat.enums.ConnectionState
import com.zj.ccIm.core.Constance
import com.zj.ccIm.core.IMHelper
import com.zj.ccIm.core.bean.FetchResult
import com.zj.ccIm.core.sp.SPHelper
import com.zj.ccIm.logger.ImLogs
import java.lang.ref.WeakReference


internal object Fetcher {

    const val SP_FETCH_SESSIONS_TS = "fetch_last_modify_ts"
    const val SP_FETCH_PRIVATE_OWNER_CHAT_SESSIONS_TS = "fetch_last_modify_owner_ts"
    private var FETCH_CODE_INCREMENT: Int = 0
        get() {
            return field++
        }
    private val cachedResultListener = mutableMapOf<Int, WeakReference<FetchResultRunner>>()
    private val handler = Handler(Looper.getMainLooper())

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

    fun refresh(f: BaseFetcher, result: FetchResultRunner) {
        val code = FETCH_CODE_INCREMENT
        synchronized(this) {
            cachedResultListener[code] = WeakReference(result)
            BaseFetcher.refresh(code, f)
        }
    }

    fun endOfFetch(prop: FetchType, result: FetchResult?) {
        ImLogs.d("Fetcher", " Fetch finished !!")
        if (result != null) endOfRefresh(prop, result, false)
        if (!IMHelper.tryToRegisterAfterConnected()) {
            IMHelper.resume(Constance.FETCH_SESSION_CODE)
        }
    }

    fun endOfRefresh(prop: FetchType, result: FetchResult, formRefresh: Boolean = true) {
        prop.fetchIds.forEach {
            val r = cachedResultListener.remove(it)?.get() ?: return
            r.result = result
            handler.post(r)
        }
        if (formRefresh) ImLogs.d("Fetcher", "on refresh result : ${result.success}")
        IMHelper.postToUiObservers(result, prop.dealCls?.getPayload())
    }

    fun cancelAll() {
        BaseFetcher.cancelAll()
        handler.removeCallbacksAndMessages(null)
    }

    fun resetIncrementTsForProp(prop: FetchType) {
        when (prop.dealCls) {
            GroupSessionFetcher::class.java -> {
                SPHelper.put(SP_FETCH_SESSIONS_TS, 0)
            }
            PrivateOwnerSessionFetcher::class.java -> {
                SPHelper.put(SP_FETCH_PRIVATE_OWNER_CHAT_SESSIONS_TS, 0)
            }
        }
    }
}