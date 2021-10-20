package com.zj.ccIm.live.error

import com.zj.ccIm.live.LiveInfoEn
import com.zj.ccIm.live.LiveReqInfo

data class ChatRoomArgumentsException(val message: String, val target: LiveReqInfo?, val liveReqInfo: LiveInfoEn?)