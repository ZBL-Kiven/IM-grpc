package com.zj.im.main.impl

import android.app.Application
import android.app.Notification
import android.app.Service
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.lifecycle.LifecycleOwner
import com.zj.im.chat.core.BaseOption
import com.zj.im.chat.enums.ConnectionState
import com.zj.im.chat.exceptions.IMException
import com.zj.im.chat.exceptions.NecessaryAttributeEmptyException
import com.zj.im.chat.exceptions.NoServiceException
import com.zj.im.chat.hub.ClientHub
import com.zj.im.chat.hub.ServerHub
import com.zj.im.chat.interfaces.MessageInterface
import com.zj.im.chat.modle.BaseMsgInfo
import com.zj.im.chat.poster.UIHandlerCreator
import com.zj.im.chat.poster.UIHelperCreator
import com.zj.im.main.ChatBase
import com.zj.im.sender.OnSendBefore
import com.zj.im.utils.cast
import com.zj.im.utils.log.NetWorkRecordInfo
import com.zj.im.utils.log.logger.NetRecordUtils
import com.zj.im.utils.log.logger.d
import com.zj.im.utils.log.logger.printErrorInFile
import com.zj.im.utils.log.logger.printInFile
import java.lang.IllegalArgumentException
import java.util.concurrent.LinkedBlockingDeque


