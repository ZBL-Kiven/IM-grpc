package com.zj.im.main

import android.app.Application
import com.zj.im.chat.core.DataStore
import com.zj.im.chat.enums.ConnectionState
import com.zj.im.chat.enums.RuntimeEfficiency
import com.zj.im.chat.enums.SendMsgState
import com.zj.im.chat.exceptions.LooperInterruptedException
import com.zj.im.chat.hub.ClientHub
import com.zj.im.chat.hub.ServerHub
import com.zj.im.chat.modle.BaseMsgInfo
import com.zj.im.main.dispatcher.DataReceivedDispatcher
import com.zj.im.main.dispatcher.EventHub
import com.zj.im.main.impl.IMInterface
import com.zj.im.main.impl.RunnerClientStub
import com.zj.im.main.impl.RunningObserver
import com.zj.im.main.looper.MsgHandlerQueue
import com.zj.im.sender.CustomSendingCallback
import com.zj.im.sender.OnSendBefore
import com.zj.im.sender.SendExecutors
import com.zj.im.sender.SendingPool
import com.zj.im.utils.*
import com.zj.im.utils.log.logger.FileUtils
import com.zj.im.utils.log.logger.initLogCollectors
import com.zj.im.utils.log.logger.printInFile
import java.io.File

@Suppress("MemberVisibilityCanBePrivate")
internal abstract class Runner<T> : RunningObserver(), RunnerClientStub<T>, SendExecutors.SendExecutorsInterface<T> {

    private var dataStore: DataStore<T>? = null
    private var msgLooper: MsgLooper? = null
    private var eventHub: EventHub<T>? = null
    private var sendingPool: SendingPool<T>? = null
    private var runningKey: String = ""
    private var curFrequency: RuntimeEfficiency = RuntimeEfficiency.MEDIUM
    private var curRunningKey: String = ""
    private var context: Application? = null
    private var isInit = false
    private var diskPathName: String = ""
    private var appEvent = object : OnAppEvent {
        override fun appStateChanged(inBackground: Boolean) {
            onLayerChanged(inBackground)
        }
    }

    protected var imi: IMInterface<T>? = null

    abstract fun initBase()

    fun init(imi: IMInterface<T>) {
        this.imi = imi
        if (isInit) {
            printInFile("ChatBase.IM", "SDK already init")
            imi.reconnect("re-init sdk")
            imi.prepare()
            return
        }
        this.context = imi.option?.context
        printInFile("ChatBase.IM", " the SDK init with $runningKey")
        initUtils()
        initBase()
        initQueue()
        initHandler()
        getServer("init and start")?.init(context)
        getClient("init.setRunningKey and start")?.let {
            dataStore?.canSend { it.canSend() }
            dataStore?.canReceive { it.canReceived() }
        }
        isInit = true
        imi.prepare()
    }

    private fun initUtils() {
        AppUtils.init(this)
        AppUtils.addAppEventStateListener(this.appEvent)
        imi?.let {
            val debugEnable = it.option?.debugEnable ?: false
            val logsCollectionAble = it.option?.logsCollectionAble ?: { false }
            val logsMaxRetain = it.option?.logsMaxRetain ?: Constance.MAX_RETAIN_TCP_LOG
            diskPathName = it.option?.logsFileName ?: ""
            initLogCollectors(context, diskPathName, debugEnable, logsCollectionAble, logsMaxRetain)
        }
    }

    private fun initHandler() {
        runningKey = getIncrementKey()
        curRunningKey = runningKey
        curFrequency = imi?.option?.runtimeEfficiency ?: RuntimeEfficiency.MEDIUM
        msgLooper = MsgLooper(curRunningKey, curFrequency.interval, this)
    }

    private fun initQueue() {
        eventHub = EventHub()
        dataStore = DataStore()
        sendingPool = SendingPool()
    }

    fun getClient(case: String): ClientHub<T>? {
        return imi?.getClient(case)
    }

    fun getServer(case: String): ServerHub<T>? {
        return imi?.getServer(case)
    }

    fun <R> sendTo(data: BaseMsgInfo<R>) {
        cast<BaseMsgInfo<R>, BaseMsgInfo<T>>(data)?.let {
            sendingPool?.push(it)
        }
    }

    override fun onLooperPrepared(queue: MsgHandlerQueue) {
        getServer("msg looper prepared")?.onLooperPrepared(queue)
    }

    override fun looperInterrupted() {
        postError(LooperInterruptedException("thread has been destroyed!"))
    }

    override fun <R> enqueue(data: BaseMsgInfo<R>) {
        cast<BaseMsgInfo<R>, BaseMsgInfo<T>>(data)?.let {
            msgLooper?.checkRunning(true)
            setLooperEfficiency(dataStore?.put(it) ?: 0)
        }
    }

    /**
     * send a msg
     * */
    override fun sendMsg(data: T, callId: String, timeOut: Long, isResend: Boolean, isSpecialData: Boolean, ignoreConnecting: Boolean, ignoreSendState: Boolean, customSendingCallback: CustomSendingCallback<T>?, vararg sendBefore: OnSendBefore<T>) {
        if (customSendingCallback == null || customSendingCallback.pending) {
            enqueue(BaseMsgInfo.sendingStateChange(SendMsgState.SENDING, callId, data, isResend, ignoreSendState))
        }
        customSendingCallback?.onStart(callId, ignoreSendState, data)
        enqueue(BaseMsgInfo.sendMsg(data, callId, timeOut, isResend, isSpecialData, ignoreConnecting, ignoreSendState, customSendingCallback, *sendBefore))
    }

