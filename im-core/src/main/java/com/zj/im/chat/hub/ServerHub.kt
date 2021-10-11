package com.zj.im.chat.hub

import android.app.Application
import android.os.Handler
import android.os.Looper
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
import com.zj.im.utils.nio

@Suppress("unused", "SameParameterValue")
abstract class ServerHub<T> constructor(private var isAlwaysHeartBeats: Boolean = false) {

    companion object {
        private const val PING_TIMEOUT = "reconnection because the ping was no response too many times!"
        private const val RECONNECTION_TIME = 2000L
        private const val HEART_BEATS_BASE_TIME = 5000L
        private const val CONNECTION_EVENT = 0xf1378
        private const val HEART_BEATS_EVENT = 0xf1365
    }

    protected var app: Application? = null
    private var connectivityManager: IConnectivityManager? = null
    private var pingHasNotResponseCount = 0
    private var pongTime = 0L
    private var pingTime = 0L
    open var heartbeatsTime = HEART_BEATS_BASE_TIME
    open val reconnectionTime = RECONNECTION_TIME
    private val handler = Handler(Looper.myLooper() ?: Looper.getMainLooper()) {
        when (it.what) {
            HEART_BEATS_EVENT -> {
                curConnectionState = ConnectionState.PING
            }
            CONNECTION_EVENT -> {
                curConnectionState = ConnectionState.CONNECTION
                connect()
            }
        }
        return@Handler false
    }

    val isNetWorkAccess: Boolean
        get() {
            return connectivityManager?.isNetWorkActive == NetWorkInfo.CONNECTED
        }

    open fun init(context: Application?) {
        this.app = context
        connectivityManager = IConnectivityManager()
        connectivityManager?.init(context) { netWorkStateChanged(it) }
        connectDelay(0)
    }

    protected abstract fun send(params: T, callId: String, callBack: SendingCallBack<T>): Long

    protected abstract fun closeConnection(case: String)

    protected abstract fun connect()

    open fun onRouteCall(callId: String?, data: T?) {}

    open fun reConnect(case: String) {
        connectDelay()
    }

    open fun onCheckNetWorkEnable(onChecked: (Boolean) -> Unit) {
        onChecked(isNetWorkAccess)
    }

    protected fun postOnConnected() {
        curConnectionState = ConnectionState.CONNECTED
    }

    protected fun postToClose(case: String) {
        heartbeatsTime = HEART_BEATS_BASE_TIME
        curConnectionState = ConnectionState.CONNECTED_ERROR.case(case)
    }

    protected fun postError(case: String, deadly: Boolean) {
        postError(ChatException(case), deadly)
    }

    protected fun postError(throws: Throwable?, deadly: Boolean) {
        postToClose(throws?.message ?: "UN_KNOW_ERROR")
        throws?.let { DataReceivedDispatcher.postError(it, deadly) }
    }

    protected fun isConnected(): Boolean {
        return curConnectionState.isConnected()
    }

    internal fun checkNetWork(alwaysCheck: Boolean) {
        this.isAlwaysHeartBeats = alwaysCheck
        curConnectionState = ConnectionState.PING
    }

    internal fun tryToReConnect(case: String) {
        NetRecordUtils.recordDisconnectCount()
        reConnect(case)
    }

    internal fun sendToServer(params: T, callId: String, callBack: SendingCallBack<T>) {
        val size = send(params, callId, callBack)
        if (size > 0) NetRecordUtils.recordLastModifySendData(size)
    }

    internal fun isDataEnable(): Boolean {
        return StatusHub.curConnectionState.isConnected() && isNetWorkAccess
    }

    private fun netWorkStateChanged(state: NetWorkInfo) {
        DataReceivedDispatcher.pushData(BaseMsgInfo.networkStateChanged<T>(state))
    }

