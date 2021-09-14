package com.zj.imUi.utils

import android.os.Handler
import android.os.Looper
import android.os.Message
import java.lang.ref.WeakReference
import java.util.concurrent.ConcurrentHashMap

@Suppress("unused")
object MessageSendTimeUtils {

    var handler = Handler(Looper.getMainLooper()) {
        val msgId = it.obj.toString()
        MessageSendTimeMaps[msgId]?.calculateSendTime()
        return@Handler false
    }

    fun onSendTime(value: Long) {
        //每分钟刷新，应传入 60*1000
        MessageSendTimeMaps.forEach {
            it.value.usedTime += value
        }
    }

    fun clearSendTimeObserver(msgId: String) {
        MessageSendTimeMaps.remove(msgId)?.let {
            it.clearListener()
            handler.removeMessages(it.hashCode())
        }
    }

    fun clearAllSendTimeObservers() {
        handler.removeCallbacksAndMessages(null)
        MessageSendTimeMaps.clear()
    }

    data class MessageSendTimeInfo(val msgId: String, var sendTime: Long, private var l: WeakReference<SendTImeListener?>) {
        var usedTime: Long = 0
            set(value) {
                if (field != value) {
                    field = value
                    if (Thread.currentThread() == Looper.getMainLooper().thread) {
                        calculateSendTime()
                    } else {
                        //如果不在主线程，则用handler处理
                        handler.sendMessage(Message.obtain().apply {
                            what = this@MessageSendTimeInfo.hashCode()
                            obj = msgId
                        })
                    }
                }
            }

        internal fun calculateSendTime() {
            this.l.get()?.onSendTime(msgId, sendTime + usedTime)
        }

        internal fun updateListener(l: WeakReference<SendTImeListener?>) {
            this.l = l
        }

        internal fun clearListener() {
            this.l.clear()
        }
    }

    private val MessageSendTimeMaps: ConcurrentHashMap<String, MessageSendTimeInfo> = ConcurrentHashMap()

    //为每个Item注册监听器
    internal fun registerSendTimeObserver(msgId: String, sendTime: Long, l: SendTImeListener) {
        var exists = MessageSendTimeMaps[msgId]
//        exists = CountdownInfo(msgId, sendTime, WeakReference(l))
        if (exists == null) {
            exists = MessageSendTimeInfo(msgId, sendTime, WeakReference(l))
        } else {
            exists.updateListener(WeakReference(l))
        }
        MessageSendTimeMaps[msgId] = exists
    }

    internal fun unRegisterSendTImeObserver(msgId: String) {
        val info = MessageSendTimeMaps[msgId]
        handler.removeMessages(info.hashCode())
        info?.clearListener()
    }

    interface SendTImeListener {
        fun onSendTime(msgId: String, sendTime: Long)
    }
}