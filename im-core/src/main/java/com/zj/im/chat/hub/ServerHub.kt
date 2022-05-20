package com.zj.im.chat.hub

import android.app.Application
import com.zj.im.chat.enums.ConnectionState
import com.zj.im.chat.exceptions.IMException
import com.zj.im.chat.modle.BaseMsgInfo
import com.zj.im.chat.interfaces.SendingCallBack
import com.zj.im.main.StatusHub
import com.zj.im.main.dispatcher.DataReceivedDispatcher
import com.zj.im.main.looper.MsgExecutor
import com.zj.im.main.looper.MsgHandlerQueue
import com.zj.im.utils.log.logger.NetRecordUtils
import com.zj.im.utils.log.logger.printErrorInFile
import com.zj.im.utils.log.logger.printInFile
import com.zj.im.utils.netUtils.IConnectivityManager
import com.zj.im.utils.netUtils.NetWorkInfo
import com.zj.im.utils.nio
import java.util.*


@Suppress("unused", "SameParameterValue", "MemberVisibilityCanBePrivate")
abstract class ServerHub<T> constructor(private var isAlwaysHeartBeats: Boolean = false) {

    companion object {
        private const val PING_TIMEOUT = "reconnection because the ping was no response too many times!"
        private const val RECONNECTION_TIME = 3000L
        private const val HEART_BEATS_BASE_TIME = 3000L
        private const val CONNECTION_EVENT = 0xf1378
        private const val HEART_BEATS_EVENT = 0xf1365
        var currentConnectId: String = ""; private set
    }

    protected var app: Application? = null
    private var connectivityManager: IConnectivityManager? = null
    private var pingHasNotResponseCount = 0
    private var pongTime = 0L
    private var pingTime = 0L
    open var heartbeatsTime = HEART_BEATS_BASE_TIME
    open val reconnectionTime = RECONNECTION_TIME
    private lateinit var handler: MsgExecutor

    val isNetWorkAccess: Boolean
        get() {
            return connectivityManager?.isNetWorkActive == NetWorkInfo.CONNECTED
        }

    protected abstract fun send(params: T, callId: String, callBack: SendingCallBack<T>): Long

    protected abstract fun closeConnection(case: String)

    protected abstract fun connect(connectId: String)

    open fun init(context: Application?) {
        this.app = context
        connectivityManager = IConnectivityManager()
        connectivityManager?.init(context) { netWorkStateChanged(it) }
        curConnectionState = ConnectionState.CONNECTION(false)
    }

    open fun onRouteCall(callId: String?, data: T?) {}

    open fun onReConnect(case: String) {
        printErrorInFile("ServerHub.onReconnect", case, true)
        connectDelay()
    }

    open fun pingServer(response: (Boolean) -> Unit) {
        response(isNetWorkAccess)
    }

    private fun onHandlerExecute(what: Int, obj: Any?) {
        when (what) {
            HEART_BEATS_EVENT -> {
                curConnectionState = ConnectionState.PING
            }
            CONNECTION_EVENT -> {
                curConnectionState = ConnectionState.CONNECTION(true)
                currentConnectId = UUID.randomUUID().toString()
                connect(currentConnectId)
            }
            else -> onMsgThreadCallback(what, obj)
        }
    }

    open fun onMsgThreadCallback(what: Int, obj: Any?) {}

    protected fun sendToMsgThread(what: Int, delay: Long, obj: Any?) {
        handler.enqueue(what, delay, obj)
    }

    protected fun postOnConnected() {
        curConnectionState = ConnectionState.CONNECTED(false)
    }

    protected fun postToClose(case: String) {
        heartbeatsTime = HEART_BEATS_BASE_TIME
        curConnectionState = ConnectionState.ERROR(case)
    }

    protected fun postError(case: String) {
        postError(IMException(case))
    }

    protected fun postError(throws: Throwable?) {
        postToClose(throws?.message ?: "UN_KNOW_ERROR")
        throws?.let { DataReceivedDispatcher.postError(it) }
    }

    protected fun isConnected(): Boolean {
        return curConnectionState.isConnected()
    }

    internal fun checkNetWork(alwaysCheck: Boolean) {
        this.isAlwaysHeartBeats = this.isAlwaysHeartBeats || alwaysCheck
        curConnectionState = ConnectionState.PING
    }

    internal fun tryToReConnect(case: String) {
        NetRecordUtils.recordDisconnectCount()
        onReConnect(case)
    }

    internal fun sendToServer(params: T, callId: String, callBack: SendingCallBack<T>) {
        val size = send(params, callId, callBack)
        if (size > 0) NetRecordUtils.recordLastModifySendData(size)
    }

    internal fun isDataEnable(): Boolean {
        return StatusHub.curConnectionState.isConnected() && isNetWorkAccess
    }

    internal fun onLooperPrepared(queue: MsgHandlerQueue) {
        handler = MsgExecutor(queue, ::onHandlerExecute)
        handler.enqueue(CONNECTION_EVENT, 0)
    }

    private fun netWorkStateChanged(state: NetWorkInfo) {
        DataReceivedDispatcher.pushData(BaseMsgInfo.networkStateChanged<T>(state))
    }

    private fun pingServer() {
        pingServer {
            if (it) {
                val inc = (heartbeatsTime.coerceAtLeast(HEART_BEATS_BASE_TIME)) * 1.2f.coerceAtMost(HEART_BEATS_BASE_TIME * 6f)
                heartbeatsTime = inc.toLong()
                curConnectionState = ConnectionState.PONG
                if (isAlwaysHeartBeats) nextHeartbeats()
            } else {
                val inc = heartbeatsTime * 0.2f.coerceAtLeast(100f)
                heartbeatsTime = inc.toLong()
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
        handler.clearAndDrop()
        curConnectionState = ConnectionState.INIT
        connectivityManager?.shutDown()
    }

    private fun nextHeartbeats() {
        handler.enqueue(HEART_BEATS_EVENT, heartbeatsTime)
    }

    private fun connectDelay(connTime: Long = reconnectionTime) {
        handler.enqueue(CONNECTION_EVENT, connTime)
    }

    private var curConnectionState: ConnectionState = ConnectionState.INIT
        set(value) {
            when (value) {
                is ConnectionState.PONG -> {
                    pingHasNotResponseCount = 0
                    pongTime = System.currentTimeMillis()
                }
                is ConnectionState.PING -> {
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
                is ConnectionState.CONNECTED -> {
                    clearPingRecord()
                    if (isAlwaysHeartBeats) nextHeartbeats()
                }
                is ConnectionState.OFFLINE, is ConnectionState.ERROR, is ConnectionState.RECONNECT -> {
                    clearPingRecord()
                }
                else -> {
                }
            }
            if (value != field) {
                field = value
                if (value.isValidState()) DataReceivedDispatcher.pushData<T>(BaseMsgInfo.connectStateChange(value))
                when (value) {
                    is ConnectionState.PING -> printInFile("on connection status change with id: $currentConnectId", "--- ${value::class.java.simpleName} -- ${nio(pingTime)}")
                    is ConnectionState.PONG -> printInFile("on connection status change with id: $currentConnectId", "--- ${value::class.java.simpleName} -- ${nio(pongTime)}")
                    is ConnectionState.ERROR -> printInFile("on connection status change with id: $currentConnectId", "${value::class.java.simpleName}  ==> reconnection with error : ${value.reason}")
                    else -> printInFile("on connection status change with id: $currentConnectId", "--- $value --")
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