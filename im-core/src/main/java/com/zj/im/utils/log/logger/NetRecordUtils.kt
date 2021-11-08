package com.zj.im.utils.log.logger

import com.zj.im.utils.log.NetRecordChangedListener
import com.zj.im.utils.log.NetWorkRecordInfo
import java.util.concurrent.locks.ReentrantReadWriteLock

/**
 * IM TCP status collector
 * */
@Suppress("unused")
internal object NetRecordUtils : LogCollectionUtils.Config() {

    override fun overriddenFolderName(folderName: String): String {
        return "$folderName/UsageSituation"
    }

    private val path: String; get() = ""
    val name: String; get() = "situations"

    override val subPath: () -> String
        get() = { path }
    override val fileName: () -> String
        get() = { name }

    private val changedListeners = mutableMapOf<String, NetRecordChangedListener>()
    private var accessAble = false
    private var onRecord: ((NetWorkRecordInfo) -> Unit)? = null
    private val rwl = ReentrantReadWriteLock()
    private val r = rwl.readLock()
    private val w = rwl.writeLock()
    private var tempRecordInfo: NetWorkRecordInfo? = null
    private var netWorkRecordInfo: NetWorkRecordInfo? = null
        get() {
            if (!accessAble) return null
            if (field == null) {
                field = getNetRecordInfo() ?: NetWorkRecordInfo()
            }
            return field
        }

    override fun prepare() {
        accessAble = true
    }

    fun beginTempRecord() {
        tempRecordInfo = NetWorkRecordInfo()
    }

    fun endTempRecord(): NetWorkRecordInfo? {
        val temp = tempRecordInfo?.copy()
        tempRecordInfo = null
        return temp
    }

    fun addRecordListener(onRecord: ((NetWorkRecordInfo) -> Unit)) {
        NetRecordUtils.onRecord = onRecord
    }

    @JvmStatic
    fun recordDisconnectCount() {
        if (accessAble) {
            val disconnectCount = (netWorkRecordInfo?.disconnectCount ?: 0) + 1
            netWorkRecordInfo?.disconnectCount = disconnectCount
            tempRecordInfo?.disconnectCount = disconnectCount
            record(netWorkRecordInfo)
        }
    }

    @JvmStatic
    fun recordLastModifySendData(lastModifySendData: Long) {
        if (accessAble) {
            netWorkRecordInfo?.apply {
                this.lastModifySendData = lastModifySendData
                this.receivedSize += lastModifySendData
                this.sentCount += 1
                this.total = sentSize + receivedSize
                record(this)
            }
            tempRecordInfo?.apply {
                this.lastModifySendData = lastModifySendData
                this.receivedSize += lastModifySendData
                this.sentCount += 1
                this.total = sentSize + receivedSize
            }
        }
    }

    @JvmStatic
    fun recordLastModifyReceiveData(lastModifyReceiveData: Long) {
        if (accessAble) {
            netWorkRecordInfo?.apply {
                this.lastModifyReceiveData = lastModifyReceiveData
                this.sentSize += lastModifyReceiveData
                this.receivedCount += 1
                this.total = sentSize + receivedSize
                record(this)
            }
            tempRecordInfo?.apply {
                this.lastModifyReceiveData = lastModifyReceiveData
                this.sentSize += lastModifyReceiveData
                this.receivedCount += 1
                this.total = sentSize + receivedSize
            }
        }
    }

    @JvmStatic
    fun getRecordInfo(): NetWorkRecordInfo? {
        return if (!accessAble) null else netWorkRecordInfo
    }

    private fun record(info: NetWorkRecordInfo?) {
        if (info == null) return
        if (info != netWorkRecordInfo) netWorkRecordInfo = info
        info.lastModifyTs = System.currentTimeMillis()
        val recordString = DataUtils.toString(info)
        write(recordString)
        onRecord?.invoke(info)
    }

    private fun getNetRecordInfo(): NetWorkRecordInfo? {
        return try {
            r.lock()
            val logFile = getLogFile(path, name)
            DataUtils.toModule(getLogText(logFile))
        } finally {
            r.unlock()
        }
    }

    fun removeRecordListener() {
        onRecord = null
    }
}
