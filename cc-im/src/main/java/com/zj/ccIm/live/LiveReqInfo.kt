package com.zj.ccIm.live


data class LiveReqInfo(val roomId: Int, val liveId: Long, val isLiver: Boolean, val userId: Int) {

    internal fun getCopyData(): LiveReqInfo {
        return LiveReqInfo(roomId, liveId, isLiver, userId)
    }

}