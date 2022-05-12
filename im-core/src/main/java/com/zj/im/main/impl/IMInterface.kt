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
import com.zj.im.chat.exceptions.IMArgumentException
import com.zj.im.chat.exceptions.IMException
import com.zj.im.chat.exceptions.NecessaryAttributeEmptyException
import com.zj.im.chat.exceptions.NoServiceException
import com.zj.im.chat.hub.ClientHub
import com.zj.im.chat.hub.ServerHub
import com.zj.im.chat.interfaces.MessageInterface
import com.zj.im.chat.modle.BaseMsgInfo
import com.zj.im.chat.modle.RouteInfo
import com.zj.im.chat.poster.UIHandlerCreator
import com.zj.im.chat.poster.UIHelperCreator
import com.zj.im.main.ChatBase
import com.zj.im.main.StatusHub
import com.zj.im.sender.CustomSendingCallback
import com.zj.im.sender.OnSendBefore
import com.zj.im.utils.ProcessUtil
import com.zj.im.utils.cast
import com.zj.im.utils.log.NetWorkRecordInfo
import com.zj.im.utils.log.logger.NetRecordUtils
import com.zj.im.utils.log.logger.d
import com.zj.im.utils.log.logger.printErrorInFile
import com.zj.im.utils.log.logger.printInFile
import java.lang.IllegalStateException
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

    private var cachedListenClasses = LinkedBlockingDeque<Class<*>>()
    private var cachedServiceOperations = LinkedBlockingDeque<RunnerClientStub<T>.() -> Unit>()
    private var baseConnectionService: ChatBase<T>? = null
    private var serviceConn: ServiceConnection? = null
    private var client: ClientHub<T>? = null
    private var server: ServerHub<T>? = null
    private var isServiceConnected = false
    internal var option: BaseOption? = null

    fun <T : Any, R : Any> addTransferObserver(classT: Class<T>, classR: Class<R>, uniqueCode: Any, lifecycleOwner: LifecycleOwner? = null): UIHandlerCreator<T, R> {
        if (classT == RouteInfo::class.java) throw IllegalStateException("Must use [#addRouteInfoObserver] to observe the RouteInfo type.")
        return UIHandlerCreator(uniqueCode, lifecycleOwner, classT, classR) { cls ->
            setNewListener(cls)
        }
    }

    fun <T : Any> addReceiveObserver(classT: Class<T>, uniqueCode: Any, lifecycleOwner: LifecycleOwner? = null): UIHelperCreator<T, T, *> {
        return addTransferObserver(classT, classT, uniqueCode, lifecycleOwner).build()
    }

    fun <T : Any> addRouteInfoObserver(classT: Class<T>, uniqueCode: Any, lifecycleOwner: LifecycleOwner? = null): UIHelperCreator<RouteInfo<T>, RouteInfo<T>, *> {
        if (classT == RouteInfo::class.java) throw IllegalStateException("Needn't to use RouteInfo wrapped type, use addRouteInfoUIObserver<Int> such as need to monitor <Route<Int>>")
        val r = RouteInfo<T>(null).javaClass
        return UIHelperCreator(uniqueCode, lifecycleOwner, r, r, null, classT) {}
    }

    inline fun <reified T : Any> addRouteInfoObserver(uniqueCode: Any, lifecycleOwner: LifecycleOwner? = null): UIHelperCreator<RouteInfo<T>, RouteInfo<T>, *> {
        return this.addRouteInfoObserver(T::class.java, uniqueCode, lifecycleOwner)
    }

    inline fun <reified T : Any, reified R : Any> addTransferObserver(uniqueCode: Any, lifecycleOwner: LifecycleOwner? = null): UIHandlerCreator<T, R> {
        return this.addTransferObserver(T::class.java, R::class.java, uniqueCode, lifecycleOwner)
    }

    inline fun <reified T : Any> addReceiveObserver(uniqueCode: Any, lifecycleOwner: LifecycleOwner? = null): UIHelperCreator<T, T, *> {
        return this.addReceiveObserver(T::class.java, uniqueCode, lifecycleOwner)
    }

    private fun <N> setNewListener(cls: Class<N>) {
        if (isServiceConnected) {
            onNewListenerRegistered(cls)
        } else {
            cachedListenClasses.add(cls)
        }
    }

    protected fun initChat(option: BaseOption) {
        this.option = option
        baseConnectionService?.let {
            it.init(this)
            return
        }
        val pid = ProcessUtil.getCurrentProcessName(option.context)
        val packageName = option.context.applicationContext.packageName
        if (pid != packageName) {
            onError(IMArgumentException("unable to start im service , please call it in your app main process!"))
            return
        }
        serviceConn = object : ServiceConnection {
            override fun onServiceDisconnected(name: ComponentName?) {
                isServiceConnected = false
                this@IMInterface.onServiceDisConnected()
            }

            override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
                if (binder !is ChatBase.ConnectionBinder<*>) return
                cast<Any?, ChatBase.ConnectionBinder<T>?>(binder)?.service?.let {
                    baseConnectionService = it
                    baseConnectionService?.init(this@IMInterface)
                    isServiceConnected = true
                    this@IMInterface.onServiceConnected()
                    if (cachedListenClasses.isNotEmpty()) {
                        cachedListenClasses.forEach { cls ->
                            onNewListenerRegistered(cls)
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
        }
        try {
            serviceConn?.let {
                this.option?.context?.let { ctx ->
                    ctx.bindService(Intent(ctx, ChatBase::class.java), it, Service.BIND_AUTO_CREATE)
                }
            }
        } catch (e: Exception) {
            onError(IMArgumentException("unable to start im service , case: ${e.message}"))
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

    protected abstract fun getClient(): ClientHub<T>

    protected abstract fun getServer(): ServerHub<T>

    abstract fun onError(e: IMException)

    abstract fun onSdkDeadlyError(e: IMException)

    open fun prepare() {}

    open fun onAppLayerChanged(isHidden: Boolean) {}

    open fun onServiceConnected() {}

    open fun onServiceDisConnected() {}

    protected open fun onNewListenerRegistered(cls: Class<*>) {}

    /**
     * send a msg ，see [RunnerClientStub.sendMsg]
     * */
    fun send(data: T, callId: String, timeOut: Long, isSpecialData: Boolean, ignoreConnecting: Boolean, ignoreSendState: Boolean, customSendingCallback: CustomSendingCallback<T>? = null, vararg sendBefore: OnSendBefore<T>) {
        getServiceOrCache("IMInterface.send") { sendMsg(data, callId, timeOut, false, isSpecialData, ignoreConnecting, ignoreSendState, customSendingCallback, *sendBefore) }
    }

    fun resend(data: T, callId: String, timeOut: Long, isSpecialData: Boolean, ignoreConnecting: Boolean, ignoreSendState: Boolean, customSendingCallback: CustomSendingCallback<T>? = null, vararg sendBefore: OnSendBefore<T>) {
        getServiceOrCache("IMInterface.resend") { sendMsg(data, callId, timeOut, true, isSpecialData, ignoreConnecting, ignoreSendState, customSendingCallback, *sendBefore) }
    }

    fun <CLS : Any> routeToUi(data: RouteInfo<CLS>, callId: String) {
        postToUi(RouteInfo::class.java, data, callId) {
            d("IMInterface.routeToUi", "the data ${data.data} has been route to ui observers")
        }
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
        getService("IMInterface.reconnect", true)?.correctConnectionState(ConnectionState.RECONNECT(case))
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

    fun checkHasReconnectionStatus(): Boolean {
        return StatusHub.hasReConnectionState.get()
    }

    protected open fun <A> postToUi(cls: Class<*>?, data: A?, payload: String? = null, onFinish: () -> Unit) {
        postToUIObservers(cls, data, payload, onFinish)
    }

    open fun shutdown(case: String) {
        cachedListenClasses.clear()
        cachedServiceOperations.clear()
        getService("shutDown by $case", true)?.shutDown()
        kotlin.runCatching {
            serviceConn?.let {
                option?.context?.unbindService(it)
            }
        }
        serviceConn = null
        baseConnectionService = null
        client = null
        server = null
    }
}