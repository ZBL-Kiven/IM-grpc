package com.zj.imtest.core.impl

import android.util.Log
import com.zj.database.DbHelper
import com.zj.database.entity.SendMessageRespEn
import com.zj.database.entity.SessionInfoEntity
import com.zj.im.chat.enums.SendMsgState
import com.zj.im.chat.hub.ClientHub
import com.zj.imtest.core.Constance

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
                val deal = dealWithDb(cls, null, it, callId, isSpecialData, sendingState, isResent)
                d = if (deal.second.isNullOrEmpty()) d else deal.second
            }
        } else {
            val deal = dealWithDb(d?.javaClass, d, null, callId, isSpecialData, sendingState, isResent)
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
        when (callId) {
            Constance.CALL_ID_GET_OFFLINE_CHAT_MESSAGES, Constance.CALL_ID_GET_OFFLINE_GROUP_MESSAGES -> {

                return false
            }
        }
        return callId?.startsWith(Constance.INTERNAL_CALL_ID_PREFIX) == true
    }

    private fun dealWithDb(cls: Class<*>?, d: Any?, dc: Collection<*>?, callId: String?, isSpecialData: Boolean, sendingState: SendMsgState?, isResent: Boolean): Triple<Any?, List<Any?>?, String?> {
        when (cls) {
            SessionInfoEntity::class.java -> {
                if (!dc.isNullOrEmpty()) {
                    dc.forEach { onDealSessionInfo(it as SessionInfoEntity) }
                }
                if (d != null) onDealSessionInfo(d as SessionInfoEntity) else null
            }
            SendMessageRespEn::class.java -> {
                if (!dc.isNullOrEmpty()) {
                    dc.forEach { onDealMsgSendInfo(it as SendMessageRespEn, callId, sendingState) }
                }
                if (d != null) onDealMsgSendInfo(d as SendMessageRespEn, callId, sendingState) else null

            }
            else -> null
        }
    }

    //todo 完善消息发送状态的同步
    private fun onDealMsgSendInfo(d: SendMessageRespEn, callId: String?, sendingState: SendMsgState?): String? {
        val db = context?.let { DbHelper.get(it)?.db?.messageDao() }
        val localMsg = db?.findMsgByClientId(callId)
        return when (sendingState) {

            //Callback when the operation of the coroutine is completed
            SendMsgState.ON_SEND_BEFORE_END -> {
                PAYLOAD_CHANGED
            }
            SendMsgState.FAIL, SendMsgState.TIME_OUT -> {
                db?.deleteMsgByClientId(d.clientMsgId)
                PAYLOAD_CHANGED
            }
            SendMsgState.SUCCESS -> {
                db?.deleteMsgByClientId(d.clientMsgId)
                PAYLOAD_DELETE
            }
            SendMsgState.SENDING -> {
                if (localMsg != null) {
                    localMsg.sendingState = sendingState.type
                }
                PAYLOAD_CHANGED
            }
        }
    }

    private fun onDealSessionInfo(info: SessionInfoEntity): String? {
        val db = context?.let { DbHelper.get(it)?.db?.sessionDao() }
        val exists = db?.findSessionById(info.groupId) == null
        db?.insertOrChangeSession(info)
        return if (exists) PAYLOAD_ADD else PAYLOAD_CHANGED
    }

    override fun progressUpdate(progress: Int, callId: String) {
        Log.e("------ ", "sending progress ->  callId = $callId   p = $progress")
    }
}