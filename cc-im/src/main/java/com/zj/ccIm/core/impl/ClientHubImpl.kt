package com.zj.ccIm.core.impl

import android.util.Log
import com.google.gson.Gson
import com.zj.ccIm.core.Comment
import com.zj.ccIm.core.bean.SendMessageRespEn
import com.zj.im.chat.enums.SendMsgState
import com.zj.im.chat.hub.ClientHub
import com.zj.ccIm.core.Constance
import com.zj.ccIm.core.IMHelper
import com.zj.ccIm.core.bean.DeleteSessionInfo
import com.zj.database.entity.SendMessageReqEn
import com.zj.database.entity.*
import com.zj.ccIm.core.bean.LastMsgReqBean
import com.zj.ccIm.core.bean.PrivateFansEn
import com.zj.ccIm.core.db.*
import com.zj.ccIm.core.sender.Converter
import com.zj.ccIm.core.sender.Sender
import com.zj.ccIm.logger.ImLogs
import com.zj.im.utils.cast
import com.zj.protocol.grpc.ImMessage

class ClientHubImpl : ClientHub<Any?>() {

    companion object {
        const val PAYLOAD_ADD = "add"
        const val PAYLOAD_DELETE = "delete"
        const val PAYLOAD_CHANGED = "change"
        const val PAYLOAD_CHANGED_SEND_STATE = "change_send_state"
        const val PAYLOAD_DELETE_FROM_SENSITIVE_WORDS = "delete_case_sensitive_words"
    }

    /**
     * running in WorkerThread
     * When a message arrives from any channel (server or other content that actively Post to the queue),
     * use this method to process and decide whether to push to the observer
     * Use [isInterruptData] to intercept content that you don't want to be pushed to the UI.
     * @param data can only be an instance or a collection of instances
     * @param callId is uniformly designated by the sender's front end Unique identifier, only valid at the front end.
     * @param isSpecialData The special type will get the highest priority of the queue.
     * @param sendingState The message sending state is usually returned by [ServerHubImpl]. Whether
     * @param isResent is marked as retransmitted, only the message sent by calling [com.zj.im.main.impl.IMInterface.resend] will get the TRUE flag.
     * @param onFinish is called to unblock the queue. By default, it is called after ClientHub pushes.
     * */
    override fun onMsgPatch(data: Any?, callId: String?, isSpecialData: Boolean, sendingState: SendMsgState?, isResent: Boolean, onFinish: () -> Unit) {
        var d = data
        var payload: String? = callId
        try {
            if (isInterruptData(callId, data, sendingState)) {
                onFinish();return
            }
            when (callId) {
                Constance.TOPIC_IM_MSG -> {
                    val info = Gson().fromJson(d?.toString(), SessionLastMsgInfo::class.java)
                    val deal = SessionLastMsgDbOperator.onDealSessionLastMsgInfo(info)
                    payload = deal?.first
                    d = if (deal?.second == null) d else deal.second
                }
                Constance.TOPIC_GROUP_INFO -> {
                    val deal = SessionDbOperator.onDealSessionInfo(d?.toString())
                    payload = deal?.first ?: callId
                    d = if (deal?.second == null) d else deal.second
                }
                Constance.TOPIC_CHAT_OWNER_INFO -> {
                    val info = Gson().fromJson(d?.toString(), PrivateOwnerEntity::class.java)
                    PrivateOwnerDbOperator.insertANewChatInfo(info)
                }
                Constance.TOPIC_CHAT_FANS_INFO -> {
                    val info = Gson().fromJson(d?.toString(), PrivateFansEn::class.java)
                    IMHelper.postToUiObservers(info, PAYLOAD_ADD)
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
                    }
                }
                else -> {
                    if (d is Collection<*>) {
                        (d as? Collection<*>)?.let {
                            val cls = it.firstOrNull()?.javaClass
                            val deal = dealWithDb(cls, null, it, callId, sendingState)
                            d = if (deal.second.isNullOrEmpty()) d else deal.second
                        }
                    } else {
                        val deal = dealWithDb(d?.javaClass, d, null, callId, sendingState)
                        d = if (deal.first == null) d else deal.first
                        payload = if (deal.third.isNullOrEmpty()) callId else deal.third
                    }
                }
            }
            super.onMsgPatch(d, payload, isSpecialData, sendingState, isResent, onFinish)
        } catch (e: Exception) {
            ImLogs.requireToPrintInFile("onMsgPatch", "parse received msg error with :\ncallId = $callId\nerror = ${e.message} \ndata = $d")
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
        if (callId == Constance.CALL_ID_REGISTERED_CHAT) {
            IMHelper.onMsgRegistered(Gson().fromJson(d?.toString(), LastMsgReqBean::class.java))
            return true
        }
        if (callId == Constance.CALL_ID_GET_OFFLINE_MESSAGES_SUCCESS) {
            IMHelper.resume(Constance.FETCH_OFFLINE_MSG_CODE)
            onDispatchSentErrorMsg(d as Long)
        }
        if (sendingState == SendMsgState.NONE && callId?.startsWith(Constance.CALL_ID_GET_OFFLINE_MESSAGES) == true) {
            return false
        }
        return interruptDefault
    }

