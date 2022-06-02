package com.zj.ccIm.core.impl


import com.zj.ccIm.CcIM
import com.zj.ccIm.error.ConnectionError
import com.zj.ccIm.error.InitializedException
import com.zj.ccIm.error.StreamFinishException
import com.zj.ccIm.logger.ImLogs
import com.zj.im.chat.hub.ServerHub
import com.zj.protocol.Grpc
import com.zj.protocol.GrpcConfig
import com.zj.protocol.grpc.MsgApiGrpc
import com.zj.protocol.grpc.Pong
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.grpc.stub.StreamObserver

internal abstract class ServerImplGrpc : ServerHub<Any?>(true) {

    private var curCachedRunningKey: String = ""

    abstract fun onConnection(connectId: String)

    override fun closeConnection(case: String) {
        try {
            Grpc.shutdown()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun connect(connectId: String) {
        try {
            if (!Grpc.isAlive()) {
                val url = CcIM.imConfig?.getGrpcAddress() ?: Pair("-", 0)
                val keepAliveTimeOut = CcIM.imConfig?.getHeatBeatsTimeOut() ?: 5000L
                val idleTimeOut = CcIM.imConfig?.getIdleTimeOut() ?: 68400000L
                val config = GrpcConfig(url.first, url.second, keepAliveTimeOut, idleTimeOut)
                Grpc.build(config) {
                    if (!it) {
                        postError(InitializedException("grpc create error !"))
                    } else {
                        onConnection(connectId)
                    }
                }
            } else {
                Grpc.reset()
                onConnection(connectId)
            }
        } catch (e: Exception) {
            onParseError(e)
        }
    }

//    override fun pingServer(response: (Boolean) -> Unit) {
//        withChannel {
//            it?.ping(null, object : CusObserver<Pong>(isStreaming = false) {
//                override fun onResult(isOk: Boolean, data: Pong?, t: Throwable?) {
//                    response(isOk)
//                }
//            })
//        }
//    }

    protected fun <R> withChannel(require: Boolean = true, r: (MsgApiGrpc.MsgApiStub?) -> R?): R? {
        CcIM.imConfig?.getApiHeader()?.let { header ->
            Grpc.stub(header)?.let {
                return r(it)
            } ?: if (require) postError(InitializedException("the Grpc stub is null!"))
        } ?: if (require) postError(InitializedException("the configuration header must not be null!"))
        return null
    }

    /**
     * Resolve all exceptions, including [StatusRuntimeException] and other [Exception].
     * And trigger reconnection in the appropriate scene, or choose whether to clear the buffering queue
     * */
    protected open fun onParseError(t: Throwable?) {
        ImLogs.recordLogsInFile("server.onParseError", "${t?.message}")
        when (t) {
            is StatusRuntimeException -> {
                when (t.status.code) {
                    Status.Code.CANCELLED, Status.Code.UNAUTHENTICATED -> {
                        ImLogs.recordLogsInFile("------ ", "onCanceled with message : ${t.message}")
                    }
                    else -> {
                        postError(ConnectionError("server error ${t.status.code.name} ; code = ${t.status.code.value()} "))
                    }
                }
            }
            is StreamFinishException -> {
                postError(ConnectionError(t.message ?: ""))
            }
            else -> postError(t)
        }
    }

    protected open class CusObserver<T>(private val name: String = "", private val runningKey: String = "", private val isStreaming: Boolean) : StreamObserver<T> {

        final override fun onNext(value: T) {
            if (this.runningKey == currentConnectId) onResult(true, value, null)
        }

        final override fun onError(t: Throwable?) {
            if (this.runningKey == currentConnectId) onResult(false, null, t)
        }

        final override fun onCompleted() {
            if (isStreaming && this.runningKey == currentConnectId) onResult(false, null, StreamFinishException("$name stream connection completed error."))
        }

        open fun onResult(isOk: Boolean, data: T?, t: Throwable?) {}
    }
}