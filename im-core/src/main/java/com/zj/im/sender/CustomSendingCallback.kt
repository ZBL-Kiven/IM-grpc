package com.zj.im.sender


abstract class CustomSendingCallback<T> {

    /**
     * @see pending
     * */
    fun setPending(): CustomSendingCallback<T> {
        pending = true
        return this
    }

    /**
     * After Pending is set, this message will be synchronized to [com.zj.im.chat.hub.ClientHub.onMsgPatch],
     * I default the following callback is the request that the developer wants to complete the independent processing (out of this system).
     * Of course, whether it is notified to UIObservers is still related to the parameter ignoreSendState when sending
     * */
    internal var pending: Boolean = false

    open fun onStart(callId: String, ignoreSendState: Boolean, d: T?) {}

    open fun onSendingUploading(progress: Int, ignoreSendState: Boolean, callId: String) {}

    abstract fun onResult(isOK: Boolean, retryAble: Boolean, callId: String, d: T?, throwable: Throwable?, payloadInfo: Any?)
}