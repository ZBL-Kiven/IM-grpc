package com.zj.ccIm.core.impl

import android.util.Log
import com.google.gson.Gson
import com.zj.ccIm.core.Comment
import com.zj.im.chat.enums.SendMsgState
import com.zj.im.chat.hub.ClientHub
import com.zj.ccIm.core.Constance
import com.zj.ccIm.CcIM
import com.zj.ccIm.core.IMHelper
import com.zj.ccIm.core.bean.*
import com.zj.database.entity.SendMessageReqEn
import com.zj.database.entity.*
import com.zj.ccIm.core.db.*
import com.zj.ccIm.error.AuthenticationError
import com.zj.ccIm.logger.ImLogs
import com.zj.im.utils.cast
import com.zj.protocol.grpc.ImMessageReply

open class ClientHubImpl : ClientHub<Any?>() {

    companion object {

        const val PAYLOAD_FETCH_GROUP_SESSION = "ClientHubImpl.payload_fetch_group_session"
        const val PAYLOAD_FETCH_OWNER_SESSION = "ClientHubImpl.payload_fetch_owner_session"

        const val PAYLOAD_ADD = "ClientHubImpl.payload_add"
        const val PAYLOAD_DELETE = "ClientHubImpl.payload_delete"
        const val PAYLOAD_CHANGED = "ClientHubImpl.payload_change"
        const val PAYLOAD_CHANGED_SEND_STATE = "ClientHubImpl.payload_change_send_state"
        const val PAYLOAD_DELETE_FROM_BLOCKED = PAYLOAD_DELETE + "_case_block"
        const val PAYLOAD_DELETE_FROM_SENSITIVE_WORDS = PAYLOAD_DELETE + "_case_sensitive_words"
        const val PAYLOAD_DELETE_FROM_GROUP_MEMBER_NOT_EXIST = PAYLOAD_DELETE + "_case_not_following"
        const val PAYLOAD_DELETE_NOT_ENOUGH = PAYLOAD_DELETE + "_case_not_enough_coins"
        const val PAYLOAD_DELETE_NOT_OWNER = PAYLOAD_DELETE + "_case_not_owner"
        const val PAYLOAD_DELETE_GROUP_STOPPED = PAYLOAD_DELETE + "_case_group_stopped"
        const val PAYLOAD_DELETE_REPEAT_ANSWER = PAYLOAD_DELETE + "_case_not_repeat_answer"
        const val PAYLOAD_DELETE_DIAMOND_NOT_ENOUGH = PAYLOAD_DELETE + "_case_diamond_not_enough"
    }

