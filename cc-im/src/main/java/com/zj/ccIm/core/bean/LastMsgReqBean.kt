package com.zj.ccIm.core.bean

import com.zj.ccIm.annos.MsgFetchType

data class LastMsgReqBean(val groupId: Long, val ownerId: Long, val targetUserid: Long?, val msgId: Long?, @MsgFetchType val type: Int, val channel: Array<out String>) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as LastMsgReqBean

        if (groupId != other.groupId) return false
        if (ownerId != other.ownerId) return false
        if (targetUserid != other.targetUserid) return false
        if (!channel.contentEquals(other.channel)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = groupId.hashCode()
        result = 31 * result + ownerId.hashCode()
        result = 31 * result + targetUserid.hashCode()
        result = 31 * result + channel.contentHashCode()
        return result
    }
}
