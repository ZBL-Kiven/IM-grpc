package com.zj.ccIm.core.sender.exchange

import com.zj.ccIm.MainLooper
import com.zj.im.chat.enums.SendMsgState
import com.zj.im.sender.CustomSendingCallback
import com.zj.im.utils.cast

sealed class CustomSendListener<O : Any>(private val targetClass: Class<O>) {

    protected var isPendingSet = false

    internal val callback = object : CustomSendingCallback<Any?>() {
        override fun onStart(callId: String, d: Any?) {
            val data = getTargetInfo(SendMsgState.SENDING, callId, d, null) ?: return
            MainLooper.post { onSendingStart(callId, data.first) }
        }

        override fun onSendingUploading(progress: Int, callId: String) {
            MainLooper.post { onSendingProgress(callId, progress) }
        }

        override fun onResult(isOK: Boolean, retryAble: Boolean, callId: String, d: Any?, throwable: Throwable?, payloadInfo: Any?) {
            val data = getTargetInfo(SendMsgState.SUCCESS, callId, d, payloadInfo) ?: return
            MainLooper.post { onSendResult(isOK, retryAble, callId, data.first, throwable, data.second) }
        }
    }

    private fun getTargetInfo(sendState: SendMsgState, callId: String, d: Any?, payloadInfo: Any?): Pair<O?, String?>? {
        if (d == null) return null
        return if (targetClass != d::class.java) {
            changeDataToTarget(sendState, callId, d, payloadInfo)
        } else {
            Pair<Nothing?, String>(cast(d), payloadInfo.toString())
        }
    }

    fun setPending(): CustomSendListener<O> {
        isPendingSet = true
        callback.setPending()
        return this
    }

    abstract fun changeDataToTarget(sendState: SendMsgState, callId: String, d: Any?, payloadInfo: Any?): Pair<O?, String?>?

    open fun onSendingStart(callId: String, d: O?) {}

    open fun onSendingProgress(callId: String, progress: Int) {}

    abstract fun onSendResult(isOK: Boolean, retryAble: Boolean, callId: String, d: O?, throwable: Throwable?, payloadInfo: Any?)


}