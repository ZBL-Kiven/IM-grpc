package com.zj.ccIm.core.bean

import com.zj.ccIm.annos.MsgFetchType
import com.zj.ccIm.core.fecher.FetchMsgChannel

data class LastMsgReqBean(val groupId: Long, val ownerId: Long, val targetUserId: Long?, val msgId: Long?, @MsgFetchType val type: Int, val channels: Array<out FetchMsgChannel>) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as LastMsgReqBean

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
        result = 31 * result + channels.contentHashCode()
        return result
    }
}
