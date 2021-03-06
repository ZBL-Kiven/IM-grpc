package com.zj.ccIm.core.fecher

import com.zj.api.base.BaseRetrofit

internal class FetchType {

    companion object {
        const val FETCH_FLAG_PENDING = 1
        const val FETCH_FLAG_REFRESH = 2
    }

    var fetchIds = mutableSetOf<Int>()
    var flags: Int = 0
    var dealCls: BaseFetcher? = null
    var compo: BaseRetrofit.RequestCompo? = null

    override fun equals(other: Any?): Boolean {
        return (other is FetchType) && other.dealCls == dealCls
    }

    override fun hashCode(): Int {
        return dealCls?.hashCode() ?: 0
    }
}