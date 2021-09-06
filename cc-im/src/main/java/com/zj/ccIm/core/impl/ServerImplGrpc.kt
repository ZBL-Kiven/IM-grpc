package com.zj.ccIm.core.impl

import android.app.Application
import android.os.Handler
import android.os.Looper
import com.zj.im.chat.enums.ConnectionState
import com.zj.im.chat.hub.ServerHub
import com.zj.im.utils.nio
import com.zj.ccIm.core.Constance
import com.zj.ccIm.core.IMHelper
import com.zj.protocol.Grpc
import com.zj.protocol.GrpcConfig
import com.zj.protocol.grpc.*
import io.grpc.StatusRuntimeException
import io.grpc.stub.StreamObserver

abstract class ServerImplGrpc : ServerHub<Any?>() {

    private var channel: Grpc.CachedChannel? = null
    private var defaultHeader: Map<String, String>? = null
    private var pingHasNotResponseCount = 0
    private var pongTime = 0L
    private var pingTime = 0L
    private var heartBeatsTime = Constance.HEART_BEATS_BASE_TIME

    private val handler = Handler(Looper.myLooper() ?: Looper.getMainLooper()) {
        when (it.what) {
            Constance.HEART_BEATS_EVENT -> {
                curConnectionState = ConnectionState.PING
            }
            Constance.CONNECTION_EVENT -> {
                conn()
            }
        }
        return@Handler false
    }

    override fun init(context: Application?) {
        super.init(context)
        defaultHeader = mapOf("token" to IMHelper.imConfig.getToken(), "userid" to "${IMHelper.imConfig.getUserId()}")
        conn()
    }

    override fun closeConnection(case: String) {
        channel?.shutdownNow()
        curConnectionState = ConnectionState.CONNECTED_ERROR
    }

    override fun reConnect(case: String) {
        connectDelay()
    }

    private fun conn() {
        curConnectionState = ConnectionState.CONNECTION
        try {
            if (channel?.isTerminated != false) {
                channel?.shutdownNow()
                val url = IMHelper.imConfig.getGrpcAddress()
                val keepAliveTimeOut = IMHelper.imConfig.getHeatBeatsTimeOut()
                val idleTimeOut = IMHelper.imConfig.getIdleTimeOut()
                val config = GrpcConfig(url.first, url.second, keepAliveTimeOut, idleTimeOut)
                channel = Grpc.get(config).defaultHeader(defaultHeader)
            }
            onConnection()
        } catch (e: Exception) {
            postToClose("connection error")
            onParseError(e)
        }
    }

    abstract fun onConnection()

    open fun onConnected(connectType: Int) {
        curConnectionState = ConnectionState.CONNECTED
    }

    protected fun <R> withChannel(require: Boolean = true, r: (MsgApiGrpc.MsgApiStub) -> R?): R? {
        channel?.let {
            if (it.isTerminated && require) {
                reConnect(Constance.CONNECTION_RESET);return null
            }
            return r(it.stub { c -> MsgApiGrpc.newStub(c) }.build())
        } ?: if (require) reConnect(Constance.CONNECTION_UNAVAILABLE)
        return null
    }

    /**
     * Detect the current connection status with the server by sending ping-pong,
     * which is mainly used to determine the disconnection under special circumstances and reduce the number of disconnections
     * eg: when the front and back are switched, the focus of the APP changes,
     * the network environment continues to be unstable, and the message sending fails, etc.
     * */
    override fun checkNetWork() {
        withChannel {
            it.ping(null, object : CusObserver<Pong>() {
                override fun onResult(isOk: Boolean, data: Pong?, t: Throwable?) {
                    heartBeatsTime = (Constance.HEART_BEATS_BASE_TIME * if (isOk) 1f else 0.5f).toLong()
                    if (isOk) curConnectionState = ConnectionState.PONG else nextHeartbeats()
                }
            })
        }
    }

    private fun nextHeartbeats() {
        handler.removeMessages(Constance.HEART_BEATS_EVENT)
        handler.sendEmptyMessageDelayed(Constance.HEART_BEATS_EVENT, Constance.HEART_BEATS_BASE_TIME)
    }

    private fun connectDelay(connTime: Long = Constance.RECONNECTION_TIME) {
        handler.removeMessages(Constance.CONNECTION_EVENT)
        handler.sendEmptyMessageDelayed(Constance.CONNECTION_EVENT, connTime)
    }

    private var curConnectionState: ConnectionState = ConnectionState.INIT
        set(value) {
            when (value) {
                ConnectionState.PONG -> {
                    pingHasNotResponseCount = 0
                    pongTime = System.currentTimeMillis()
                }
                ConnectionState.PING -> {
                    checkNetWork()
                    val curTime = System.currentTimeMillis()
                    val outOfTime = heartBeatsTime * 3f
                    val lastPingTime = curTime - pingTime - heartBeatsTime
                    if (pingTime > 0 && pongTime <= 0) {
                        pingHasNotResponseCount++
                    }
                    if (pingHasNotResponseCount > 3 || (pongTime > 0L && curTime - (pongTime + lastPingTime) > outOfTime)) {
                        reConnect(Constance.PING_TIMEOUT)
                    }
                    pingTime = System.currentTimeMillis()
                }
                ConnectionState.CONNECTED -> {
                    clearPingRecord()
                }
                ConnectionState.NETWORK_STATE_CHANGE, ConnectionState.CONNECTED_ERROR -> {
                    clearPingRecord()
                    connectDelay(Constance.RECONNECTION_TIME_5000)
                }
                else -> {
                }
            }
            if (value != field) {
                field = value
                if (value.isValidState()) postConnectState(curConnectionState)
                when (value) {
                    ConnectionState.PING -> print("on connection status change ----- ", "--- $value -- ${nio(pingTime)}")
                    ConnectionState.PONG -> print("on connection status change ----- ", "--- $value -- ${nio(pongTime)}")
                    ConnectionState.CONNECTED_ERROR -> print("on connection status change ----- ", "$value  ==> reconnection with error : ${value.case}")
                    else -> print("on connection status change ----- ", "--- $value --")
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
    }

    /**
     * Resolve all exceptions, including [StatusRuntimeException] and other [Exception].
     * And trigger reconnection in the appropriate scene, or choose whether to clear the buffering queue
     * */
    protected open fun onParseError(t: Throwable?) {
        curConnectionState = ConnectionState.CONNECTED_ERROR.case("the connection have to reconnection with error: ${t?.message}")
    }

    protected open class CusObserver<T> : StreamObserver<T> {
        final override fun onNext(value: T) {
            onResult(true, value, null)
        }

        final override fun onError(t: Throwable?) {
            onResult(false, null, t)
        }

        final override fun onCompleted() {}
        open fun onResult(isOk: Boolean, data: T?, t: Throwable?) {}
    }
}