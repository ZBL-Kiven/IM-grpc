package com.zj.imtest

import android.app.Activity
import android.app.Application
import android.os.Build
import android.os.Bundle
import com.zj.ccIm.core.IMHelper
import com.zj.imtest.ui.SplashActivity
import java.lang.ref.WeakReference
import java.util.*

class BaseApp : Application(), Application.ActivityLifecycleCallbacks {

    companion object {

        var startedList = mutableMapOf<String, WeakReference<Activity>>()
        var hasInitChat: Boolean = false
        lateinit var context: Application
        lateinit var config: IMConfig

        fun initChat(uid: Int) {
            hasInitChat = true
            config = IMConfig(uid)
            IMHelper.init(context, config)
        }

        fun getLoginUisFromSp(): Int {
            val sp = context.getSharedPreferences(context.packageName, MODE_PRIVATE)
            return sp.getInt("user_id", 0)
        }

        fun commitNewUid(uid: Int): Boolean {
            initChat(uid)
            val sp = context.getSharedPreferences(context.packageName, MODE_PRIVATE)
            return sp.edit().putInt("user_id", uid).commit()
        }

        fun getHeader(): Map<String, String> {
            val token = config.getToken()
            val userId = config.getUserId()
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

        fun clear(): Boolean {
            val sp = context.getSharedPreferences(context.packageName, MODE_PRIVATE)
            return sp.edit().clear().commit()
        }

        fun backToSplash() {
            startedList.values.forEach {
                val act = it.get()
                if (act !is SplashActivity) {
                    act?.finish()
                }
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        context = this
        val login = getLoginUisFromSp()
        if (login > 0) initChat(login)
    }

    override fun onActivityCreated(a: Activity, p1: Bundle?) {

    }

    override fun onActivityStarted(p0: Activity) {

    }

    override fun onActivityResumed(a: Activity) {
        startedList[a.javaClass.name] = WeakReference(a)
    }

    override fun onActivityPaused(a: Activity) {
        startedList.remove(a.javaClass.name)
    }

    override fun onActivityStopped(p0: Activity) {

    }

    override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {

    }

    override fun onActivityDestroyed(p0: Activity) {

    }
}