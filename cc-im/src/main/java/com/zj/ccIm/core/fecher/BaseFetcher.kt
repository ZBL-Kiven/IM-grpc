package com.zj.ccIm.core.fecher


import com.zj.api.base.BaseRetrofit
import com.zj.ccIm.CcIM
import com.zj.ccIm.core.bean.FetchResult
import com.zj.ccIm.core.catching
import java.util.concurrent.LinkedBlockingDeque

internal abstract class BaseFetcher {

    abstract fun getPayload(): String

    private var selfInFetching = false

    companion object {
        private var fetching = false
        private val cachedFetchers = LinkedBlockingDeque<FetchType>()

        fun cancelAll() {
            catching {
                cachedFetchers.forEach { it.compo?.cancel() }
                cachedFetchers.clear()
            }
        }

        fun startFetch(vararg from: BaseFetcher) {
            if (fetching) return
            fetching = true
            from.forEach { getOrCreateFetcher(it, null, FetchType.FETCH_FLAG_PENDING) }
            fetch()
        }

        fun refresh(fetchId: Int, vararg f: BaseFetcher) {
            f.forEach { getOrCreateFetcher(it, fetchId, FetchType.FETCH_FLAG_REFRESH) }
            fetch()
        }

        private fun fetch() {
            val prop = cachedFetchers.poll()
            if (prop == null) {
                Fetcher.endOfFetch(FetchType(), null);return
            }
            if (prop.dealCls?.selfInFetching == true) return
            prop.dealCls?.selfInFetching = true
            catching({
                prop.compo = prop.dealCls?.startFetch(prop)
            }, {
                prop.dealCls?.selfInFetching = false
            })
        }

        private fun getOrCreateFetcher(from: BaseFetcher, fetchId: Int?, flags: Int): FetchType {
            var f = cachedFetchers.firstOrNull { it.dealCls?.equals(from) == true }
            if (f == null) {
                f = FetchType()
                cachedFetchers.push(f)
            }
            if (f.dealCls != from) {
                f.dealCls?.cancel(f)
                f.dealCls = from
            }
            f.flags = f.flags or flags
            if (fetchId != null) f.fetchIds.add(fetchId)
            return f
        }
    }

    protected fun cancel(prop: FetchType, e: Throwable? = null) {
        prop.compo?.cancel()
        if (e != null) {
            fetching = false
            selfInFetching = false
            cachedFetchers.clear()
            CcIM.postError(e)
            CcIM.reconnect("fetch failed , case : Fetcher was canceled by error ${e.message}!!")
        }
    }

    protected fun finishAFetch(prop: FetchType, result: FetchResult) {
        selfInFetching = false
        if (prop.flags.and(FetchType.FETCH_FLAG_PENDING) != 0) {
            if (result.success) {
                if (cachedFetchers.all { prop.flags.and(FetchType.FETCH_FLAG_PENDING) == 0 }) {
                    fetching = false
                    selfInFetching = false
                    Fetcher.endOfFetch(prop, result)
                } else {
                    Fetcher.notifyNodeEnd(prop, result)
                    fetch()
                }
            } else {
                fetching = false
                Fetcher.resetIncrementTsForProp(prop)
                CcIM.reconnect("fetch failed , case :${result.errorMsg} !!")
            }
        }
        if (prop.flags.and(FetchType.FETCH_FLAG_REFRESH) != 0) {
            Fetcher.endOfRefresh(prop, result)
        }
    }

    abstract fun startFetch(prop: FetchType): BaseRetrofit.RequestCompo?
}