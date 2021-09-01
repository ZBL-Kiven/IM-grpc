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

internal class SendExecutors<T>(info: BaseMsgInfo<T>, server: ServerHub<T>?, done: (isOK: Boolean, tryRecent: Boolean, info: BaseMsgInfo<T>, e: Throwable?) -> Unit) {

    init {
        var exc: Throwable? = null

        fun clearTimeout() {
            TimeOutUtils.remove(info.callId)
        }

        try {
            when (info.sendingUp) {
                SendingUp.CANCEL -> {
                    clearTimeout()
                    done(false, false, info, exc)
                }
                else -> {
                    val data = info.data ?: throw NullPointerException("what's the point you are sending an empty message?")
                    TimeOutUtils.putASentMessage(info.callId, info.data, info.timeOut, info.isResend, info.ignoreConnecting)
                    server?.sendToServer(data, info.callId, object : SendingCallBack<T> {
                        override fun result(isOK: Boolean, d: T?, throwable: Throwable?) {
                            exc = throwable
                            clearTimeout()
                            info.data = d
                            done(isOK && d != null, !DataReceivedDispatcher.isDataEnable(), info, exc)
                        }
                    }) ?: throw NullPointerException("server can not be null !!")
                }
            }
        } catch (e: Exception) {
            exc = e;clearTimeout()
            done(false, false, info, exc)
        }
    }
}
