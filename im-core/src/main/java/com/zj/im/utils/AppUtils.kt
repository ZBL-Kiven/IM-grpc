@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package com.zj.im.utils

import android.os.Bundle
import android.app.Activity
import android.app.Application
import android.content.ComponentCallbacks2
import android.content.Context
import android.content.res.Configuration
import java.lang.IllegalStateException


object AppUtils : Application.ActivityLifecycleCallbacks, ComponentCallbacks2 {

    private var application: Application? = null
    private val appEvents: MutableList<OnAppEvent> = mutableListOf()
    private var isInitLifecycleCallback = false
    private var runningTasksNum: Int = 0
    private var isAppInBackgroundCurrent = false

    fun init(context: Context) {
        if (isInitLifecycleCallback) return
        application = context as? Application
        if (application == null) application = context.applicationContext as? Application
        if (application == null) throw IllegalStateException("app status exception ! and the application context cannot be found through Context")
        isInitLifecycleCallback = application?.let {
            it.registerActivityLifecycleCallbacks(this)
            it.registerComponentCallbacks(this)
            true
        } ?: false
    }

    fun addAppEventStateListener(onAppStateChangedListener: OnAppEvent) {
        if (!appEvents.contains(onAppStateChangedListener)) appEvents.add(onAppStateChangedListener)
    }

    fun removeAppEventStateListener(onAppStateChangedListener: OnAppEvent) {
        appEvents.remove(onAppStateChangedListener)
    }

    private fun <T : Any> notifyAppState(e: OnAppEvent.() -> T) {
        appEvents.forEach { e.invoke(it) }
    }

    override fun onActivityStarted(activity: Activity) {

    }

    override fun onActivityDestroyed(activity: Activity) {
        notifyAppState { onFinished(activity) }
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

    override fun onActivityStopped(activity: Activity) {}

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
    }

    override fun onActivityResumed(activity: Activity) {
        runningTasksNum++
        if (isAppInBackgroundCurrent) {
            isAppInBackgroundCurrent = false
            notifyAppState {
                appStateChanged(false)
            }
        }
    }

    override fun onActivityPaused(activity: Activity) {
        runningTasksNum = runningTasksNum--.coerceAtLeast(0)
    }

    override fun onLowMemory() {

    }

    override fun onConfigurationChanged(newConfig: Configuration) {

    }

    override fun onTrimMemory(level: Int) {
        notifyAppState { onAppMemoryLevelChanged(level) }
        if (!isAppInBackgroundCurrent && level >= ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN) {
            isAppInBackgroundCurrent = true
            notifyAppState {
                appStateChanged(true)
            }
        }
    }

    fun destroy() {
        appEvents.clear()
        application?.unregisterActivityLifecycleCallbacks(this)
        application?.unregisterComponentCallbacks(this)
        isInitLifecycleCallback = false
        runningTasksNum = 0
    }
}

interface OnAppEvent {

    fun appStateChanged(inBackground: Boolean)

    fun onAppMemoryLevelChanged(level: Int) {}

    fun onFinished(activity: Activity) {}

}
