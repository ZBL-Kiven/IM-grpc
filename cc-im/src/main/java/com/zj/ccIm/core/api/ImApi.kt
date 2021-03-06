package com.zj.ccIm.core.api

import android.util.Log
import com.zj.api.BaseApi
import com.zj.api.interceptor.HeaderProvider
import com.zj.api.interceptor.UrlProvider
import com.zj.api.interfaces.ErrorHandler
import com.zj.ccIm.CcIM
import retrofit2.HttpException
import com.google.gson.Gson
import com.zj.api.base.BaseRetrofit
import com.zj.ccIm.core.bean.ChannelRegisterInfo
import com.zj.ccIm.error.InitializedException
import com.zj.database.entity.MessageInfoEntity
import io.reactivex.schedulers.Schedulers
import java.net.UnknownHostException


object ImApi {

    private val baseUrl = object : UrlProvider() {
        override fun url(): String {
            return CcIM.imConfig?.getIMHost() ?: ""
        }
    }
    private val header = object : HeaderProvider {
        override fun headers(): Map<out String, String> {
            return CcIM.imConfig?.getApiHeader() ?: throw InitializedException("the configuration header must not be null!")
        }
    }

    fun getRecordApi(h: HeaderProvider? = null): BaseApi<IMRecordSizeApi> {
        return BaseApi.create<IMRecordSizeApi>(EH).baseUrl(baseUrl).header(h ?: header).build()
    }

    fun getFunctionApi(): BaseApi<FunctionApi> {
        return BaseApi.create<FunctionApi>(EH).baseUrl(baseUrl).header(header).build()
    }

    fun getMsgList(param: ChannelRegisterInfo, result: (isSuccess: Boolean, data: Map<String, List<MessageInfoEntity?>?>?, throwable: HttpException?, a: Any?) -> Unit): BaseRetrofit.RequestCompo? {
        val channelString = param.mChannel.serializeName
        return getRecordApi().call({ it.getOfflineMsgList(param.msgId, param.groupId, param.ownerId, param.targetUserid, param.type, channels = arrayOf(channelString)) }, Schedulers.io(), Schedulers.io(), result)
    }

    object EH : ErrorHandler {

        private const val SERVER_ERROR = 555
        const val NOT_ENOUGH = 125 // 资金不够，无法发送有偿消息
        const val REPEAT_ANSWER = 20001 // 重复回答消息
        const val GROUP_STOPPED = 20002 // 群组被冻结，消息不可发出
        const val NOT_OWNER = 20003 //对方不是 CO
        const val SENSITIVE_WORD_ERROR = 300110 // 触发被 CO 设置的敏感词，消息不可发送
        const val GROUP_MEMBER_NOT_EXIST = 20006 // 该成员未关注此 ClapHouse
        const val DIAMOND_NOT_ENOUGH = 20007 // 钻石不够，无法发送有偿消息

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

        class HttpErrorBody {
            var code: Int = 0
            var lang: String? = null
            var message: String? = null
            var name: String? = null
        }
    }
}