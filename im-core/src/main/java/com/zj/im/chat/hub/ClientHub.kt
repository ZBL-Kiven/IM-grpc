package com.zj.im.chat.hub

import android.app.Application
import com.zj.im.chat.enums.LifeType
import com.zj.im.chat.enums.SendMsgState
import com.zj.im.chat.interfaces.MessageInterface
import com.zj.im.chat.modle.IMLifecycle
import com.zj.im.main.StatusHub
import com.zj.im.main.dispatcher.DataReceivedDispatcher
import com.zj.im.utils.log.logger.printInFile

/**
 * Created by ZJJ
 *
 * the bridge of client, override and custom your client hub.
 *
 * it may reconnection if change the system clock to earlier.
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
abstract class ClientHub<T> {

    var context: Application? = null; internal set

    abstract fun progressUpdate(progress: Int, callId: String)

    protected open fun onMsgPatch(data: T?, callId: String?, ignoreSendState: Boolean, sendingState: SendMsgState?, isResent: Boolean, onFinish: () -> Unit) {
        postToUi(data, callId, ignoreSendState, onFinish)
    }

    protected fun postToUi(data: Any?, pl: String?, ignoreSendState: Boolean, onFinish: () -> Unit) {
        try {
            if (ignoreSendState) {
                onFinish()
            } else {
                MessageInterface.postToUIObservers(null, data, pl, onFinish)
            }
        } catch (e: Exception) {
            printInFile("client hub error ", " the ui poster throw an error case: ${e.message}")
        }
    }

    open fun onRouteCall(callId: String?, data: T?) {}

    open fun canReceived(): Boolean {
        return StatusHub.isRunning() && !StatusHub.isReceiving && DataReceivedDispatcher.isDataEnable()
    }

    open fun canSend(): Boolean {
        return StatusHub.isAlive() && DataReceivedDispatcher.isDataEnable()
    }

    internal fun onSendingStateChanged(sendingState: SendMsgState, callId: String, data: T?, ignoreSendState: Boolean, resend: Boolean) {
        onMsgPatch(data, callId, ignoreSendState, sendingState, resend) {
            StatusHub.isReceiving = false
        }
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
