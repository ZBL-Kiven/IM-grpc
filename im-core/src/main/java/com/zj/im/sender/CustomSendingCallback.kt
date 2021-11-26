package com.zj.im.sender


abstract class CustomSendingCallback<T> {

    fun setPending(): CustomSendingCallback<T> {
        pending = true
        return this
    }

    internal var pending: Boolean = false

    open fun onStart(callId: String, d: T?) {}

    open fun onSendingUploading(progress: Int, callId: String) {}

    abstract fun onResult(isOK: Boolean, retryAble: Boolean, callId: String, d: T?, throwable: Throwable?, payloadInfo: Any?)
}