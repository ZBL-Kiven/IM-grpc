package com.zj.imtest

import android.app.Application
import com.zj.ccIm.core.IMHelper

class BaseApp : Application() {

    companion object {

        lateinit var context: Application

        fun initChat() {

            //初始化 IM 聊天 模块，（异步完成）
           IMHelper.init(context, IMConfig)
        }
    }

    override fun onCreate() {
        super.onCreate()
        context = this
        initChat()
    }
}