    private fun setLooperEfficiency(total: Int) {
        if (!StatusHub.isAlive()) return
        val e = if (DataReceivedDispatcher.isDataEnable()) {
            EfficiencyUtils.getEfficiency(total)
        } else {
            RuntimeEfficiency.MEDIUM
        }
        if (e != curFrequency) {
            curFrequency = e
            printInFile("ChatBase.SetLooperEfficiency", String.format(Constance.LOOPER_EFFICIENCY, curFrequency.name))
            msgLooper?.setFrequency(curFrequency.interval)
        }
    }

    override fun run(runningKey: String): Boolean {
        if (runningKey != curRunningKey) {
            msgLooper?.shutdown()
            correctConnectionState(ConnectionState.ERROR("running key invalid"))
            return false
        }
        var isEmptyQueue = false
        catching {
            dataStore?.pop()?.let {
                eventHub?.handle(it)
            } ?: run {
                isEmptyQueue = true
            }
        }
        catching {
            TimeOutUtils.updateNode()
        }
        catching {
            sendingPool?.pop()?.let {
                isEmptyQueue = false
                sendingPool?.lock()
                SendExecutors(it, imi?.getServer("send"), this)
            } ?: run {
                isEmptyQueue = true
            }
        }
        return isEmptyQueue
    }

    override fun result(isOK: Boolean, retryAble: Boolean, d: BaseMsgInfo<T>, throwable: Throwable?, payloadInfo: Any?) {
        fun notify() {
            if (isOK) {
                printInFile("SendExecutors.send", "the data [${d.callId}] has been send to server")
                enqueue(BaseMsgInfo.sendingStateChange(SendMsgState.SUCCESS, d.callId, d.data, d.isResend, false))
            } else {
                printInFile("SendExecutors.send", "send ${d.callId} was failed with error : ${throwable?.message} , payload = $payloadInfo")
                if (retryAble) enqueue(d) else enqueue(BaseMsgInfo.sendingStateChange(SendMsgState.FAIL.setSpecialBody(payloadInfo), d.callId, d.data, d.isResend, false))
            }
        }
        d.customSendingCallback?.let {
            d.customSendingCallback?.onResult(isOK, retryAble, callId = d.callId, d.data, throwable, payloadInfo)
            if (it.pending) {
                notify()
            }
        } ?: notify()
        sendingPool?.unLock()
    }

    private fun onLayerChanged(isHidden: Boolean) {
        enqueue(BaseMsgInfo.onLayerChange<T>(isHidden))
    }

    fun correctConnectionState(state: ConnectionState) {
        enqueue(BaseMsgInfo.connectStateChange<T>(state))
    }

    fun postError(e: Throwable?) {
        if (e is LooperInterruptedException) {
            if (!isFinishing(curRunningKey)) initHandler()
            else printInFile("ChatBase.IM.LooperInterrupted", " the MsgLooper was stopped by SDK shutDown")
            return
        }
        imi?.postError(e)
    }

    fun getLogsFolder(zipFolderName: String, zipFileName: String): String {
        if (zipFolderName.contains(".")) throw IllegalArgumentException("case: zipFolderName error : zip folder name can not contain with '.'")
        if (zipFolderName.contains(diskPathName)) throw IllegalArgumentException("case: zipFolderName error : zip folder can not create in log file")
        val path = FileUtils.getHomePath(diskPathName)
        if (path.isNotEmpty()) {
            val homeFile = File(path)
            if (homeFile.isDirectory) {
                val zipPath = FileUtils.getHomePath(zipFolderName)
                val zipName = "$zipFileName.zip"
                FileUtils.compressToZip(path, zipPath, zipName)
                val zipFile = File(zipPath, zipName)
                if (zipFile.exists() && zipFile.isFile) return zipFile.path
            }
        }
        return ""
    }

    fun deleteFormQueue(callId: String?) {
        dataStore?.deleteFormQueue(callId)
        sendingPool?.deleteFormQueue(callId)
    }

    fun queryInQueue(callId: String?): Boolean {
        return !callId.isNullOrEmpty() && dataStore?.queryInMsgQueue { it.callId == callId } == true || sendingPool?.queryInSendingQueue { it.callId == callId } == true
    }

    fun cancelMsgTimeOut(callId: String) {
        TimeOutUtils.remove(callId)
    }

    fun isFinishing(runningKey: String?): Boolean {
        return runningKey != this.runningKey
    }

    fun notify(sLog: String? = null): IMInterface<T>? {
        if (!sLog.isNullOrEmpty()) printInFile("ChatBase.IM.Notify", sLog)
        return imi
    }

    open fun shutDown() {
        printInFile("ChatBase.IM", " the SDK has begin shutdown with $runningKey")
        catching { msgLooper?.shutdown() }
        catching { TimeOutUtils.clear() }
        catching { AppUtils.destroy() }
        MainLooper.removeCallbacksAndMessages(null)
        catching { getClient("shutdown call")?.shutdown() }
        catching { getServer("shutdown call")?.shutdown() }
        runningKey = ""
        catching { dataStore?.shutDown() }
        catching { sendingPool?.clear() }
        dataStore = null
        msgLooper = null
        eventHub = null
        sendingPool = null
        isInit = false
        printInFile("ChatBase.IM", " the SDK was shutdown")
    }

    protected fun catching(run: () -> Unit) {
        return try {
            run()
        } catch (e: Exception) {
            postError(e)
        } catch (e: java.lang.Exception) {
            postError(e)
        }
    }
}