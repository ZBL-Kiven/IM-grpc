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
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger


@Suppress("unused", "SameParameterValue", "MemberVisibilityCanBePrivate")
abstract class ServerHub<T> constructor(private var isAlwaysHeartBeats: Boolean = false) {

    companion object {
        private const val PING_TIMEOUT = "reconnection because the ping was no response too many times!"
        private const val RECONNECTION_TIME = 3000L
        private const val HEART_BEATS_BASE_TIME = 3000L
        private const val CONNECT_STATE_CHANGE = 0xf1379
        var currentConnectId: String = ""; private set
    }

    protected var app: Application? = null
    private var connectivityManager: IConnectivityManager? = null
    private var alwaysHeartbeats: AtomicBoolean = AtomicBoolean(isAlwaysHeartBeats)
    private var pingHasNotResponseCount = 0
    private var pongTime = 0L
    private var pingTime = 0L
    open var maxPingCount = 5
    private var curPingCount: AtomicInteger = AtomicInteger(0)
    private var heartbeatsTime = HEART_BEATS_BASE_TIME
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
    }

    open fun onRouteCall(callId: String?, data: T?) {}

    open fun onReConnect(case: String) {
        printErrorInFile("ServerHub.onReconnect", case, true)
        handler.enqueue(ConnectionState.CONNECTION(true), reconnectionTime)
    }

    open fun pingServer(response: (Boolean) -> Unit) {
        response(isNetWorkAccess)
    }

    private fun onHandlerExecute(what: Int, obj: Any?) {
        if (obj is ConnectionState) {
            when (obj) {
                is ConnectionState.INIT -> {
                }
                is ConnectionState.CONNECTION -> {
                    curConnectionState = ConnectionState.CONNECTION(true)
                    currentConnectId = UUID.randomUUID().toString()
                    connect(currentConnectId)
                }
                is ConnectionState.PING -> {
                    onCalledPing()
                }
                is ConnectionState.PONG -> {
                    onCalledPong()
                }
                is ConnectionState.CONNECTED -> {
                    onCalledConnected(obj.fromReconnect)
                }
                is ConnectionState.OFFLINE, is ConnectionState.ERROR, is ConnectionState.RECONNECT -> {
                    onCalledError(obj)
                }
            }
        } else onMsgThreadCallback(what, obj)
    }

    open fun onMsgThreadCallback(what: Int, obj: Any?) {}

    protected fun sendToMsgThread(what: Int, delay: Long, obj: Any?) {
        handler.enqueue(what, delay, obj)
    }

    protected fun removeFromMsgThread(what: Int) {
        handler.removeMessages(what)
    }

    protected fun postOnConnected() {
        handler.enqueue(ConnectionState.CONNECTED(false))
    }

    protected fun postToClose(case: String) {
        handler.enqueue(ConnectionState.ERROR(case))
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
        this.alwaysHeartbeats.set(this.isAlwaysHeartBeats || alwaysCheck)
        nextHeartbeats(true)
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
        handler.enqueue(ConnectionState.CONNECTION(false))
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

    protected fun recordOtherSendNetworkDataSize(size: Long) {
        if (size > 0) NetRecordUtils.recordLastModifySendData(size)
    }

    protected fun recordOtherReceivedNetworkDataSize(size: Long) {
        if (size > 0) NetRecordUtils.recordLastModifyReceiveData(size)
    }

    private fun nextHeartbeats(resetPingCount: Boolean) {
        if (resetPingCount) curPingCount.set(0)
        handler.enqueue(ConnectionState.PING, heartbeatsTime)
    }

    private var curConnectionState: ConnectionState = ConnectionState.INIT
        set(value) {
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

    private fun onCalledPing() {
        if (!alwaysHeartbeats.get()) curPingCount.addAndGet(1)
        if (pingTime > 0 && pongTime <= 0) {
            pingHasNotResponseCount++
        }
        if (pingHasNotResponseCount > 3) {
            postToClose(PING_TIMEOUT)
        } else {
            pingServer {
                if (!it) postToClose("PING sent with server received explicit error callback, see [pingServer(Result)]!")
                else {
                    handler.enqueue(ConnectionState.PONG)
                }
            }
            val inc = if (pongTime < 0) {
                heartbeatsTime * 0.2f.coerceAtLeast(100f)
            } else {
                (heartbeatsTime.coerceAtLeast(HEART_BEATS_BASE_TIME)) * 1.2f.coerceAtMost(HEART_BEATS_BASE_TIME * 6f)
            }
            heartbeatsTime = inc.toLong()
            if (alwaysHeartbeats.get() || curPingCount.get() < maxPingCount) nextHeartbeats(false)
        }
        pingTime = System.currentTimeMillis()
        pongTime = -1
        curConnectionState = ConnectionState.PING
    }

    private fun onCalledPong() {
        pingHasNotResponseCount = 0
        pongTime = System.currentTimeMillis()
        curConnectionState = ConnectionState.PONG
    }

    private fun onCalledConnected(fromReconnected: Boolean) {
        curConnectionState = ConnectionState.CONNECTED(fromReconnected)
        clearPingRecord()
        if (alwaysHeartbeats.get() || curPingCount.get() < maxPingCount) nextHeartbeats(false)
    }

    private fun onCalledError(state: ConnectionState) {
        clearPingRecord()
        curConnectionState = state
    }

    private fun clearPingRecord() {
        pongTime = 0L
        pingTime = 0L
        curPingCount.set(0)
        pingHasNotResponseCount = 0
        heartbeatsTime = HEART_BEATS_BASE_TIME
        handler.remove(ConnectionState.PING)
        handler.remove(ConnectionState.PONG)
    }

    open fun shutdown() {
        closeConnection("shutdown")
        handler.clearAndDrop()
        curConnectionState = ConnectionState.INIT
        connectivityManager?.shutDown()
    }
}