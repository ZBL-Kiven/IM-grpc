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
    private var tempRecordInfo = mutableMapOf<String, NetWorkRecordInfo>()
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

    fun beginTempRecord(key: String) {
        tempRecordInfo[key] = NetWorkRecordInfo()
    }

    fun endTempRecord(key: String): NetWorkRecordInfo? {
        return tempRecordInfo.remove(key)?.let {
            NetWorkRecordInfo(it.startedTs).apply {
                lastModifySendData = it.lastModifySendData
                lastModifyReceiveData = it.lastModifyReceiveData
                disconnectCount = it.disconnectCount
                sentSize = it.sentSize
                receivedSize = it.receivedSize
                sentCount = it.sentCount
                receivedCount = it.receivedCount
                total = it.total
                this.modify()
            }
        }
    }

    fun addRecordListener(onRecord: ((NetWorkRecordInfo) -> Unit)) {
        NetRecordUtils.onRecord = onRecord
    }

    @JvmStatic
    fun recordDisconnectCount() {
        if (accessAble) {
            val disconnectCount = (netWorkRecordInfo?.disconnectCount ?: 0) + 1
            netWorkRecordInfo?.disconnectCount = disconnectCount
            refTemp { it.disconnectCount = disconnectCount }
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
            refTemp {
                it.lastModifySendData = lastModifySendData
                it.receivedSize += lastModifySendData
                it.sentCount += 1
                it.total = it.sentSize + it.receivedSize
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
            refTemp {
                it.lastModifyReceiveData = lastModifyReceiveData
                it.sentSize += lastModifyReceiveData
                it.receivedCount += 1
                it.total = it.sentSize + it.receivedSize
            }
        }
    }

    @JvmStatic
    fun getRecordInfo(): NetWorkRecordInfo? {
        return if (!accessAble) null else netWorkRecordInfo
    }

    private fun refTemp(r: (NetWorkRecordInfo) -> Unit) {
        tempRecordInfo.values.forEach {
            r(it)
            it.modify()
        }
    }

    private fun record(info: NetWorkRecordInfo?) {
        if (info == null) return
        info.modify()
        if (info != netWorkRecordInfo) netWorkRecordInfo = info
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
