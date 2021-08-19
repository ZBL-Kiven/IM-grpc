package com.zj.im.sender

import com.zj.im.chat.enums.SendMsgState
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

internal class SendExecutors<T>(info: BaseMsgInfo<T>, server: ServerHub<T>?, done: (isOK: Boolean, inRecent: Boolean, info: BaseMsgInfo<T>, e: Throwable?) -> Unit) {

    init {
        var exc: Throwable? = null

        fun clearTimeout() {
            TimeOutUtils.remove(info.callId)
        }

        try {
            when (SendingUp.CANCEL) {
                info.sendingUp -> {
                    clearTimeout()
                    done(false, true, info, exc)
                }
                else -> {
                    val data = info.data ?: throw NullPointerException("what's the point you are sending an empty message?")
                    TimeOutUtils.putASentMessage(info.callId, info.data, info.timeOut, info.isResend, info.ignoreConnecting)
                    server?.sendToServer(data, info.callId, object : SendingCallBack<T> {

                        override fun result(isOK: Boolean, d: T?, throwable: Throwable?) {
                            var inRecent = false
                            try {
                                exc = throwable
                                if (!isOK) {
                                    if (!DataReceivedDispatcher.isDataEnable()) {
                                        DataReceivedDispatcher.pushData(info)
                                        inRecent = true
                                        return
                                    }
                                } else {
                                    if (d != null) {
                                        DataReceivedDispatcher.pushData(BaseMsgInfo.sendingStateChange(SendMsgState.SUCCESS, info.callId, d, info.isResend))
                                    }
                                }
                            } catch (e: Exception) {
                                exc = e
                            } finally {
                                clearTimeout()
                                done(isOK, inRecent, info, exc)
                            }
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
