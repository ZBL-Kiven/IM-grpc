package com.zj.ccIm.core.api

import android.util.Log
import com.zj.api.BaseApi
import com.zj.api.interceptor.HeaderProvider
import com.zj.api.interceptor.UrlProvider
import com.zj.api.interfaces.ErrorHandler
import com.zj.ccIm.core.IMHelper
import retrofit2.HttpException
import com.google.gson.Gson
import java.net.UnknownHostException


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
        return BaseApi.create<SenderApi>(EH).baseUrl(baseUrl).header(header).build()
    }

    fun getOptionApi(): BaseApi<OptionApi> {
        return BaseApi.create<OptionApi>(EH).baseUrl(baseUrl).header(header).build()
    }

    fun getFetcherApi(): BaseApi<FetcherApi> {
        return BaseApi.create<FetcherApi>(EH).baseUrl(baseUrl).header(header).build()
    }

    object EH : ErrorHandler {

        override fun onError(throwable: Throwable?): Boolean {
            if (throwable is HttpException) {
                val errorInfo = throwable.response()?.body()?.toString()
                val errorString = throwable.response()?.errorBody()?.string()
                val errorCode = throwable.code()
                val errorBodyCode = throwable.response()?.code()
                if (errorBodyCode == SERVER_ERROR) {
                    val errorBody = Gson().fromJson(errorString, HttpErrorBody::class.java)
                    errorBody?.throwable = throwable
                    if (!resolveProfError(errorBody)) return true
                }
                Log.e("request error", " ----- case: $errorInfo \ndetail = $errorString  \nerrorCode = $errorCode \nerrorBodyCode = $errorBodyCode")
            } else {
                try {
                    if (throwable is UnknownHostException) {
                        Log.e("http test", "net work error with UnknownHostException")
                    } else {
                        Log.e("http test", "onHttpError ----- case: ${throwable?.message}")
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            throwable?.printStackTrace()
            return false
        }

        private fun resolveProfError(error: HttpErrorBody?): Boolean {
            return when (error?.code) {
                AUTH_FAIL -> {
                    Log.e("----- ", "token expired");true
                }
                else -> false
            }
        }

        const val SERVER_ERROR = 555
        const val AUTH_FAIL = 10009

        class HttpErrorBody {
            var code: Int = 0
            var lang: String? = null
            var message: String? = null
            var debug: String? = null
            var name: String? = null
            var throwable: Throwable? = null
        }
    }
}