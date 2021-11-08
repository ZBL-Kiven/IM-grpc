package com.zj.im.utils.log

import com.zj.im.utils.full

@Suppress("MemberVisibilityCanBePrivate", "unused")
data class NetWorkRecordInfo(val startedTs: Long = System.currentTimeMillis()) {
    var lastModifyTs: Long = 0L
        private set(value) {
            field = value
            lastModifyTime = full()
        }
    val startedTime: String = full(startedTs)
    var lastModifyTime: String = startedTime; private set
    var lastModifySendData: Long = 0L
    var lastModifyReceiveData: Long = 0L
    var disconnectCount: Long = 0L
    var sentSize: Long = 0L
    var receivedSize: Long = 0L
    var sentCount: Long = 0L
    var receivedCount: Long = 0L
    var total: Long = 0L

    fun modify() {
        lastModifyTs = System.currentTimeMillis()
    }

    @Suppress("unused")
    fun getTcpDataSize(): Long {
        return sentSize + receivedSize
    }
}
