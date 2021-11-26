package com.zj.im.sender

import com.zj.im.chat.hub.ServerHub
import com.zj.im.chat.modle.BaseMsgInfo
import com.zj.im.chat.interfaces.SendingCallBack
import com.zj.im.chat.modle.SendingUp
import com.zj.im.utils.TimeOutUtils
import com.zj.im.main.dispatcher.DataReceivedDispatcher
import java.lang.NullPointerException

/**
 * Created by ZJJ
 */

internal class SendExecutors<T>(info: BaseMsgInfo<T>, server: ServerHub<T>?, done: SendExecutorsInterface<T>) {

    init {
        var exc: Throwable? = null
        fun clearTimeout() {
            TimeOutUtils.remove(info.callId)
        }

        try {
            when (info.sendingUp) {
                SendingUp.CANCEL -> {
                    clearTimeout()
                    if (info.customSendingCallback != null) {
                        info.customSendingCallback?.onResult(isOK = false, retryAble = false, callId = info.callId, d = info.data, throwable = exc, payloadInfo = null)
                    } else {
                        done.result(isOK = false, retryAble = false, d = info, throwable = exc, payloadInfo = null)
                    }
                }
                else -> {
                    val data = info.data ?: throw NullPointerException("what's the point you are sending an empty message?")
                    TimeOutUtils.putASentMessage(info.callId, info.data, info.timeOut, info.isResend, info.ignoreConnecting)
                    server?.sendToServer(data, info.callId, object : SendingCallBack<T> {
                        override fun result(isOK: Boolean, d: T?, retryAble: Boolean, throwable: Throwable?, payloadInfo: Any?) {
                            exc = throwable
                            clearTimeout()
                            val canRetry = retryAble && !DataReceivedDispatcher.isDataEnable()
                            info.customSendingCallback?.let {
                                info.customSendingCallback?.onResult(isOK, canRetry, callId = info.callId, d, exc, payloadInfo)
                                if (it.pending) {
                                    notifyChange(info, isOK, data, d, canRetry, exc, payloadInfo, done)
                                }
                            } ?: notifyChange(info, isOK, data, d, canRetry, exc, payloadInfo, done)
                        }
                    }) ?: throw NullPointerException("server can not be null !!")
                }
            }
        } catch (e: Exception) {
            exc = e
            clearTimeout()
            done.result(false, retryAble = false, d = info, throwable = exc, payloadInfo = null)
        }
    }

    private fun notifyChange(info: BaseMsgInfo<T>, isOK: Boolean, data: T?, d: T?, canRetry: Boolean, exc: Throwable?, payloadInfo: Any?, done: SendExecutorsInterface<T>) {
        info.data = if ((!isOK && canRetry) || d == null) data else d
        done.result(isOK, canRetry, info, exc, payloadInfo)
    }

    internal interface SendExecutorsInterface<T> {
        fun result(isOK: Boolean, retryAble: Boolean, d: BaseMsgInfo<T>, throwable: Throwable?, payloadInfo: Any?)
    }

}
