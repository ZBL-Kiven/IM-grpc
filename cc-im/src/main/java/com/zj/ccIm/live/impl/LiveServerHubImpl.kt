package com.zj.ccIm.live.impl

import com.zj.ccIm.core.impl.ServerHubImpl
import com.zj.im.chat.interfaces.SendingCallBack

class LiveServerHubImpl : ServerHubImpl() {

    override fun send(params: Any?, callId: String, callBack: SendingCallBack<Any?>): Long {
        return super.send(params, callId, callBack)
    }

}