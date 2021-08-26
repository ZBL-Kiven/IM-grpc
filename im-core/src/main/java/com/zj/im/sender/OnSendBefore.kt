package com.zj.im.sender

/**
 * Created by ZJJ
 */

interface OnSendBefore<T> {
    fun call(onStatus: OnStatus<T>)
}

interface OnStatus<T> {

    fun call(isFinish: Boolean, callId: String, progress: Int, data: T, isOK: Boolean, e: Throwable?)

    fun onSendingInfoChanged(callId: String, data: T)
}