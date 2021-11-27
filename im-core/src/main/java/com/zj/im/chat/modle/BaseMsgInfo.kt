package com.zj.im.chat.modle

import com.zj.im.chat.enums.SendMsgState
import com.zj.im.chat.enums.ConnectionState
import com.zj.im.sender.CustomSendingCallback
import com.zj.im.utils.netUtils.NetWorkInfo
import com.zj.im.sender.OnSendBefore
import com.zj.im.utils.Constance
import com.zj.im.utils.getIncrementNumber

/**
 * Created by ZJJ
 */
internal enum class MessageHandleType {
    NETWORK_STATE, RECEIVED_MSG, CONNECT_STATE, SEND_MSG, SEND_STATE_CHANGE, SEND_PROGRESS_CHANGED, LAYER_CHANGED, ROUTE_CLIENT, ROUTE_SERVER
}

internal enum class SendingUp {
    NORMAL, READY, WAIT, CANCEL
}

internal class BaseMsgInfo<T> private constructor() {

    var sendWithoutState: Boolean = false

    var ignoreConnecting: Boolean = false

    var ignoreStateCheck: Boolean = false

    var createdTs: Double = 0.0

    var type: MessageHandleType? = null

    var data: T? = null

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

    var customSendingCallback: CustomSendingCallback<T>? = null

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

        fun <T> sendingStateChange(state: SendMsgState?, callId: String, data: T?, isResend: Boolean, ignoreSendState: Boolean): BaseMsgInfo<T> {
            val baseInfo = BaseMsgInfo<T>()
            baseInfo.data = data
            baseInfo.callId = callId
            baseInfo.isResend = isResend
            baseInfo.sendingState = state
            baseInfo.type = MessageHandleType.SEND_STATE_CHANGE
            baseInfo.sendWithoutState = (state == SendMsgState.SENDING || state == SendMsgState.ON_SEND_BEFORE_END) && ignoreSendState
            return baseInfo
        }

        fun <T> networkStateChanged(state: NetWorkInfo): BaseMsgInfo<T> {
            val baseInfo = BaseMsgInfo<T>()
            baseInfo.netWorkState = state
            baseInfo.type = MessageHandleType.NETWORK_STATE
            return baseInfo
        }

        fun <T> connectStateChange(connStateChange: ConnectionState): BaseMsgInfo<T> {
            val baseInfo = BaseMsgInfo<T>()
            baseInfo.type = MessageHandleType.CONNECT_STATE
            baseInfo.connStateChange = connStateChange
            return baseInfo
        }

        fun <T> sendMsg(data: T, callId: String, timeOut: Long, isResend: Boolean, ignoreStateCheck: Boolean, ignoreConnecting: Boolean, ignoreSendState: Boolean, sendBefore: OnSendBefore<T>?, customSendingCallback: CustomSendingCallback<T>?): BaseMsgInfo<T> {
            return BaseMsgInfo<T>().apply {
                this.data = data
                this.callId = callId
                this.timeOut = timeOut
                this.isResend = isResend
                this.onSendBefore = sendBefore
                this.createdTs = getIncrementNumber()
                this.type = MessageHandleType.SEND_MSG
                this.sendWithoutState = ignoreSendState
                this.ignoreStateCheck = ignoreStateCheck
                this.ignoreConnecting = ignoreConnecting
                this.sendingState = SendMsgState.SENDING
                this.customSendingCallback = customSendingCallback
                this.sendingUp = if (sendBefore != null) SendingUp.WAIT else SendingUp.NORMAL
            }
        }

        fun <T> receiveMsg(callId: String, data: T?, isSpecialData: Boolean): BaseMsgInfo<T> {
            val baseInfo = BaseMsgInfo<T>()
            baseInfo.data = data
            baseInfo.callId = callId
            baseInfo.sendingState = SendMsgState.NONE
            baseInfo.ignoreStateCheck = isSpecialData
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
