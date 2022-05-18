package com.zj.im.main.impl

import android.app.Service
import android.os.Looper
import com.zj.im.main.looper.MsgHandlerQueue

/**
 * Created by ZJJ
 */

internal abstract class RunningObserver : Service() {

    protected abstract fun run(runningKey: String): Boolean

    private var lock: Boolean = false
    private var isRunning = false

    fun runningInBlock(runningKey: String): Boolean {
        if (lock || isRunning) return false
        val isEmptyQueue: Boolean
        try {
            lock = true
            isRunning = true
            isEmptyQueue = run(runningKey)
        } finally {
            isRunning = false
            lock = false
        }
        return isEmptyQueue
    }

    abstract fun onLooperPrepared(queue: MsgHandlerQueue)

    abstract fun looperInterrupted()
}
