package com.zj.ccIm.live.impl


import com.zj.ccIm.core.impl.ServerHubImpl
import com.zj.ccIm.core.impl.ServerImplGrpc
import com.zj.ccIm.live.LiveIMHelper
import com.zj.ccIm.live.LiveInfoEn
import com.zj.ccIm.live.LiveReqInfo
import com.zj.ccIm.logger.ImLogs
import com.zj.protocol.grpc.LiveRoomMessageReply
import com.zj.protocol.grpc.LiveRoomMessageReq
import io.grpc.stub.StreamObserver
import java.lang.NullPointerException


internal class LiveServerHubImpl : ServerHubImpl() {

    private var liveStreamObserver: StreamObserver<LiveRoomMessageReq>? = null

    override fun onConnection() {
        super.onConnection()
        listenLiveData()
    }

    override fun onRouteCall(callId: String?, data: Any?) {
        if (callId?.startsWith(LiveIMHelper.CALL_ID_LIVE) == true) {
            when (callId) {
                LiveIMHelper.CALL_ID_LIVE_REGISTER_LIVE_ROOM -> {
                    liveRoomEvent(data as? LiveReqInfo, LiveRoomMessageReq.Op.JOIN)
                }
                LiveIMHelper.CALL_ID_LIVE_LEAVE_LIVE_ROOM -> {
                    liveRoomEvent(data as? LiveReqInfo, LiveRoomMessageReq.Op.LEAVE)
                }
            }
        } else super.onRouteCall(callId, data)
    }

    private fun listenLiveData() {
        liveStreamObserver = withChannel(true) {
            it.liveRoomMessage(object : ServerImplGrpc.CusObserver<LiveRoomMessageReply>() {
                override fun onResult(isOk: Boolean, data: LiveRoomMessageReply?, t: Throwable?) {
                    if (isOk && data != null) {
                        val respInfo = LiveInfoEn(data.roomId, data.liveId, data.msgType, data.content)
                        ImLogs.d("on live data received:", respInfo.toString())
                        when (data.msgType) {
                            LiveClientHubImpl.TYPE_USER_JOIN -> {
                                postReceivedMessage(LiveIMHelper.CALL_ID_LIVE_REGISTER_LIVE_ROOM, respInfo, false, data.serializedSize.toLong())
                            }
                            LiveClientHubImpl.TYPE_USER_KICK_OUT -> {
                                postReceivedMessage(LiveIMHelper.CALL_ID_LIVE_LEAVE_LIVE_ROOM, respInfo, false, data.serializedSize.toLong())
                            }
                            else -> postReceivedMessage(LiveIMHelper.CALL_ID_LIVE_NEW_MESSAGE, respInfo, false, data.serializedSize.toLong())
                        }
                    } else onParseError(t)
                }
            })
        }
    }

    private fun liveRoomEvent(data: LiveReqInfo?, type: LiveRoomMessageReq.Op) {
        if (data == null) throw NullPointerException("live event req data should not be null! ")
        val info = LiveRoomMessageReq.newBuilder()
        info.liveId = data.liveId
        info.roomId = data.roomId
        info.userId = (data.userId).toLong()
        info.liverIsMe = data.isLiver
        info.op = type
        liveStreamObserver?.onNext(info.build())
        ImLogs.d("live req sent to server:", data.toString() + ", type = ${type.name}")
    }
}