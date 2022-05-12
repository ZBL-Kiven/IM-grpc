package com.zj.im.sender


interface OnStatus<T> {

    /**
     * After completing the execution of the additional task,
     * this callback needs to be called manually to complete the current task.
     *
     * @param executeId The identity ID of the current task.
     * @param callId The call ID of the current message.
     * @param data The message data will be based on the result returned here.
     * */
    fun call(executeId: String, callId: String, data: T)

    /**
     * @param payloadInfo is the custom information attached to the error type,
     * which can be obtained through [com.zj.im.chat.enums.SendMsgState.getSpecialBody].
     * */
    fun error(executeId: String, callId: String, e: Throwable?, payloadInfo: Any? = null)

    /**
     * @param progress Task execution progress.
     * */
    fun onProgress(executeId: String, callId: String, progress: Int)

}