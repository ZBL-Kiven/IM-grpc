package com.zj.ccIm.core.impl

import android.app.Application
import com.zj.im.chat.hub.ServerHub
import com.zj.ccIm.core.Constance
import com.zj.ccIm.core.IMHelper
import com.zj.protocol.Grpc
import com.zj.protocol.GrpcConfig
import com.zj.protocol.grpc.*
import io.grpc.StatusRuntimeException
import io.grpc.stub.StreamObserver

internal abstract class ServerImplGrpc : ServerHub<Any?>() {

    private var channel: Grpc.CachedChannel? = null

    abstract fun onConnection()

    override fun init(context: Application?) {
        super.init(context)
        connect()
    }

    override fun closeConnection(case: String) {
        try {
            channel?.shutdownNow()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun connect() {
        try {
            if (channel?.isTerminated != false) {
                channel?.shutdownNow()
                val url = IMHelper.imConfig?.getGrpcAddress() ?: Pair("-", 0)
                val keepAliveTimeOut = IMHelper.imConfig?.getHeatBeatsTimeOut() ?: 5000L
                val idleTimeOut = IMHelper.imConfig?.getIdleTimeOut() ?: 68400000L
                val config = GrpcConfig(url.first, url.second, keepAliveTimeOut, idleTimeOut)
                val header = mapOf("token" to IMHelper.imConfig?.getToken(), "userid" to "${IMHelper.imConfig?.getUserId()}")
                channel = Grpc.get(config).defaultHeader(header)
            }
            onConnection()
        } catch (e: Exception) {
            onParseError(e)
        }
    }

    /**
     * Detect the current connection status with the server by sending ping-pong,
     * which is mainly used to determine the disconnection under special circumstances and reduce the number of disconnections
     * eg: when the front and back are switched, the focus of the APP changes,
     * the network environment continues to be unstable, and the message sending fails, etc.
     * */
    override fun onCheckNetWorkEnable(onChecked: (Boolean) -> Unit) {
        withChannel(true) {
            it.ping(null, object : CusObserver<Pong>() {
                override fun onResult(isOk: Boolean, data: Pong?, t: Throwable?) {
                    onChecked(isOk)
                }
            })
        }
    }

    protected fun <R> withChannel(require: Boolean = true, r: (MsgApiGrpc.MsgApiStub) -> R?): R? {
        channel?.let {
            if (it.isTerminated && require) {
                postToClose(Constance.CONNECTION_RESET);return null
            }
            return r(it.stub { c -> MsgApiGrpc.newStub(c) }.build())
        } ?: if (require) postToClose(Constance.CONNECTION_UNAVAILABLE)
        return null
    }

    /**
     * Resolve all exceptions, including [StatusRuntimeException] and other [Exception].
     * And trigger reconnection in the appropriate scene, or choose whether to clear the buffering queue
     * */
    protected open fun onParseError(t: Throwable?) {
        postError(t)
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