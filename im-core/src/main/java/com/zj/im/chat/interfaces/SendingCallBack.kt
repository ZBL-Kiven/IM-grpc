package com.zj.im.chat.interfaces

/**
 * Created by ZJJ
 */

interface SendingCallBack<T> {

    fun result(isOK: Boolean, d: T?, retryAble: Boolean, throwable: Throwable?, payloadInfo: Any?)
}
