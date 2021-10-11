package com.zj.ccIm.core.bean

import com.zj.ccIm.annos.MsgFetchType
import com.zj.ccIm.core.fecher.FetchMsgChannel

data class LastMsgReqBean constructor(val groupId: Long, val ownerId: Long, val targetUserid: Long?, val msgId: Long?, @MsgFetchType val type: Int? = null, var channels: Array<out FetchMsgChannel>) {

    private var channel: List<String>? = null

    internal var callIdPrivate: String = ""

    internal fun setChannels() {
        val lst = arrayListOf<FetchMsgChannel>()
        channel?.forEach { v ->
            val fc = FetchMsgChannel.values().firstOrNull { it.serializeName == v }
            if (fc != null) lst.add(fc)
        }
        channels = lst.toTypedArray()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as LastMsgReqBean

        if (groupId != other.groupId) return false
        if (ownerId != other.ownerId) return false
        if (targetUserid != other.targetUserid) return false
        if (!channels.contentEquals(other.channels)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = groupId.hashCode()
        result = 31 * result + ownerId.hashCode()
        result = 31 * result + targetUserid.hashCode()
        result = 31 * result + channels.hashCode()
        return result
    }
}
