package com.zj.ccIm.core.impl

import android.util.Log
import com.zj.database.DbHelper
import com.zj.ccIm.core.bean.SendMessageRespEn
import com.zj.im.chat.enums.SendMsgState
import com.zj.im.chat.hub.ClientHub
import com.zj.ccIm.core.Constance
import com.zj.ccIm.core.IMHelper
import com.zj.database.entity.SendMessageReqEn
import com.zj.database.entity.*
import com.zj.protocol.utl.ProtoBeanUtils
import com.google.gson.Gson
import com.zj.ccIm.core.bean.MessageTotalDots
import com.zj.ccIm.core.fecher.Fetcher
import com.zj.ccIm.core.sender.Converter
import com.zj.ccIm.core.sp.SPHelper
import com.zj.ccIm.error.FetchSessionResult
import com.zj.im.chat.poster.log
import com.zj.protocol.grpc.ImMessage

class ClientHubImpl : ClientHub<Any?>() {

    companion object {
        const val PAYLOAD_ADD = "add"
        const val PAYLOAD_DELETE = "delete"
        const val PAYLOAD_CHANGED = "change"
        const val PAYLOAD_CHANGED_SEND_STATE = "change_send_state"
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
        if (isInterruptData(callId, sendingState == SendMsgState.SENDING)) {
            onFinish();return
        }
        var d = data
        var payload: String? = callId
        when (callId) {
            Constance.CALL_ID_CLEAR_SESSION_BADGE -> {
                val deal = clearGroupBadge(d as Long)
                payload = deal?.first
                d = deal?.second
            }
            Constance.TOPIC_IM_MSG -> {
                val info = try {
                    Gson().fromJson(d?.toString(), SessionLastMsgInfo::class.java)
                } catch (e: Exception) {
                    log("parse session error with : ${e.message} \n data = $d");return
                }
                val deal = onDealSessionLastMsgInfo(info)
                payload = deal.first
                d = if (deal.second == null) d else deal.second
            }
            Constance.TOPIC_GROUP_INFO -> {
                val deal = onDealSessionInfo(d?.toString())
                payload = deal?.first ?: callId
                d = if (deal?.second == null) d else deal.second
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
    }

    /**
     * This is used to intercept content that you don't want to be pushed to UI observers
     * @return true is no longer push
     * */
    private fun isInterruptData(callId: String?, b: Boolean): Boolean {
        if (callId == Constance.CALL_ID_REGISTERED_CHAT) {
            IMHelper.onMsgRegistered();return true
        }
        if (!b && callId?.startsWith(Constance.CALL_ID_GET_OFFLINE_MESSAGES) == true) {
            if (IMHelper.resume(Constance.FETCH_OFFLINE_MSG_CODE)) IMHelper.onOfflineMsgPatched()
            return false
        }
        return callId?.startsWith(Constance.INTERNAL_CALL_ID_PREFIX) == true
    }

    private fun dealWithDb(cls: Class<*>?, d: Any?, dc: Collection<*>?, callId: String?, sendingState: SendMsgState?): Triple<Any?, List<Any?>?, String?> {
        var first: Any? = null
        val second = arrayListOf<Any?>()
        var pl: String? = null
        when (cls) {
            SendMessageReqEn::class.java -> {
                if (d != null) {
                    val r = onDealSendingInfo(d as SendMessageReqEn, callId, sendingState)
                    first = r?.first
                    pl = r?.second
                }
            }
            SendMessageRespEn::class.java -> {
                if (d != null) {
                    val r = onDealMsgSentInfo(d as SendMessageRespEn, callId, sendingState)
                    first = r?.first
                    pl = r?.second
                }
            }
            ImMessage::class.java, MessageInfoEntity::class.java -> {
                if (!dc.isNullOrEmpty()) {
                    dc.forEach {
                        val r = onDealMessages(it, callId, sendingState)
                        second.add(r?.first)
                    }
                }
                if (d != null) {
                    val r = onDealMessages(d, callId, sendingState)
                    first = r?.first
                    pl = r?.second
                }
            }
        }
        return Triple(first, second, pl)
    }

    private fun onDealSendingInfo(sen: SendMessageReqEn, callId: String?, sendingState: SendMsgState?): Pair<Any?, String?>? {
        return if (sendingState == SendMsgState.SENDING) {
            val msg = Converter.exchangeMsgInfoBySendingInfo(sen, sendingState)
            onDealMessages(msg, callId, sendingState)
        } else {
            val resp = SendMessageRespEn()
            resp.clientMsgId = sen.clientMsgId
            resp.groupId = sen.groupId
            resp.black = false
            onDealMsgSentInfo(resp, callId, sendingState)
        }
    }

    /**
     * When the message is sent, the state callback event,
     * here is the message that the database already exists in the database and is sent to the UI.
     * */
    private fun onDealMsgSentInfo(d: SendMessageRespEn, callId: String?, sendingState: SendMsgState?): Pair<Any?, String?>? {
        if (sendingState == null) return null
        val msgDb = context?.let { DbHelper.get(it)?.db?.messageDao() }
        val sendDb = context?.let { DbHelper.get(it)?.db?.sendMsgDao() }
        val localMsg = msgDb?.findMsgByClientId(callId)
        localMsg?.sendingState = sendingState.type
        localMsg?.msgId = d.msgId
        localMsg?.sendTime = d.sendTime
        return when (sendingState) {
            SendMsgState.SENDING -> null

            //Callback when the operation of the coroutine is completed
            SendMsgState.ON_SEND_BEFORE_END -> {
                Pair(localMsg, PAYLOAD_CHANGED_SEND_STATE)
            }
            SendMsgState.FAIL, SendMsgState.TIME_OUT -> {
                if (d.black) {
                    msgDb?.deleteAllBySessionId(d.groupId)
                } else {
                    msgDb?.deleteMsgByClientId(d.clientMsgId)
                }
                if (d.black) sendDb?.deleteAllBySessionId(d.groupId)
                Pair(localMsg, if (d.black) PAYLOAD_DELETE else PAYLOAD_CHANGED_SEND_STATE)
            }
            SendMsgState.NONE, SendMsgState.SUCCESS -> {
                sendDb?.deleteByCallId(d.clientMsgId)
                msgDb?.deleteMsgByClientId(d.clientMsgId)
                Pair(localMsg, PAYLOAD_CHANGED_SEND_STATE)
            }
        }
    }

    /**
     * Parse Msg and refresh the contents of the local database cache according to different situations
     * */
    private fun onDealMessages(d: Any?, callId: String?, sendingState: SendMsgState?): Pair<MessageInfoEntity?, String?>? {
        val msg: MessageInfoEntity? = (d as? MessageInfoEntity) ?: (d as? ImMessage)?.let {
            ProtoBeanUtils.toPojoBean(MessageInfoEntity::class.java, d as? ImMessage)
        }
        if (msg != null) {
            if (msg.sendTime <= 0) msg.sendTime = System.currentTimeMillis()
            val msgDb = context?.let { DbHelper.get(it)?.db?.messageDao() }
            val localMsg = if (callId.isNullOrEmpty()) null else msgDb?.findMsgByClientId(callId)
            if (localMsg == null && sendingState == SendMsgState.SENDING) {
                msgDb?.insertOrChangeMessage(msg)
            }
            if (localMsg != null && (sendingState == SendMsgState.SUCCESS || sendingState == SendMsgState.NONE)) {
                val sendDb = context?.let { DbHelper.get(it)?.db?.sendMsgDao() }
                val localSendCache = if (callId.isNullOrEmpty()) null else sendDb?.findByCallId(callId)
                sendDb?.delete(localSendCache)
                msgDb?.deleteMsgByClientId(callId)
            }
            msg.sendingState = sendingState?.type ?: SendMsgState.NONE.type
            val pl = if (localMsg == null) PAYLOAD_ADD else PAYLOAD_CHANGED
            return Pair(msg, pl)
        }
        return null
    }

    private fun onDealSessionInfo(d: String?): Pair<String, Any?>? {
        val info = try {
            Gson().fromJson(d, SessionInfoEntity::class.java)
        } catch (e: Exception) {
            log("parse session error with : ${e.message} \n data = $d"); return null
        }
        val sessionDb = context?.let { DbHelper.get(it)?.db?.sessionDao() }
        val lastMsgDb = context?.let { DbHelper.get(it)?.db?.sessionMsgDao() }
        val exists = sessionDb?.findSessionById(info.groupId) == null
        val local = sessionDb?.findSessionById(info.groupId)
        if (local != null) {
            info.disturbStatus = local.disturbStatus
            info.top = local.top
        }
        val lastMsgInfo = lastMsgDb?.findSessionMsgInfoBySessionId(info.groupId)
        info.sessionMsgInfo = lastMsgInfo
        sessionDb?.insertOrChangeSession(info)
        return Pair(if (exists) PAYLOAD_ADD else PAYLOAD_CHANGED, info)
    }

    private fun clearGroupBadge(groupId: Long): Pair<String, Any?>? {
        return IMHelper.withDb {
            val last = it?.sessionMsgDao()?.findSessionMsgInfoBySessionId(groupId)
            last?.ownerMsgId = null
            last?.ownerReplyMsgId = null
            last?.replyMeQuesMsgId = null
            last?.replyMeMsgId = null
            last?.msgNum = 0
            last
        }?.let {
            onDealSessionLastMsgInfo(it)
        }
    }

    private fun onDealSessionLastMsgInfo(info: SessionLastMsgInfo): Pair<String, Any?> {
        val sessionDb = context?.let { DbHelper.get(it)?.db?.sessionDao() }
        val lastMsgDb = context?.let { DbHelper.get(it)?.db?.sessionMsgDao() }
        val sessionInfo = sessionDb?.findSessionById(info.groupId)
        val exists = sessionInfo != null
        lastMsgDb?.insertOrUpdateSessionMsgInfo(info)
        sessionInfo?.sessionMsgInfo = info
        var allDots = sessionDb?.allSessions?.sumOf { it.questionNum } ?: 0
        allDots += lastMsgDb?.findAll()?.sumOf { it.msgNum } ?: 0
        super.onMsgPatch(MessageTotalDots(allDots), "", false, null, false) {}
        return Pair(if (!exists) PAYLOAD_ADD else PAYLOAD_CHANGED, sessionInfo)
    }

    private fun notifyAllSession(callId: String?) {
        val sessions = context?.let { DbHelper.get(it)?.db?.sessionDao() }?.allSessions
        val lastMsgDb = context?.let { DbHelper.get(it)?.db?.sessionMsgDao() }
        sessions?.forEach {
            it.sessionMsgInfo = lastMsgDb?.findSessionMsgInfoBySessionId(it.groupId)
        }
        val isFirst = SPHelper[Fetcher.SP_FETCH_SESSIONS_TS, 0L] ?: 0L <= 0
        IMHelper.postToUiObservers(FetchSessionResult(true, isFirst, sessions.isNullOrEmpty())) {}
        super.onMsgPatch(sessions, callId, true, null, false) {}
    }

    override fun onRouteCall(callId: String?, data: Any?) {
        if (callId == Constance.CALL_ID_START_LISTEN_SESSION) {
            notifyAllSession(callId)
        }
    }

    override fun progressUpdate(progress: Int, callId: String) {
        Log.e("------ ", "sending progress change , callId = $callId ===> $progress")
    }
}