    /**
     * running in WorkerThread
     * When a message arrives from any channel (server or other content that actively Post to the queue),
     * use this method to process and decide whether to push to the observer
     * Use [isInterruptData] to intercept content that you don't want to be pushed to the UI.
     * @param data can only be an instance or a collection of instances
     * @param callId is uniformly designated by the sender's front end Unique identifier, only valid at the front end.
     * @param ignoreSendState This data request is not pushed to UIObservers.
     * @param sendingState The message sending state is usually returned by [ServerHubImpl]. Whether
     * @param isResent is marked as retransmitted, only the message sent by calling [com.zj.im.main.impl.IMInterface.resend] will get the TRUE flag.
     * @param onFinish is called to unblock the queue. By default, it is called after ClientHub pushes.
     * */
    override fun onMsgPatch(data: Any?, callId: String?, ignoreSendState: Boolean, sendingState: SendMsgState?, isResent: Boolean, onFinish: () -> Unit) {
        var d = data
        var payload: String? = callId
        try {
            if (isInterruptData(callId, data, sendingState)) {
                onFinish();return
            }
            when (callId) {
                Constance.TOPIC_IM_MSG, Constance.TOPIC_CHAT_OWNER_INFO, Constance.TOPIC_CHAT_FANS_INFO -> {
                    val info = Gson().fromJson(d?.toString(), SessionLastMsgInfo::class.java)
                    val deal = SessionLastMsgDbOperator.dealSessionLastMsgInfo(callId, info)
                    payload = deal?.first
                    d = if (deal?.second == null) d else deal.second
                }
                Constance.TOPIC_KICK_OUT -> {
                    CcIM.postError(AuthenticationError(d.toString()));return
                }
                Constance.TOPIC_ROLE -> {
                    val deal = SessionDbOperator.onDealSessionRoleInfo(d?.toString())
                    payload = deal?.first ?: callId
                    d = if (deal?.second == null) d else deal.second
                }
                Constance.TOPIC_ASSETS_CHANGED -> {
                    AssetsChangedOperator.changeAssetsWithTopic(d.toString());return
                }
                Constance.TOPIC_LIVE_STATE -> {
                    d = Gson().fromJson(d?.toString(), LiveStateInfo::class.java)
                }
                Constance.TOPIC_GROUP_INFO -> {
                    val deal = SessionDbOperator.onDealSessionInfo(d?.toString())
                    payload = deal?.first ?: callId
                    d = if (deal?.second == null) d else deal.second
                }
                Constance.CALL_ID_GET_OFFLINE_CHAT_MESSAGES -> {
                    cast<Any?, Map<String, List<MessageInfoEntity>>?>(data)?.forEach { (k, v) ->
                        val trans = arrayListOf<MessageInfoEntity>()
                        v.forEach {
                            val t = MessageDbOperator.onDealMessages(it, callId, sendingState)?.first
                            if (t != null) trans.add(t)
                        }
                        payload = k
                        d = trans
                        CcIM.postToUiObservers(MessageInfoEntity::class.java, d, payload)
                    }
                    onFinish();return
                }
                else -> {
                    if (d is Collection<*>) {
                        (d as? Collection<*>)?.let {
                            val cls = it.firstOrNull()?.javaClass
                            val deal = dealWithDb(cls, null, it, callId, sendingState, ignoreSendState)
                            d = if (deal?.second.isNullOrEmpty()) d else deal?.second
                        }
                    } else {
                        val deal = dealWithDb(d?.javaClass, d, null, callId, sendingState, ignoreSendState)
                        d = if (deal?.first == null) d else deal.first
                        payload = if (deal?.third.isNullOrEmpty()) callId else deal?.third
                    }
                }
            }
            super.onMsgPatch(d, payload, ignoreSendState, sendingState, isResent, onFinish)
        } catch (e: Exception) {
            ImLogs.recordErrorInFile("onMsgPatch", "parse received msg error with :\ncallId = $callId\nerror = ${e.message} \ndata = $d")
            onFinish()
            return
        }
    }

    /**
     * This is used to intercept content that you don't want to be pushed to UI observers
     * @return true is no longer push
     * */
    private fun isInterruptData(callId: String?, d: Any?, sendingState: SendMsgState?): Boolean {
        val interruptDefault = callId?.startsWith(Constance.INTERNAL_CALL_ID_PREFIX) == true
        if (callId?.startsWith(Constance.CALL_ID_REGISTERED_CHAT) == true) {
            val req = d as ImMessageReply.ReqContext
            val bean = ChannelRegisterInfo(null, req.groupId, req.ownerId.toInt(), req.targetUserId.toInt(), req.channel)
            IMHelper.onMsgRegistered(bean)
            return true
        }
        if (callId?.startsWith(Constance.CALL_ID_LEAVE_FROM_CHAT_ROOM) == true) {
            return true
        }
        if (callId?.startsWith(Constance.CALL_ID_REGISTER_CHAT) == true) {
            BadgeDbOperator.clearGroupBadge(d as ChannelRegisterInfo)
            return true
        }
        if (callId?.startsWith(Constance.CALL_ID_LEAVE_CHAT_ROOM) == true) {
            val bean: ChannelRegisterInfo = if (d is ChannelRegisterInfo) d else {
                val req = d as ImMessageReply.ReqContext
                ChannelRegisterInfo(null, req.groupId, req.ownerId.toInt(), req.targetUserId.toInt(), req.channel)
            }
            BadgeDbOperator.clearGroupBadge(bean)
            return true
        }
        if (callId == Constance.CALL_ID_GET_OFFLINE_MESSAGES_SUCCESS) {
            CcIM.resume(Constance.FETCH_OFFLINE_MSG_CODE)
            onDispatchSentErrorMsg(d as ChannelRegisterInfo)
            return true
        }
        if (sendingState == SendMsgState.NONE && callId?.startsWith(Constance.CALL_ID_GET_MESSAGES) == true) {
            return false
        }
        return interruptDefault
    }

