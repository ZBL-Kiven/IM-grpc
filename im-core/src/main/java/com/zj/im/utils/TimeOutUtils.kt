package com.zj.im.utils

import android.os.Handler
import android.os.Looper
import android.os.Message
import com.zj.im.chat.enums.SendMsgState
import com.zj.im.main.StatusHub.isRunning
import com.zj.im.chat.modle.BaseMsgInfo
import com.zj.im.main.dispatcher.DataReceivedDispatcher

/**
 * created by ZJJ
 *
 * the Timeout utils ,Nonnull param callId
 * */

internal object TimeOutUtils {

    private const val REMOVE = 0
    private const val ADD = 1
    private const val CLEAR = 2
    private const val UPDATE = 3
    private var lastNotifyNode: Long = 0
    private const val updateInterval = 1000L

    private val sentMessages = hashSetOf<SentMsgInfo<*>>()

    fun remove(callId: String?) {
        handler?.sendMessage(Message.obtain().apply {
            this.what = REMOVE
            this.obj = callId
        })
    }

    fun <T> putASentMessage(callId: String, data: T, timeOut: Long, isResend: Boolean, isIgnoreConnecting: Boolean = false) {
        val sentInfo = SentMsgInfo(data, callId, timeOut, isResend, isIgnoreConnecting)
        handler?.sendMessage(Message.obtain().apply {
            this.what = ADD
            this.obj = sentInfo
        })
    }

    fun updateNode() {
        val cur = System.currentTimeMillis()
        if (cur - lastNotifyNode > updateInterval) {
            handler?.sendEmptyMessage(UPDATE)
            lastNotifyNode = cur
        }
    }

    fun clear() {
        handler?.sendEmptyMessage(CLEAR)
    }

    private var handler: Handler? = null
        get() {
            if (field == null) field = Looper.myLooper()?.let { looper ->
                Handler(looper) { msg ->
                    when (msg.what) {
                        REMOVE -> {
                            (msg.obj as? String)?.let { callId ->
                                sentMessages.runSync {
                                    it.removeAll { d ->
                                        d.callId == callId
                                    }
                                }
                            }
                        }

                        ADD -> {
                            (msg.obj as? SentMsgInfo<*>)?.let { data ->
                                sentMessages.runSync {
                                    it.add(data)
                                }
                            }
                        }
                        UPDATE -> {
                            sentMessages.runSync {
                                val rev = arrayListOf<SentMsgInfo<*>>()
                                it.forEach { v ->
                                    if (v.isIgnoreConnecting || (isRunning() && DataReceivedDispatcher.isDataEnable())) {
                                        if (System.currentTimeMillis() - v.putTime >= v.timeOut) {
                                            rev.add(v)
                                        }
                                    } else v.putTime = System.currentTimeMillis()
                                }
                                if (rev.isNotEmpty()) rev.forEach { t ->
                                    it.remove(t)
                                    DataReceivedDispatcher.pushData(BaseMsgInfo.sendingStateChange(SendMsgState.TIME_OUT, t.callId, t.data, t.isResend))
                                }
                                rev.clear()
                            }
                        }
                    }
                    return@Handler false
                }
            }
            return field
        }

    private class SentMsgInfo<T>(val data: T, val callId: String, val timeOut: Long, val isResend: Boolean, val isIgnoreConnecting: Boolean, var putTime: Long = System.currentTimeMillis()) {

        override fun equals(other: Any?): Boolean {
            if (other !is SentMsgInfo<*>) return false
            return other.callId == callId
        }

        override fun hashCode(): Int {
            return callId.hashCode()
        }
    }
}