package com.zj.imtest.core.impl

import android.util.Log
import com.zj.database.DbHelper
import com.zj.database.entity.MessageInfoEntity
import com.zj.imtest.core.bean.SendMessageRespEn
import com.zj.database.entity.SessionInfoEntity
import com.zj.im.chat.enums.SendMsgState
import com.zj.im.chat.hub.ClientHub
import com.zj.imtest.core.Constance
import com.zj.protocol.grpc.ImMessage
import com.zj.protocol.utl.ProtoBeanUtils

class ClientHubImpl : ClientHub<Any?>() {

    companion object {
        const val PAYLOAD_ADD = "add"
        const val PAYLOAD_DELETE = "delete"
        const val PAYLOAD_CHANGED = "change"
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
        if (isInterruptData(callId)) {
            onFinish();return
        }
        var d = data
        var payload: String? = callId
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
        super.onMsgPatch(d, payload, isSpecialData, sendingState, isResent, onFinish)
    }

    /**
     * This is used to intercept content that you don't want to be pushed to UI observers
     * @return true is no longer push
     * */
    private fun isInterruptData(callId: String?): Boolean {
        if (callId?.startsWith(Constance.CALL_ID_GET_OFFLINE_MESSAGES) == true) {

            //todo open the msg queue?
            return false
        }
        return callId?.startsWith(Constance.INTERNAL_CALL_ID_PREFIX) == true
    }

    private fun dealWithDb(cls: Class<*>?, d: Any?, dc: Collection<*>?, callId: String?, sendingState: SendMsgState?): Triple<Any?, List<Any?>?, String?> {
        var first: Any? = null
        val second = arrayListOf<Any?>()
        var pl: String? = null
        when (cls) {
            SessionInfoEntity::class.java -> {
                if (!dc.isNullOrEmpty()) {
                    dc.forEach {
                        onDealSessionInfo(it as SessionInfoEntity)
                    }
                    second.addAll(dc)
                }
                if (d != null) {
                    pl = onDealSessionInfo(d as SessionInfoEntity)
                    first = d
                }
            }
            SendMessageRespEn::class.java -> {
                if (!dc.isNullOrEmpty()) {
                    dc.forEach {
                        val r = onDealMsgSendInfo(it as SendMessageRespEn, callId, sendingState)
                        second.add(r?.first)
                    }
                }
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

    /**
     * When the message is sent, the state callback event,
     * here is the message that the database already exists in the database and is sent to the UI.
     * */
    private fun onDealMsgSendInfo(d: SendMessageRespEn, callId: String?, sendingState: SendMsgState?): Pair<Any?, String?>? {
        if (sendingState == null) return null
        val db = context?.let { DbHelper.get(it)?.db?.messageDao() }
        val localMsg = db?.findMsgByClientId(callId)
        localMsg?.sendingState = sendingState.type
        return when (sendingState) {
            SendMsgState.SENDING -> null

            //Callback when the operation of the coroutine is completed
            SendMsgState.ON_SEND_BEFORE_END -> {
                Pair(localMsg, PAYLOAD_CHANGED)
            }
            SendMsgState.FAIL, SendMsgState.TIME_OUT -> {
                db?.deleteMsgByClientId(d.clientMsgId)
                Pair(localMsg, if (d.black) PAYLOAD_DELETE else PAYLOAD_CHANGED)
            }
            SendMsgState.NONE, SendMsgState.SUCCESS -> {
                db?.deleteMsgByClientId(d.clientMsgId)
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
            val db = context?.let { DbHelper.get(it)?.db?.messageDao() }
            val localMsg = if (callId.isNullOrEmpty()) null else db?.findMsgByClientId(callId)
            if (localMsg == null && sendingState == SendMsgState.SENDING) {
                db?.insertOrChangeMessage(msg)
            }
            if (localMsg != null && (sendingState == SendMsgState.SUCCESS || sendingState == SendMsgState.NONE)) {
                db?.deleteMsgByClientId(callId)
            }
            msg.sendingState = sendingState?.type ?: SendMsgState.NONE.type
            val pl = if (localMsg == null) PAYLOAD_ADD else PAYLOAD_CHANGED
            return Pair(msg, pl)
        }
        return null
    }

    private fun onDealSessionInfo(info: SessionInfoEntity): String {
        val db = context?.let { DbHelper.get(it)?.db?.sessionDao() }
        val exists = db?.findSessionById(info.groupId) == null
        db?.insertOrChangeSession(info)
        return if (exists) PAYLOAD_ADD else PAYLOAD_CHANGED
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


    override fun progressUpdate(progress: Int, callId: String) {
        Log.e("------ ", "sending progress ->  callId = $callId   p = $progress")
    }
}