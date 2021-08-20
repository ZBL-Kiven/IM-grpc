package com.zj.ccIm.core.api

import com.zj.api.BaseApi
import com.zj.api.interceptor.HeaderProvider
import com.zj.api.interceptor.UrlProvider
import com.zj.ccIm.core.IMHelper

object ImApi {

    private val baseUrl = object : UrlProvider() {
        override fun url(): String {
            return IMHelper.imConfig.getIMHost()
        }
    }
    private val header = object : HeaderProvider {
        override fun headers(): Map<out String, String> {
            return mutableMapOf("Content-Type" to "multipart/form-data", "userId" to "${IMHelper.imConfig.getUserId()}", "token" to IMHelper.imConfig.getToken())
        }
    }

    fun getSenderApi(): BaseApi<SenderApi> {
        return BaseApi.create<SenderApi>().baseUrl(baseUrl).header(header).build()
    }


    fun getFetcherApi(): BaseApi<FetcherApi> {
        return BaseApi.create<FetcherApi>().baseUrl(baseUrl).header(header).build()
    }


}