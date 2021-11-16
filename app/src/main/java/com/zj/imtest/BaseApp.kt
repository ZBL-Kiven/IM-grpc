package com.zj.imtest

import android.app.Application
import android.os.Build
import com.zj.api.interceptor.HeaderProvider
import com.zj.ccIm.core.IMHelper
import java.util.*

class BaseApp : Application() {

    companion object {

        lateinit var context: Application

        fun initChat() {

            //初始化 IM 聊天 模块，（异步完成）
            IMHelper.init(context, IMConfig)
        }

        fun getHeader(): Map<String, String> {
            val token = IMConfig.getToken()
            val userId = IMConfig.getUserId()
            if (token.isEmpty() || userId == 0) {
                throw NullPointerException("the header params [userId , token] was null or empty , returened by service creating!")
            }
            return hashMapOf<String, String>().apply {
                this["Content-Type"] = "application/json"
                this["token"] = token
                this["charset"] = "UTF-8"
                this["userId"] = "$userId"
                this["ostype"] = "android"
                this["lang"] = "zh-cn"
                this["sysver"] = Build.VERSION.RELEASE
                this["apiver"] = "42"
                this["appver"] = "3.5.7"
                this["timezone"] = (TimeZone.getDefault().getOffset(System.currentTimeMillis()) / (3600 * 1000)).toString()
                this["uuid"] = "TEST_IM_${UUID.randomUUID()}"
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        context = this
        initChat()
    }
}