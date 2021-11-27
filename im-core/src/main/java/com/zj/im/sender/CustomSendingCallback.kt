package com.zj.im.sender


abstract class CustomSendingCallback<T> {

    fun setPending(): CustomSendingCallback<T> {
        pending = true
        return this
    }

    internal var pending: Boolean = false

    open fun onStart(callId: String, ignoreSendState: Boolean, d: T?) {}

    open fun onSendingUploading(progress: Int, ignoreSendState: Boolean, callId: String) {}

    abstract fun onResult(isOK: Boolean, retryAble: Boolean, callId: String, d: T?, throwable: Throwable?, payloadInfo: Any?)
}