/**
 * created by ZJJ
 *
 * extend this and call init before use ,or it will be crash without init!!
 *
 * the entry of chatModule ,call register/unRegister listeners to observer/cancel the msg received
 *
 * you can call pause/resume to modify the messagePool`s running state.
 *
 * @property getClient return your custom client for sdk {@see ClientHub}
 *
 * @property getServer return your custom server for sdk {@see ServerHub}
 *
 * @property onError handler the sdk errors with runtime
 *
 * @property prepare on SDK init prepare
 *
 * @property shutdown it called when SDK was shutdown
 *
 * @property onAppLayerChanged it called when SDK was changed form foreground / background
 *
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
abstract class IMInterface<T> : MessageInterface<T>() {

    inline fun <reified T : Any, reified R : Any> addTransferObserver(uniqueCode: Any, lifecycleOwner: LifecycleOwner? = null): UIHandlerCreator<T, R> {
        setNewListener(R::class.java, "UT2F-E17T-33I-9112")
        return UIHandlerCreator(uniqueCode, lifecycleOwner, T::class.java, R::class.java)
    }

    inline fun <reified T : Any> addReceiveObserver(uniqueCode: Any, lifecycleOwner: LifecycleOwner? = null): UIHelperCreator<T, T, *> {
        setNewListener(T::class.java, "UT2Q-99BR-E88-2271")
        return UIHelperCreator(uniqueCode, lifecycleOwner, T::class.java, T::class.java, null)
    }

    fun <X> setNewListener(cls: Class<*>, imm: X) {
        if (imm.toString() == "UT2Q-99BR-E88-2271" || imm.toString() == "UT2F-E17T-33I-9112") {
            if (isServiceConnected) {
                onNewListenerRegistered(cls)
            } else {
                cachedListenClasses.add(cls)
            }
        } else {
            throw IllegalArgumentException()
        }
    }

    private var cachedListenClasses = LinkedBlockingDeque<Class<*>>()

    private var cachedServiceOperations = LinkedBlockingDeque<RunnerClientStub<T>.() -> Unit>()

    private var baseConnectionService: ChatBase<T>? = null

    private var serviceConn: ServiceConnection? = null

    private var client: ClientHub<T>? = null

    private var server: ServerHub<T>? = null

    private var isServiceConnected = false

    internal var option: BaseOption? = null

    protected fun initChat(option: BaseOption) {
        this.option = option
        baseConnectionService?.let {
            it.init(this)
            return
        }
        serviceConn = object : ServiceConnection {
            override fun onServiceDisconnected(name: ComponentName?) {
                isServiceConnected = false
                this@IMInterface.onServiceDisConnected()
            }

            override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
                baseConnectionService = cast<IBinder?, ChatBase.ConnectionBinder<T>?>(binder)?.service
                baseConnectionService?.init(this@IMInterface)
                isServiceConnected = true
                this@IMInterface.onServiceConnected()
                if (cachedListenClasses.isNotEmpty()) {
                    cachedListenClasses.forEach {
                        onNewListenerRegistered(it)
                    }
                }
                if (cachedServiceOperations.isNotEmpty()) {
                    cachedServiceOperations.forEach {
                        baseConnectionService?.let { s -> it.invoke(s) }
                    }
                }
                cachedListenClasses.clear()
                cachedServiceOperations.clear()
            }
        }
        serviceConn?.let {
            this.option?.context?.let { ctx ->
                ctx.bindService(Intent(ctx, ChatBase::class.java), it, Service.BIND_AUTO_CREATE)
            }
        }
    }

    /**
     * If the detection fails, it means the service is unavailable, and [NoServiceException] will be triggered
     * */
    private fun getService(tag: String, ignoreNull: Boolean = false): ChatBase<T>? {
        if (!ignoreNull && (baseConnectionService == null || !isServiceConnected)) {
            postIMError(NoServiceException("at $tag \n connectionService == null ,you must restart the sdk and recreate the service"))
            return null
        }
        return baseConnectionService
    }

    /**
     * A method in [RunnerClientStub] will be detected and executed. Refer to [getService] for detection
     * */
    private fun getServiceOrCache(tag: String, d: RunnerClientStub<T>.() -> Unit) {
        getService(tag, false)?.let {
            d.invoke(it)
        } ?: cachedServiceOperations.add(d)
    }

    internal fun getClient(case: String = ""): ClientHub<T>? {
        if (client == null) {
            client = getClient()
            client?.context = option?.context
            d("IMI.getClient", "create client with $case")
        }
        if (client == null) {
            postIMError(NecessaryAttributeEmptyException("can't create a client by null!"))
        }
        return client
    }

    internal fun getServer(case: String = ""): ServerHub<T>? {
        if (server == null) {
            server = getServer()
            d("IMI.getServer", "create server with $case")
        }
        if (server == null) {
            postIMError(NecessaryAttributeEmptyException("can't create a server by null!"))
        }
        return server
    }

    internal fun getNotification(): Notification? {
        return option?.notification
    }

    internal fun getSessionId(): Int {
        return option?.sessionId ?: -1
    }

    abstract fun getClient(): ClientHub<T>

    abstract fun getServer(): ServerHub<T>

    abstract fun onError(e: IMException)

    abstract fun onSdkDeadlyError(e: IMException)

    open fun prepare() {}

    open fun onAppLayerChanged(isHidden: Boolean) {}

    open fun onServiceConnected() {}

    open fun onServiceDisConnected() {}

    open fun onNewListenerRegistered(cls: Class<*>) {}

    /**
     * send a msg ，see [RunnerClientStub.send]
     * */
    fun send(data: T, callId: String, timeOut: Long, isSpecialData: Boolean, ignoreConnecting: Boolean, sendBefore: OnSendBefore<T>?) {
        getServiceOrCache("IMInterface.send") { send(data, callId, timeOut, false, isSpecialData, ignoreConnecting, sendBefore) }
    }

    fun resend(data: T, callId: String, timeOut: Long, isSpecialData: Boolean, ignoreConnecting: Boolean, sendBefore: OnSendBefore<T>?) {
        getServiceOrCache("IMInterface.resend") { send(data, callId, timeOut, true, isSpecialData, ignoreConnecting, sendBefore) }
    }

    fun routeToClient(data: T, callId: String) {
        getServiceOrCache("IMInterface.routeClient") { enqueue(BaseMsgInfo.route(true, callId, data)) }
    }

    fun routeToServer(data: T, callId: String) {
        getServiceOrCache("IMInterface.routeServer") { enqueue(BaseMsgInfo.route(false, callId, data)) }
    }

    fun pause(code: String): Boolean {
        return getClient("IMInterface.pause")?.pause(code) ?: false
    }

    fun resume(code: String): Boolean {
        return getClient("IMInterface.resume")?.resume(code) ?: false
    }

    /**
     * ServerHub will receive a request to reconnect
     * */
    fun reconnect(case: String) {
        getService("IMInterface.reconnect", true)?.correctConnectionState(ConnectionState.RECONNECT, case)
    }

    fun getAppContext(): Application? {
        return option?.context
    }

    fun recordLogs(where: String, log: String?, b: Boolean) {
        printInFile(where, log, b)
    }

    fun recordError(where: String, log: String?, b: Boolean) {
        printErrorInFile(where, log, b)
    }

    fun beginMessageTempRecord(key: String) {
        NetRecordUtils.beginTempRecord(key)
    }

    fun endMessageTempRecord(key: String): NetWorkRecordInfo? {
        return NetRecordUtils.endTempRecord(key)
    }

    fun postError(e: Throwable?) {
        if (e is IMException) {
            postIMError(e)
        } else {
            postIMError(IMException(e?.message, e))
        }
    }

    fun postIMError(e: IMException) {
        recordError("postError", e::class.java.name + e.message, true)
        if (e.errorLevel != IMException.ERROR_LEVEL_ALERT || e is NoServiceException) {
            onSdkDeadlyError(e);return
        } else onError(e)
    }

    protected open fun <A> postToUi(cls: Class<*>?, data: A?, payload: String? = null, onFinish: () -> Unit) {
        postToUIObservers(cls, data, payload, onFinish)
    }

    open fun shutdown(case: String) {
        cachedListenClasses.clear()
        cachedServiceOperations.clear()
        getService("shutDown by $case", true)?.shutDown()
        serviceConn?.let {
            option?.context?.unbindService(it)
        }
        serviceConn = null
        baseConnectionService = null
        client = null
        server = null
    }
}