package com.zj.ccIm.core.api

import android.util.Log
import com.zj.api.BaseApi
import com.zj.api.interceptor.HeaderProvider
import com.zj.api.interceptor.UrlProvider
import com.zj.api.interfaces.ErrorHandler
import com.zj.ccIm.core.IMHelper
import retrofit2.HttpException
import com.google.gson.Gson
import com.zj.api.base.BaseRetrofit
import com.zj.ccIm.core.bean.GetMsgReqBean
import com.zj.database.entity.MessageInfoEntity
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

    fun getRecordApi(h: HeaderProvider? = null): BaseApi<IMRecordSizeApi> {
        return BaseApi.create<IMRecordSizeApi>(EH).baseUrl(baseUrl).header(h ?: header).build()
    }

    fun getFunctionApi(): BaseApi<FunctionApi> {
        return BaseApi.create<FunctionApi>(EH).baseUrl(baseUrl).header(header).build()
    }

    fun getMsgList(param: GetMsgReqBean, result: (isSuccess: Boolean, data: Map<String, List<MessageInfoEntity?>?>?, throwable: HttpException?, a: Any?) -> Unit): BaseRetrofit.RequestCompo? {
        val channelString = param.channels.map { it.serializeName }.toTypedArray()
        return getRecordApi().call({ it.getOfflineMsgList(param.msgId, param.groupId, param.ownerId, param.targetUserid, channels = channelString) }, result)
    }

    object EH : ErrorHandler {

        override fun onError(throwable: Throwable?): Pair<Boolean, Any?> {
            var msgBody: Any? = null
            if (throwable is HttpException) {
                val errorInfo = throwable.response()?.body()?.toString()
                val errorString = throwable.response()?.errorBody()?.string()
                val errorCode = throwable.code()
                val errorBodyCode = throwable.response()?.code()
                if (errorBodyCode == SERVER_ERROR) {
                    msgBody = Gson().fromJson(errorString, HttpErrorBody::class.java)
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
            return Pair(false, msgBody)
        }

        private const val SERVER_ERROR = 555
        const val SENSITIVE_WORDS = 20004
        const val NOT_FOLLOWING = 20006

        class HttpErrorBody {
            var code: Int = 0
            var lang: String? = null
            var message: String? = null
            var name: String? = null
        }
    }
}