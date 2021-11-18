package com.zj.ccIm.live.impl

import com.zj.ccIm.CcIM
import com.zj.ccIm.core.impl.ClientHubImpl
import com.zj.ccIm.live.LiveIMHelper
import com.zj.ccIm.live.LiveInfoEn
import com.zj.im.chat.enums.SendMsgState


class LiveClientHubImpl : ClientHubImpl() {

    companion object {
        const val TYPE_USER_JOIN = "user_join"
        const val TYPE_USER_KICK_OUT = "user_kick_out"
        const val TYPE_SPARK_CHANGE = "spark_change"
        const val TYPE_LIVE_USER_CHANGE = "live_user_change"
        const val TYPE_LIVE_STATUS_CHANGE = "live_status_change"
        const val TYPE_GIFT_RECEIVED = "gift_received"
        const val TYPE_DAN_MU = "dan_mu"
        const val TYPE_LIVE_WARNING = "live_room_warning"
        const val TYPE_LIVE_FROZEN = "live_room_frozen"
        const val TYPE_LIVE_USER_FOLLOW = "user_follow"
    }

    override fun onMsgPatch(data: Any?, callId: String?, isSpecialData: Boolean, sendingState: SendMsgState?, isResent: Boolean, onFinish: () -> Unit) {
        (data as? LiveInfoEn)?.let { onPatchLiveMsg(data, onFinish) } ?: super.onMsgPatch(data, callId, isSpecialData, sendingState, isResent, onFinish)
    }

    private fun onPatchLiveMsg(data: LiveInfoEn, onFinish: () -> Unit) {
        when (data.msgType) {
            LiveIMHelper.CALL_ID_LIVE_REGISTER_LIVE_ROOM -> {
                LiveIMHelper.onLiveRoomRegistered(data)
                onFinish();return
            }
        }
        CcIM.postToUiObservers(LiveInfoEn::class.java, data, data.msgType)
        onFinish()
    }
}