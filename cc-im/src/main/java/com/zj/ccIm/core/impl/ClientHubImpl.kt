package com.zj.ccIm.core.impl

import android.util.Log
import com.zj.database.DbHelper
import com.zj.ccIm.core.bean.SendMessageRespEn
import com.zj.im.chat.enums.SendMsgState
import com.zj.im.chat.hub.ClientHub
import com.zj.ccIm.core.Constance
import com.zj.ccIm.core.IMHelper
import com.zj.ccIm.core.sp.SPHelper
import com.zj.database.entity.SendMessageReqEn
import com.zj.database.entity.*
import com.zj.protocol.grpc.ImMessage
import com.zj.protocol.utl.ProtoBeanUtils
import com.google.gson.Gson
import com.zj.ccIm.core.MsgType
import com.zj.im.chat.poster.log

class ClientHubImpl : ClientHub<Any?>() {

    companion object {
        const val PAYLOAD_ADD = "add"
        const val PAYLOAD_DELETE = "delete"
        const val PAYLOAD_CHANGED = "change"
        private const val SP_LAST_CREATE_TS = "last_create_ts"
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
            Constance.TOPIC_IM_MSG -> {
                val deal = onDealSessionLastMsgInfo(d?.toString())
                payload = deal?.first ?: callId
                d = if (deal?.second == null) d else deal.second
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
            IMHelper.onMsgRegistered()
        }
        if (!b && callId?.startsWith(Constance.CALL_ID_GET_OFFLINE_MESSAGES) == true) {
            IMHelper.resume(Constance.FETCH_OFFLINE_MSG_CODE)
            return true
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
                    val r = onDealMsgSendInfo(d as SendMessageRespEn, callId, sendingState)
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
        if (sendingState != SendMsgState.SENDING) return null
        val msg = MessageInfoEntity()
        msg.groupId = sen.groupId ?: return null
        msg.msgType = sen.msgType
        msg.sendingState = sendingState.type
        msg.clientMsgId = sen.clientMsgId
        msg.sendTime = getLastCreateTs()
        msg.replyMsg = sen.replyMsg
        msg.sender = SenderInfo().apply {
            this.senderId = IMHelper.imConfig.getUserId()
        }
        when (sen.msgType) {
            MsgType.TEXT.type -> {
                msg.textContent = TextContent(sen.content)
            }
            MsgType.IMG.type -> {
                msg.imgContent = ImgContent().apply {
                    this.url = sen.localFilePath
                    this.duration = sen.duration
                    this.width = sen.width
                    this.height = sen.height
                }
            }
            MsgType.AUDIO.type -> {
                msg.audioContent = AudioContent().apply {
                    this.url = sen.localFilePath
                    this.duration = sen.duration
                }
            }
            MsgType.VIDEO.type -> {
                msg.videoContent = VideoContent().apply {
                    this.url = sen.localFilePath
                    this.thumbnail = sen.localFilePath
                    this.duration = sen.duration
                    this.width = sen.width
                    this.height = sen.height
                }
            }
            MsgType.QUESTION.type -> {
                msg.questionContent = QuestionContent().apply {
                    this.diamond = sen.diamondNum ?: 0
                    this.isPublic = sen.public
                    this.textContent = TextContent(sen.content)
                }
            }
        }
        context?.let {
            val db = DbHelper.get(it)?.db?.sendMsgDao()
            db?.insertOrChange(sen)
        }
        return onDealMessages(msg, callId, sendingState)
    }

    private fun getLastCreateTs(): Long {
        var ts = SPHelper[SP_LAST_CREATE_TS, 0L] ?: 0L
        ts += 1
        SPHelper.put(SP_LAST_CREATE_TS, ts)
        return ts
    }

    /**
     * When the message is sent, the state callback event,
     * here is the message that the database already exists in the database and is sent to the UI.
     * */
    private fun onDealMsgSendInfo(d: SendMessageRespEn, callId: String?, sendingState: SendMsgState?): Pair<Any?, String?>? {
        if (sendingState == null) return null
        val msgDb = context?.let { DbHelper.get(it)?.db?.messageDao() }
        val sendDb = context?.let { DbHelper.get(it)?.db?.sendMsgDao() }
        val localMsg = msgDb?.findMsgByClientId(callId)
        localMsg?.sendingState = sendingState.type
        return when (sendingState) {
            SendMsgState.SENDING -> null

            //Callback when the operation of the coroutine is completed
            SendMsgState.ON_SEND_BEFORE_END -> {
                Pair(localMsg, PAYLOAD_CHANGED)
            }
            SendMsgState.FAIL, SendMsgState.TIME_OUT -> {
                if (d.black) {
                    msgDb?.deleteAllBySessionId(d.groupId)
                } else {
                    msgDb?.deleteMsgByClientId(d.clientMsgId)
                }
                if (d.black) sendDb?.deleteAllBySessionId(d.groupId)
                Pair(localMsg, if (d.black) PAYLOAD_DELETE else PAYLOAD_CHANGED)
            }
            SendMsgState.NONE, SendMsgState.SUCCESS -> {
                sendDb?.deleteByCallId(d.clientMsgId)
                msgDb?.deleteMsgByClientId(d.clientMsgId)
                Pair(localMsg, PAYLOAD_CHANGED)
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
        sessionDb?.insertOrChangeSession(info)
        val lastMsgInfo = lastMsgDb?.findSessionMsgInfoBySessionId(info.groupId)
        info.sessionMsgInfo = lastMsgInfo
        return Pair(if (exists) PAYLOAD_ADD else PAYLOAD_CHANGED, info)
    }

    private fun onDealSessionLastMsgInfo(d: String?): Pair<String, Any?>? {
        val info = try {
            Gson().fromJson(d, SessionLastMsgInfo::class.java)
        } catch (e: Exception) {
            log("parse session error with : ${e.message} \n data = $d"); return null
        }
        val sessionDb = context?.let { DbHelper.get(it)?.db?.sessionDao() }
        val lastMsgDb = context?.let { DbHelper.get(it)?.db?.sessionMsgDao() }
        val exists = sessionDb?.findSessionById(info.groupId) == null
        lastMsgDb?.insertOrUpdateSessionMsgInfo(info)
        val sessionInfo = sessionDb?.findSessionById(info.groupId)
        sessionInfo?.sessionMsgInfo = info
        return Pair(if (exists) PAYLOAD_ADD else PAYLOAD_CHANGED, sessionInfo)
    }

    override fun progressUpdate(progress: Int, callId: String) {
        Log.e("------ ", "sending progress change , callId = $callId ===> $progress")
    }

    //                    val isSelfMsg = msg.senderId == Constance.getUserId()
    //                    val isReplyMeMsg = msg.replyMsg?.senderId == Constance.getUserId()
    //                    val isFromOwner = msg.ownerId == msg.senderId && !isSelfMsg
    //
    //                    //Whether it is a common message, that is, the type that is saved and displayed in the main state, and not saved in the guest state
    //                    val isNormalMsg = !isFromOwner && !isReplyMeMsg
    //
    //                    //Is need to save and display what the V said as the last message
    //                    val isOwnerPublic = (isFromOwner && msg.replyId == null) || (isFromOwner && msg.replyMsg?.let {
    //                        it.questionContent?.isPublic == true || it.senderId == Constance.getUserId()
    //                    } == true)
    //
    //                    val nextReplaceId = when {
    //                        isNormalMsg && msg.ownerId == Constance.getUserId() -> Constance.PRIMARY_LOCAL_ID_NORMAL
    //                        isOwnerPublic -> Constance.PRIMARY_LOCAL_ID_V
    //                        else -> ""
    //                    }
    //                    if (nextReplaceId.isNotEmpty()) {
    //                        msg.saveInfoId = nextReplaceId //mark as a save info id
    //                        db?.deleteMsgBySaveInfoId(nextReplaceId)
    //                        db?.insertOrChangeMessage(msg)
    //                    }
}