    private fun pingServer() {
        onCheckNetWorkEnable {
            if (it) {
                heartbeatsTime = HEART_BEATS_BASE_TIME
                curConnectionState = ConnectionState.PONG
                if (isAlwaysHeartBeats) nextHeartbeats()
            } else {
                heartbeatsTime = (heartbeatsTime * if (it) 1f else 0.5f).toLong()
                nextHeartbeats()
            }
        }
    }

    /**
     * @param isSpecialData This message is prioritized when calculating priority and is not affected by pauses
     * */
    @Suppress("SameParameterValue")
    protected fun postReceivedMessage(callId: String, data: T, isSpecialData: Boolean, size: Long) {
        if (size > 0) NetRecordUtils.recordLastModifyReceiveData(size)
        DataReceivedDispatcher.pushData(BaseMsgInfo.receiveMsg(callId, data, isSpecialData))
    }

    protected fun recordOtherSendNetworkDataSize(size: Long) {
        if (size > 0) NetRecordUtils.recordLastModifySendData(size)
    }

    protected fun recordOtherReceivedNetworkDataSize(size: Long) {
        if (size > 0) NetRecordUtils.recordLastModifyReceiveData(size)
    }

    open fun shutdown() {
        closeConnection("shutdown")
        curConnectionState = ConnectionState.INIT
        connectivityManager?.shutDown()
    }

    private fun nextHeartbeats() {
        handler.removeMessages(HEART_BEATS_EVENT)
        handler.sendEmptyMessageDelayed(HEART_BEATS_EVENT, heartbeatsTime)
    }

    private fun connectDelay(connTime: Long = reconnectionTime) {
        handler.removeMessages(CONNECTION_EVENT)
        handler.sendEmptyMessageDelayed(CONNECTION_EVENT, connTime)
    }

    private var curConnectionState: ConnectionState = ConnectionState.INIT
        set(value) {
            when (value) {
                ConnectionState.PONG -> {
                    pingHasNotResponseCount = 0
                    pongTime = System.currentTimeMillis()
                }
                ConnectionState.PING -> {
                    val curTime = System.currentTimeMillis()
                    val outOfTime = HEART_BEATS_BASE_TIME * 3f
                    val lastPingTime = curTime - pingTime - heartbeatsTime
                    if (pingTime > 0 && pongTime <= 0) {
                        pingHasNotResponseCount++
                    }
                    if (pingHasNotResponseCount > 3 || (pongTime > 0L && curTime - (pongTime + lastPingTime) > outOfTime)) {
                        postToClose(PING_TIMEOUT)
                    } else {
                        pingServer()
                    }
                    pingTime = System.currentTimeMillis()
                }
                ConnectionState.CONNECTED -> {
                    clearPingRecord()
                    if (isAlwaysHeartBeats) nextHeartbeats()
                }
                ConnectionState.NETWORK_STATE_CHANGE, ConnectionState.CONNECTED_ERROR, ConnectionState.RECONNECT -> {
                    clearPingRecord()
                }
                else -> {
                }
            }
            if (value != field) {
                val case = value.pollCase()
                field = value
                if (value.isValidState()) DataReceivedDispatcher.pushData<T>(BaseMsgInfo.connectStateChange(value, case))
                when (value) {
                    ConnectionState.PING -> printInFile("on connection status change ----- ", "--- $value -- ${nio(pingTime)}")
                    ConnectionState.PONG -> printInFile("on connection status change ----- ", "--- $value -- ${nio(pongTime)}")
                    ConnectionState.CONNECTED_ERROR -> printInFile("on connection status change ----- ", "$value  ==> reconnection with error : $case")
                    else -> printInFile("on connection status change ----- ", "--- $value --")
                }
            }
        }
        get() {
            synchronized(field) {
                return field
            }
        }

    private fun clearPingRecord() {
        pongTime = 0L
        pingTime = 0L
        pingHasNotResponseCount = 0
        handler.removeMessages(HEART_BEATS_EVENT)
    }
}