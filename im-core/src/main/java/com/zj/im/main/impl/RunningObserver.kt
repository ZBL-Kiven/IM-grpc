package com.zj.im.main.impl

import android.app.Service

/**
 * Created by ZJJ
 */

internal abstract class RunningObserver : Service() {

    protected abstract fun run(runningKey: String)

    open fun getTotal(): Int {
        return 0
    }

    private var lock: Boolean = false
    private var isRunning = false

    fun runningInBlock(runningKey: String) {
        if (lock || isRunning) return
        try {
            lock = true
            isRunning = true
            run(runningKey)
        } finally {
            isRunning = false
            lock = false
        }
    }
}
