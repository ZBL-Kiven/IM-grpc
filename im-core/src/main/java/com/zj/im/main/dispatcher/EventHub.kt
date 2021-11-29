package com.zj.im.main.dispatcher

import com.zj.im.chat.enums.SendMsgState
import com.zj.im.chat.enums.ConnectionState
import com.zj.im.chat.modle.BaseMsgInfo
import com.zj.im.chat.modle.MessageHandleType

internal class EventHub<T> {

    fun handle(data: BaseMsgInfo<T>) {
        when (data.type) {
            MessageHandleType.ROUTE_CLIENT -> DataReceivedDispatcher.routeToClient(data.data, data.callId)
            MessageHandleType.ROUTE_SERVER -> DataReceivedDispatcher.routeToServer(data.data, data.callId)
            MessageHandleType.SEND_MSG -> DataReceivedDispatcher.sendMsg(data)
            MessageHandleType.RECEIVED_MSG -> DataReceivedDispatcher.received(data.data, data.sendingState, data.callId)
            MessageHandleType.CONNECT_STATE -> DataReceivedDispatcher.onConnectionStateChange(data.connStateChange ?: ConnectionState.ERROR("null connect state"))
            MessageHandleType.SEND_STATE_CHANGE -> DataReceivedDispatcher.sendingStateChanged(data.sendingState ?: SendMsgState.NONE, data.callId, data.data, data.ignoreStateCheck, data.isResend)
            MessageHandleType.NETWORK_STATE -> DataReceivedDispatcher.onNetworkStateChanged(data.netWorkState)
            MessageHandleType.SEND_PROGRESS_CHANGED -> DataReceivedDispatcher.onSendingProgress(data.callId, data.progress)
            MessageHandleType.LAYER_CHANGED -> DataReceivedDispatcher.onLayerChanged(data.isHidden)
        }
    }
}