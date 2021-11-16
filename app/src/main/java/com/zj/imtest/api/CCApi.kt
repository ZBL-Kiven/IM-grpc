package com.zj.imtest.api

import com.zj.api.BaseApi
import com.zj.api.interceptor.HeaderProvider
import com.zj.api.interceptor.UrlProvider
import com.zj.imtest.IMConfig

object CCApi {

    const val api_url = "https://api.ccdev.lerjin.com"


    fun getTestApi(): BaseApi<TestApi> {
        return BaseApi.create(TestApi::class.java).baseUrl(ccUrl).header(baseHeader).timeOut(3000).build()
    }

    /**
     * the URL with the publishing address [https://api.cc.lerjin.com], refer to the constant [ClipClapsConstant#api_url]
     **/
    private val ccUrl = object : UrlProvider() {
        override fun url(): String {
            return api_url
        }
    }

    /**
     * Corresponding to the Header used by lerjin newer server
     * */
    private val baseHeader = object : HeaderProvider {
        override fun headers(): Map<String, String> {
            val token = IMConfig.getToken()
            val userId = IMConfig.getUserId()
            return hashMapOf<String, String>().apply {
                this["Content-Type"] = "application/json"
                this["token"] = token
                this["charset"] = "UTF-8"
                this["userId"] = "$userId"
                this["ostype"] = "android"
            }
        }
    }
}