    private fun dealWithDb(cls: Class<*>?, d: Any?, dc: Collection<*>?, callId: String?, sendingState: SendMsgState?, ignoreSendState: Boolean): Triple<Any?, List<Any?>?, String?>? {
        var first: Any? = null
        val second = arrayListOf<Any?>()
        var pl: String? = null
        when (cls) {
            SendMessageReqEn::class.java -> {
                val r = SendingDbOperator.onDealSendMsgReqInfo(d as? SendMessageReqEn, sendingState, callId)
                first = r?.first
                pl = r?.second
            }
            SendMessageRespEn::class.java -> {
                val r = SendingDbOperator.onDealSendMsgRespInfo(d as? SendMessageRespEn, sendingState, callId)
                first = r?.first
                pl = r?.second
            }
            MessageInfoEntity::class.java -> {
                if (!dc.isNullOrEmpty()) {
                    dc.forEach {
                        val r = MessageDbOperator.onDealMessages(it as? MessageInfoEntity, callId, sendingState)
                        second.add(r?.first)
                    }
                }
                if (d != null) {
                    val r = MessageDbOperator.onDealMessages(d as? MessageInfoEntity, callId, sendingState)
                    first = r?.first
                    pl = r?.second
                }
            }
        }
        return if (!ignoreSendState) Triple(first, second, pl) else null
    }

    override fun onRouteCall(callId: String?, data: Any?) {
        when (callId) {
            Constance.CALL_ID_START_LISTEN_SESSION -> {
                SessionDbOperator.notifyAllSession(callId)
            }
            Constance.CALL_ID_START_LISTEN_TOTAL_DOTS -> {
                SessionLastMsgDbOperator.notifyAllSessionDots(callId)
            }
            Constance.CALL_ID_START_LISTEN_PRIVATE_OWNER_CHAT -> {
                PrivateOwnerDbOperator.notifyAllSession(callId)
            }
            Constance.CALL_ID_DELETE_SESSION -> {
                val d = data as DeleteSessionInfo
                when (d.status) {
                    Comment.DELETE_OWNER_SESSION -> {
                        PrivateOwnerDbOperator.deleteSession(d.groupId)
                    }
                    Comment.DELETE_FANS_SESSION -> {
                        PrivateFansDbOperator.deleteSession(callId, d.targetUserId, d.groupId)
                    }
                }
            }
        }
    }

    private fun onDispatchSentErrorMsg(bean: ChannelRegisterInfo) {
        IMHelper.withDb {
            val resendMsg = it.sendMsgDao().findAllByKey(bean.key)
            resendMsg?.forEach { msg ->
                if (msg.autoResendWhenBootStart) IMHelper.Sender.resendMessage(msg)
                else {
                    val fm = dealWithDb(msg.javaClass, msg, null, msg.clientMsgId, SendMsgState.FAIL, false)
                    CcIM.postToUiObservers(null, fm?.first, fm?.third)
                }
            }
        }
    }

    override fun progressUpdate(progress: Int, callId: String) {
        Log.e("------ ", "sending progress change , callId = $callId ===> $progress")
    }
}