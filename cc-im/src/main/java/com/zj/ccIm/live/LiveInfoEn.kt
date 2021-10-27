package com.zj.ccIm.live

class LiveInfoEn(

    val roomId: Int = 0,

    val liveId: Long = 0,

    val msgType: String = "",

    val content: String? = null

) {
    override fun toString(): String {
        return "roomId = $roomId , liveId = $liveId , msgType = $msgType \ncontent = $content"
    }
}