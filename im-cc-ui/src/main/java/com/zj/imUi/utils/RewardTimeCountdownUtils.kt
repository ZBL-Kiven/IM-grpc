package com.zj.imUi.utils

import android.os.Handler
import android.os.Looper
import android.os.Message
import java.lang.ref.WeakReference
import java.util.concurrent.ConcurrentHashMap

@Suppress("unused")
object RewardTimeCountdownUtils {

    var handler = Handler(Looper.getMainLooper()) {
        val msgId = it.obj.toString()
        countdownMaps[msgId]?.calculateExpireTime()
        return@Handler false
    }

    fun onCountDown(value: Long) {
        countdownMaps.forEach {
            it.value.usedTime += value
        }
    }

    fun clearCountdownObserver(msgId: String) {
        countdownMaps.remove(msgId)?.let {
            it.clearListener()
            handler.removeMessages(it.hashCode())
        }
    }

    fun clearAllCountdownObservers() {
        handler.removeCallbacksAndMessages(null)
        countdownMaps.clear()
    }

    data class CountdownInfo(val msgId: String, var expireTime: Long, private var l: WeakReference<CountdownListener?>) {
        var usedTime: Long = 0
            set(value) {
                if (field != value) {
                    field = value
                    if (Thread.currentThread() == Looper.getMainLooper().thread) {
                        calculateExpireTime()
                    } else {
                        handler.sendMessage(Message.obtain().apply {
                            what = this@CountdownInfo.hashCode()
                            obj = msgId
                        })
                    }
                }
            }

        internal fun calculateExpireTime() {
            this.l.get()?.onCountdown(msgId, expireTime - usedTime)
        }

        internal fun updateListener(l: WeakReference<CountdownListener?>) {
            this.l = l
        }

        internal fun clearListener() {
            this.l.clear()
        }
    }

    private val countdownMaps: ConcurrentHashMap<String, CountdownInfo> = ConcurrentHashMap()

    internal fun registerCountdownObserver(msgId: String, expireTime: Long, l: CountdownListener) {
        var exists = countdownMaps[msgId]
        if (exists == null) {
            exists = CountdownInfo(msgId, expireTime, WeakReference(l))
        } else {
            exists.updateListener(WeakReference(l))
        }
        countdownMaps[msgId] = exists
    }

    internal fun unRegisterCountdownObserver(msgId: String) {
        val info = countdownMaps[msgId]
        handler.removeMessages(info.hashCode())
        info?.clearListener()
    }

    interface CountdownListener {

        fun onCountdown(msgId: String, remainingTime: Long)

    }
}