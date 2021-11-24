package com.zj.im.chat.hub

import android.app.Application
import com.zj.im.chat.enums.LifeType
import com.zj.im.chat.enums.SendMsgState
import com.zj.im.chat.enums.ConnectionState
import com.zj.im.chat.interfaces.MessageInterface
import com.zj.im.main.StatusHub
import com.zj.im.main.dispatcher.DataReceivedDispatcher
import com.zj.im.chat.modle.BaseMsgInfo
import com.zj.im.chat.modle.IMLifecycle
import com.zj.im.utils.log.logger.printInFile
import java.lang.Exception

/**
 * Created by ZJJ
 *
 * the bridge of client, override and custom your client hub.
 *
 * it may reconnection if change the system clock to earlier.
 *
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
abstract class ClientHub<T> {

    var context: Application? = null; internal set

    protected open fun onMsgPatch(data: T?, callId: String?, isSpecialData: Boolean, sendingState: SendMsgState?, isResent: Boolean, onFinish: () -> Unit) {
        try {
            MessageInterface.postToUIObservers(null, data, callId, onFinish)
        } catch (e: Exception) {
            printInFile("client hub error ", " the ui poster throw an error case: ${e.message}")
        }
    }

    open fun onRouteCall(callId: String?, data: T?) {}

    abstract fun progressUpdate(progress: Int, callId: String)

    internal fun onSendingStateChanged(sendingState: SendMsgState, callId: String, data: T?, isSpecialData: Boolean, resend: Boolean) {
        onMsgPatch(data, callId, isSpecialData, sendingState, resend) {
            StatusHub.isReceiving = false
        }
    }

    open fun canReceived(): Boolean {
        return StatusHub.isRunning() && !StatusHub.isReceiving && DataReceivedDispatcher.isDataEnable()
    }

    open fun canSend(): Boolean {
        return StatusHub.isAlive() && DataReceivedDispatcher.isDataEnable()
    }

    internal fun pause(code: String): Boolean {
        if (StatusHub.isPaused()) return false
        StatusHub.onLifecycle(IMLifecycle(LifeType.PAUSE, code))
        return true
    }

    internal fun resume(code: String): Boolean {
        if (StatusHub.isRunning()) return false
        StatusHub.onLifecycle(IMLifecycle(LifeType.RESUME, code))
        return true
    }

    internal fun shutdown() {
        StatusHub.onLifecycle(IMLifecycle(LifeType.STOP, "-- shutdown --"))
    }
}
