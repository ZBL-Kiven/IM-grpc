package com.zj.ccIm.core.bean

import com.zj.ccIm.annos.MsgFetchType
import com.zj.ccIm.core.catching
import com.zj.ccIm.core.fecher.FetchMsgChannel
import java.lang.IllegalArgumentException

data class GetMsgReqBean constructor(val groupId: Long, val ownerId: Int, val targetUserId: Int?, val msgId: Long?, @MsgFetchType val type: Int? = null, var channels: Array<out FetchMsgChannel>) {

    internal fun checkValid(): Boolean {
        val errorMsg = "your call register with channels $channels , but %s is invalid"
        return catching({
            var hasGroupType = false
            var hasPrivateFansType = false
            var hasPrivateOwnerType = false
            channels.forEach {
                if (it.classification == 0) hasGroupType = true
                if (it.classification == 1) hasPrivateFansType = true
                if (it.classification == 2) hasPrivateOwnerType = true
            }
            if (hasGroupType && groupId < 0) throw IllegalArgumentException(String.format(errorMsg, "groupId"))
            if (hasPrivateFansType && ownerId < 0) throw IllegalArgumentException(String.format(errorMsg, "ownerId"))
            if (hasPrivateOwnerType && (targetUserId == null || targetUserId < 0)) throw IllegalArgumentException(String.format(errorMsg, "targetUserId"))
            true
        }, { false }) ?: false
    }

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

    internal fun getCopyData(): GetMsgReqBean {
        return GetMsgReqBean(groupId, ownerId, targetUserId, msgId, type, channels)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as GetMsgReqBean

        if (groupId != other.groupId) return false
        if (ownerId != other.ownerId) return false
        if (targetUserId != other.targetUserId) return false
        if (!channels.contentEquals(other.channels)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = groupId.hashCode()
        result = 31 * result + ownerId.hashCode()
        result = 31 * result + targetUserId.hashCode()
        result = 31 * result + channels.hashCode()
        return result
    }

}
