package com.zj.ccIm.live


import com.zj.ccIm.core.IMHelper
import com.zj.ccIm.live.error.ChatRoomArgumentsException
import com.zj.im.chat.enums.ConnectionState

@Suppress("unused", "MemberVisibilityCanBePrivate")
object LiveIMHelper : (ConnectionState) -> Unit {

    internal const val CALL_ID_LIVE = "call_id_live_"
    internal const val CALL_ID_LIVE_NEW_MESSAGE = CALL_ID_LIVE + "new_message"
    internal const val CALL_ID_LIVE_REGISTER_LIVE_ROOM = CALL_ID_LIVE + "register_live_room"
    internal const val CALL_ID_LIVE_LEAVE_LIVE_ROOM = CALL_ID_LIVE + "leave_live_room"

    private var checkLastLiveRoomInfo: LiveReqInfo? = null
    private var onLiveConnectListener: ((Boolean) -> Unit)? = null

    fun setLiveConnectionListener(l: ((Boolean) -> Unit)?) {
        this.onLiveConnectListener = l
    }

    fun joinToLiveRoom(req: LiveReqInfo) {
        IMHelper.registerConnectionStateChangeListener(LiveIMHelper::class.java.name, this)
        joinToLiveRoomIfChecked(req)
    }

    private fun joinToLiveRoomIfChecked(req: LiveReqInfo) {
        this.checkLastLiveRoomInfo = req
        IMHelper.routeToServer(req, CALL_ID_LIVE_REGISTER_LIVE_ROOM)
    }

    fun leaveLiveRoom(req: LiveReqInfo? = checkLastLiveRoomInfo) {
        IMHelper.routeToServer(req?.getCopyData(), CALL_ID_LIVE_LEAVE_LIVE_ROOM)
        checkLastLiveRoomInfo = null
        onLiveConnectListener?.invoke(false)
    }

    internal fun onLiveRoomRegistered(data: LiveInfoEn) {
        if (data.roomId != checkLastLiveRoomInfo?.roomId || data.liveId != checkLastLiveRoomInfo?.liveId) {
            IMHelper.postToUiObservers(ChatRoomArgumentsException("your registered id is not same as your target id!", checkLastLiveRoomInfo, data))
        } else {
            onLiveConnectListener?.invoke(true)
        }
    }

    internal fun close() {
        checkLastLiveRoomInfo = null
        onLiveConnectListener?.invoke(false)
    }

    override fun invoke(s: ConnectionState) {
        if (s == ConnectionState.CONNECTED && checkLastLiveRoomInfo != null) {
            checkLastLiveRoomInfo?.let { joinToLiveRoomIfChecked(it) }
        }
    }
}