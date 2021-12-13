package com.zj.im.sender


interface OnStatus<T> {

    fun call(isFinish: Boolean, callId: String, progress: Int, data: T, isOK: Boolean, e: Throwable?, payloadInfo: Any? = null)

}