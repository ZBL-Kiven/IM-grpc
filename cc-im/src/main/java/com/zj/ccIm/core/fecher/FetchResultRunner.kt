package com.zj.ccIm.core.fecher

import com.zj.ccIm.core.bean.FetchResult

abstract class FetchResultRunner : Runnable {

    internal lateinit var result: FetchResult

    abstract fun result(result: FetchResult)

    final override fun run() {
        result(result)
    }
}