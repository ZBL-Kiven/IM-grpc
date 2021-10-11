package com.zj.ccIm.core.fecher

import com.zj.api.base.BaseRetrofit
import com.zj.ccIm.core.IMHelper
import com.zj.ccIm.core.catching
import java.util.concurrent.LinkedBlockingDeque

internal abstract class BaseFetcher {

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
            from.forEach { getOrCreateFetcher(it, FetchType.FETCH_FLAG_PENDING) }
            fetch()
        }

        fun refresh(vararg f: BaseFetcher) {
            f.forEach { getOrCreateFetcher(it, FetchType.FETCH_FLAG_REFRESH) }
            fetch()
        }

        private fun fetch() {
            val prop = cachedFetchers.poll()
            if (prop == null) {
                Fetcher.endOfFetch();return
            }
            if (prop.dealCls?.selfInFetching == true) return
            prop.dealCls?.selfInFetching = true
            catching({
                prop.compo = prop.dealCls?.startFetch(prop)
            }, {
                prop.dealCls?.selfInFetching = false
            })
        }

        private fun getOrCreateFetcher(from: BaseFetcher, flags: Int): FetchType {
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
            return f
        }
    }

    protected fun cancel(prop: FetchType) {
        prop.compo?.cancel()
    }

    protected fun finishAFetch(prop: FetchType, isSuccess: Boolean, case: String = "") {
        selfInFetching = false
        if (prop.flags.and(FetchType.FETCH_FLAG_PENDING) != 0) {
            if (isSuccess) {
                if (cachedFetchers.all { prop.flags.and(FetchType.FETCH_FLAG_PENDING) == 0 }) {
                    fetching = false
                    selfInFetching = false
                    Fetcher.endOfFetch()
                } else {
                    fetch()
                }
            } else {
                fetching = false
                IMHelper.reconnect("fetch failed , case :$case !!")
            }
        }
        if (prop.flags.and(FetchType.FETCH_FLAG_REFRESH) != 0) {
            Fetcher.abortOrFinishFetch(prop, isSuccess)
        }
    }

    abstract fun startFetch(prop: FetchType): BaseRetrofit.RequestCompo?
}