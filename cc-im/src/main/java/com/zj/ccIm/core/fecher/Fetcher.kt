package com.zj.ccIm.core.fecher

import com.zj.im.chat.enums.ConnectionState
import com.zj.ccIm.core.Constance
import com.zj.ccIm.CcIM
import com.zj.ccIm.MainLooper
import com.zj.ccIm.core.IMChannelManager
import com.zj.ccIm.core.IMHelper
import com.zj.ccIm.core.bean.FetchResult
import com.zj.ccIm.logger.ImLogs


internal object Fetcher {

    private var FETCH_CODE_INCREMENT: Int = 0
        get() {
            return field++
        }
    private val cachedResultListener = mutableMapOf<String, MutableMap<Int, FetchResultRunner>>()

    fun init() {
        CcIM.registerConnectionStateChangeListener("main_fetcher_observer") {
            when (it) {
                is ConnectionState.CONNECTED -> {
                    CcIM.pause(Constance.FETCH_SESSION_CODE)
                    BaseFetcher.startFetch(GroupSessionFetcher, PrivateOwnerSessionFetcher)
                }
                is ConnectionState.ERROR, is ConnectionState.OFFLINE -> {
                    cancelAll()
                }
                else -> {
                }
            }
        }
    }

    fun refresh(f: BaseFetcher, result: FetchResultRunner) {
        fun pushFetcher(code: Int) {
            val cache = cachedResultListener[f.getPayload()]
            if (cache == null) {
                cachedResultListener[f.getPayload()] = mutableMapOf()
                pushFetcher(code)
            } else {
                cache[code] = result
            }
        }

        val code = FETCH_CODE_INCREMENT
        synchronized(this) {
            pushFetcher(code)
            BaseFetcher.refresh(code, f)
        }
    }

    fun notifyNodeEnd(prop: FetchType, result: FetchResult?) {
        ImLogs.d("Fetcher", " Fetch node end with : ${prop.dealCls?.getPayload()}!!")
        if (result != null) endOfRefresh(prop, result, false)
    }

    fun endOfFetch(prop: FetchType, result: FetchResult?) {
        ImLogs.d("Fetcher", " Fetch finished with last : ${prop.dealCls?.getPayload()}!!")
        if (result != null) endOfRefresh(prop, result, false)
        if (!IMChannelManager.tryToRegisterAfterConnected()) {
            CcIM.resume(Constance.FETCH_SESSION_CODE)
        }
    }

    fun endOfRefresh(prop: FetchType, result: FetchResult, formRefresh: Boolean = true) {
        prop.fetchIds.forEach {
            val r = cachedResultListener.remove(prop.dealCls?.getPayload())?.get(it) ?: return@forEach
            r.result = result
            MainLooper.post(r)
        }
        if (formRefresh) ImLogs.d("Fetcher", "on refresh result : ${result.success}")
        CcIM.postToUiObservers(result, prop.dealCls?.getPayload())
    }

    fun cancelAll() {
        BaseFetcher.cancelAll()
    }

    fun resetIncrementTsForProp(prop: FetchType) {
        when (prop.dealCls) {
            GroupSessionFetcher::class.java -> {
                IMHelper.getDbHelper()?.clearSessionFetchingTs()
            }
            PrivateOwnerSessionFetcher::class.java -> {
                IMHelper.getDbHelper()?.clearPrivateOwnerSessionFetchingTs()
            }
        }
    }
}