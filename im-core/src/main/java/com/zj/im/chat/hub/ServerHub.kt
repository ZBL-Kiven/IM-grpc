package com.zj.im.chat.hub

import android.app.Application
import com.zj.im.chat.enums.ConnectionState
import com.zj.im.chat.exceptions.ChatException
import com.zj.im.chat.modle.BaseMsgInfo
import com.zj.im.chat.interfaces.SendingCallBack
import com.zj.im.main.StatusHub
import com.zj.im.main.dispatcher.DataReceivedDispatcher
import com.zj.im.utils.log.NetRecordUtils
import com.zj.im.utils.log.logger.printInFile
import com.zj.im.utils.netUtils.IConnectivityManager
import com.zj.im.utils.netUtils.NetWorkInfo

@Suppress("unused", "SameParameterValue")
abstract class ServerHub<T> {

    protected var app: Application? = null
    private var connectivityManager: IConnectivityManager? = null

    open fun init(context: Application?) {
        this.app = context
        connectivityManager = IConnectivityManager()
        connectivityManager?.init(context) { netWorkStateChanged(it) }
    }

    protected abstract fun send(params: T, callId: String, callBack: SendingCallBack<T>): Long

    protected abstract fun closeConnection(case: String)

    open fun onRouteCall(callId: String?, data: T?) {}

    protected abstract fun reConnect(case: String)

    internal fun tryToReConnect(case: String) {
        NetRecordUtils.recordDisconnectCount()
        reConnect(case)
    }

    internal fun sendToServer(params: T, callId: String, callBack: SendingCallBack<T>) {
        val size = send(params, callId, callBack)
        if (size > 0) NetRecordUtils.recordLastModifySendData(size)
    }

    protected fun postToClose(case: String) {
        DataReceivedDispatcher.pushData<T>(BaseMsgInfo.connectStateChange(ConnectionState.CONNECTED_ERROR, case))
    }

    protected fun postConnectState(state: ConnectionState, case: String = "") {
        DataReceivedDispatcher.pushData<T>(BaseMsgInfo.connectStateChange(state, case))
    }

    protected fun postError(case: String) {
        DataReceivedDispatcher.postError(ChatException(case))
    }

    protected fun postError(throws: Throwable) {
        DataReceivedDispatcher.postError(throws)
    }

    protected fun hasNetworkAccess(): Boolean {
        return connectivityManager?.isNetWorkActive == NetWorkInfo.CONNECTED
    }

    protected fun checkNetwork(): Boolean {
        return connectivityManager?.checkNetWorkValidate() == NetWorkInfo.CONNECTED
    }

    private fun netWorkStateChanged(state: NetWorkInfo) {
        DataReceivedDispatcher.pushData(BaseMsgInfo.networkStateChanged<T>(state))
    }

    /**
     * @param isSpecialData This message is prioritized when calculating priority and is not affected by pauses
     * */
    @Suppress("SameParameterValue")
    protected fun postReceivedMessage(callId: String, data: T, isSpecialData: Boolean, size: Long) {
        if (size > 0) NetRecordUtils.recordLastModifyReceiveData(size)
        DataReceivedDispatcher.pushData(BaseMsgInfo.receiveMsg(callId, data, isSpecialData))
    }

    protected fun print(where: String, case: String) {
        printInFile(where, case)
    }

    open fun checkNetWork() {}

    val isNetWorkAccess: Boolean
        get() {
            return connectivityManager?.isNetWorkActive == NetWorkInfo.CONNECTED
        }

    fun isDataEnable(): Boolean {
        return StatusHub.curConnectionState.isConnected() && isNetWorkAccess
    }

    open fun shutdown() {
        closeConnection("shutdown")
        connectivityManager?.shutDown()
    }
}