    private fun dealWithDb(cls: Class<*>?, d: Any?, dc: Collection<*>?, callId: String?, sendingState: SendMsgState?): Triple<Any?, List<Any?>?, String?> {
        var first: Any? = null
        val second = arrayListOf<Any?>()
        var pl: String? = null
        when (cls) {
            SendMessageReqEn::class.java -> {
                if (d != null) {
                    val sst = sendingState ?: SendMsgState.SENDING
                    val msg = Converter.exchangeMsgInfoBySendingInfo(d as SendMessageReqEn, sst)
                    val r = MessageDbOperator.onDealMessages(msg, callId, sendingState)
                    first = r?.first
                    pl = r?.second
                }
            }
            SendMessageRespEn::class.java -> {
                if (d != null) {
                    val r = SendingDbOperator.onDealMsgSentInfo(d as SendMessageRespEn, callId, sendingState)
                    first = r?.first
                    pl = r?.second
                }
            }
            ImMessage::class.java, MessageInfoEntity::class.java -> {
                if (!dc.isNullOrEmpty()) {
                    dc.forEach {
                        val r = MessageDbOperator.onDealMessages(it, callId, sendingState)
                        second.add(r?.first)
                    }
                }
                if (d != null) {
                    val r = MessageDbOperator.onDealMessages(d, callId, sendingState)
                    first = r?.first
                    pl = r?.second
                }
            }
        }
        return Triple(first, second, pl)
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
            Constance.CALL_ID_CLEAR_SESSION_BADGE -> {
                val deal = BadgeDbOperator.clearGroupBadge(data as Long)
                IMHelper.postToUiObservers(deal?.second, deal?.first)
            }
            Constance.CALL_ID_DELETE_SESSION -> {
                val d = data as DeleteSessionInfo
                when (d.pl) {
                    Comment.DELETE_OWNER_SESSION -> PrivateOwnerDbOperator.deleteSession(d.targetId ?: return)
                    Comment.DELETE_FANS_SESSION -> {
                        val en = PrivateFansEn().apply { this.userId = d.targetId }
                        IMHelper.postToUiObservers(en, PAYLOAD_DELETE)
                    }
                }
            }
        }
    }

    private fun onDispatchSentErrorMsg(groupId: Long) {
        IMHelper.withDb {
            val resendMsg = it.sendMsgDao().findAllBySessionId(groupId)
            resendMsg?.forEach { msg ->
                if (msg.autoResendWhenBootStart) Sender.resendMessage(msg)
                else dealWithDb(msg.javaClass, msg, null, msg.clientMsgId, SendMsgState.FAIL)
            }
        }
    }

    override fun progressUpdate(progress: Int, callId: String) {
        Log.e("------ ", "sending progress change , callId = $callId ===> $progress")
    }
}