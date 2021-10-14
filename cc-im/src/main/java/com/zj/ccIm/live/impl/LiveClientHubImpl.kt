package com.zj.ccIm.live.impl

import com.zj.ccIm.core.impl.ClientHubImpl
import com.zj.ccIm.live.LiveInfoEn
import com.zj.im.chat.enums.SendMsgState


class LiveClientHubImpl : ClientHubImpl() {

    override fun onMsgPatch(data: Any?, callId: String?, isSpecialData: Boolean, sendingState: SendMsgState?, isResent: Boolean, onFinish: () -> Unit) {
        (data as? LiveInfoEn)?.let { onPatchLiveMsg(data, callId, isSpecialData, sendingState, isResent, onFinish) } ?: super.onMsgPatch(data, callId, isSpecialData, sendingState, isResent, onFinish)
    }

    private fun onPatchLiveMsg(data: LiveInfoEn, callId: String?, specialData: Boolean, sendingState: SendMsgState?, resent: Boolean, onFinish: () -> Unit) {

    }

}