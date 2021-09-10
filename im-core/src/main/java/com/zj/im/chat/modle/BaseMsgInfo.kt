package com.zj.im.chat.modle

import com.zj.im.chat.enums.SendMsgState
import com.zj.im.chat.enums.ConnectionState
import com.zj.im.utils.netUtils.NetWorkInfo
import com.zj.im.sender.OnSendBefore
import com.zj.im.utils.Constance
import com.zj.im.utils.getIncrementNumber

/**
 * Created by ZJJ
 */
enum class MessageHandleType {
    NETWORK_STATE, RECEIVED_MSG, CONNECT_STATE, SEND_MSG, SEND_STATE_CHANGE, SEND_PROGRESS_CHANGED, LAYER_CHANGED, ROUTE_CLIENT, ROUTE_SERVER
}

enum class SendingUp {
    NORMAL, READY, WAIT, CANCEL
}

internal class BaseMsgInfo<T> private constructor() {

    var ignoreConnecting: Boolean = false

    var createdTs: Double = 0.0

    var type: MessageHandleType? = null

    var data: T? = null

    var isSpecialData: Boolean = false

    var connStateChange: ConnectionState? = null

    var netWorkState: NetWorkInfo = NetWorkInfo.UNKNOWN

    var sendingState: SendMsgState? = null

    var isResend: Boolean = false

    var timeOut = Constance.DEFAULT_TIMEOUT

    var onSendBefore: OnSendBefore<T>? = null

    var sendingUp: SendingUp = SendingUp.NORMAL

    var progress: Int = 0

    var joinInTop = false

    var isHidden: Boolean = false

    /**
     *the pending id for per message，
     *
     * it used in the status notification ,example 'timeout' / 'sending status changed' / 'success' /...
     *
     * the default value is uuid
     * */
    var callId: String = ""


    companion object {

        fun <T> onProgressChange(progress: Int, callId: String): BaseMsgInfo<T> {
            return BaseMsgInfo<T>().apply {
                this.callId = callId
                this.progress = progress
                this.type = MessageHandleType.SEND_PROGRESS_CHANGED
            }
        }

        fun <T> sendingStateChange(state: SendMsgState?, callId: String, data: T?, isResend: Boolean): BaseMsgInfo<T> {
            val baseInfo = BaseMsgInfo<T>()
            baseInfo.sendingState = state
            baseInfo.callId = callId
            baseInfo.data = data
            baseInfo.isResend = isResend
            baseInfo.type = MessageHandleType.SEND_STATE_CHANGE
            return baseInfo
        }

        fun <T> networkStateChanged(state: NetWorkInfo): BaseMsgInfo<T> {
            val baseInfo = BaseMsgInfo<T>()
            baseInfo.netWorkState = state
            baseInfo.type = MessageHandleType.NETWORK_STATE
            return baseInfo
        }

        fun <T> connectStateChange(connStateChange: ConnectionState, case: String = ""): BaseMsgInfo<T> {
            val baseInfo = BaseMsgInfo<T>()
            baseInfo.type = MessageHandleType.CONNECT_STATE
            connStateChange.case = case
            baseInfo.connStateChange = connStateChange
            return baseInfo
        }

        fun <T> sendMsg(data: T, callId: String, timeOut: Long, isResend: Boolean, isSpecialData: Boolean, ignoreConnecting: Boolean, sendBefore: OnSendBefore<T>?, joinInTop: Boolean): BaseMsgInfo<T> {
            return BaseMsgInfo<T>().apply {
                this.data = data
                this.callId = callId
                this.timeOut = timeOut
                this.isResend = isResend
                this.joinInTop = joinInTop
                this.onSendBefore = sendBefore
                this.sendingUp = if (sendBefore != null) SendingUp.WAIT else SendingUp.NORMAL
                this.isSpecialData = isSpecialData
                this.createdTs = getIncrementNumber()
                this.type = MessageHandleType.SEND_MSG
                this.ignoreConnecting = ignoreConnecting
                this.sendingState = SendMsgState.SENDING
            }
        }

        fun <T> receiveMsg(callId: String, data: T?, isSpecialData: Boolean): BaseMsgInfo<T> {
            val baseInfo = BaseMsgInfo<T>()
            baseInfo.data = data
            baseInfo.callId = callId
            baseInfo.sendingState = SendMsgState.NONE
            baseInfo.isSpecialData = isSpecialData
            baseInfo.type = MessageHandleType.RECEIVED_MSG
            return baseInfo
        }

        fun <T> onLayerChange(isHidden: Boolean): BaseMsgInfo<T> {
            return BaseMsgInfo<T>().apply {
                this.type = MessageHandleType.LAYER_CHANGED
                this.isHidden = isHidden
                this.ignoreConnecting = true
            }
        }

        fun <T> route(client: Boolean, callId: String, data: T?): BaseMsgInfo<T> {
            return BaseMsgInfo<T>().apply {
                this.callId = callId
                this.data = data
                this.type = if (client) MessageHandleType.ROUTE_CLIENT else MessageHandleType.ROUTE_SERVER
            }
        }
    }
}
