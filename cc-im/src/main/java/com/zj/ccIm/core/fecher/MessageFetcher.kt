package com.zj.ccIm.core.fecher

import com.zj.api.base.BaseRetrofit
import com.zj.ccIm.MainLooper
import com.zj.ccIm.core.ExtMsgType
import com.zj.ccIm.core.MessageType
import com.zj.ccIm.core.SystemMsgType
import com.zj.ccIm.core.api.ImApi
import com.zj.ccIm.core.bean.GetMoreMessagesResult
import com.zj.ccIm.core.bean.ChannelRegisterInfo
import com.zj.ccIm.logger.ImLogs
import com.zj.database.entity.MessageInfoEntity
import com.zj.im.chat.enums.SendMsgState
import com.zj.im.utils.cast
import com.zj.protocol.grpc.ImMessage
import com.zj.protocol.utl.ProtoBeanUtils

internal object MessageFetcher {

    private var fetchingRunners = mutableMapOf<String, FetchMsgRunner>()

    /**
     * Get the latest news of the conversation during the offline period, here is a distinction between group and single chat
     * */
    fun getOfflineMessage(callId: String, rq: ChannelRegisterInfo, threadCheck: Boolean, onCalled: (GetMoreMessagesResult) -> Unit) {
        var fetching = fetchingRunners[rq.key]
        if (fetching == null) {
            fetching = FetchMsgRunner(callId, threadCheck, rq)
            fetching.run(rq.key, onCalled)
        } else fetching.updateRequest(rq.key, onCalled)
        fetchingRunners[rq.key] = fetching
    }

    fun cancelFetchOfflineMessage(key: String) {
        fetchingRunners.remove(key)?.cancel()
    }

    private class FetchMsgRunner(private val callId: String, private val threadCheck: Boolean = false, private val rq: ChannelRegisterInfo) {

        private var callIdObservers = mutableMapOf<String, (GetMoreMessagesResult) -> Unit>()
        private var compo: BaseRetrofit.RequestCompo? = null

        fun run(key: String, observer: ((GetMoreMessagesResult) -> Unit)? = null) {
            if (observer != null) callIdObservers[key] = observer
            compo = ImApi.getMsgList(rq) { isOk, data, t, a ->
                ImLogs.d("server hub event ", "get offline msg for type [$callId] -> ${if (isOk) "success" else "failed"} with ${rq.key} ${if (!isOk) ", error case: ${t?.message}" else ""}")
                synchronized(callIdObservers) {
                    callIdObservers.forEach { (k, v) ->
                        val mapped = mutableMapOf<String, List<MessageInfoEntity?>?>()
                        data?.forEach { (k1, v1) ->
                            val mappedLst = arrayListOf<Any?>()
                            v1?.forEach {
                                mappedLst.addAll(dealMessageExtContent(it, k))
                            }
                            mapped[k1] = cast(mappedLst)
                        }
                        val rsp = GetMoreMessagesResult(callId, isOk, mapped, rq, a, t)
                        if (threadCheck) MainLooper.post { v.invoke(rsp) } else v.invoke(rsp)
                    }
                    callIdObservers.clear()
                    fetchingRunners.remove(rq.key)
                }
            }
        }

        fun updateRequest(callId: String, observer: (GetMoreMessagesResult) -> Unit) {
            callIdObservers[callId] = observer
        }

        fun cancel() {
            compo?.cancel()
        }
    }

    fun <T : Any> dealMessageExtContent(d: T?, key: String): List<MessageInfoEntity?> {
        if (d == null) return arrayListOf()
        if (d is Collection<*>) {
            if (d.isEmpty()) return arrayListOf()
            val lst = arrayListOf<MessageInfoEntity?>()
            d.forEach {
                lst.addAll(dealMessageExtContent(it, key))
            }
            return lst
        } else {
            val msg: MessageInfoEntity? = (d as? MessageInfoEntity) ?: (d as? ImMessage)?.let {
                ProtoBeanUtils.toPojoBean(MessageInfoEntity::class.java, d as? ImMessage)
            }
            val result = arrayListOf(msg)
            msg?.channelKey = key
            msg?.messageType = MessageType.MESSAGE.type

            if (msg?.questionContent?.questionStatus == 3) { // refused message
                msg.messageType = MessageType.SYSTEM.type
                msg.systemMsgType = SystemMsgType.REFUSED.type
            }
            msg?.extContent?.let {
                if (it.containsKey(ExtMsgType.EXTENDS_TYPE_RECALL)) {
                    msg.messageType = MessageType.SYSTEM.type
                    msg.systemMsgType = SystemMsgType.RECALLED.type
                }
                if (it.containsKey(ExtMsgType.EXTENDS_TYPE_SENSITIVE_HINT)) {
                    result.add(MessageInfoEntity().apply {
                        this.extContent = msg.extContent
                        this.messageType = MessageType.SYSTEM.type
                        this.systemMsgType = SystemMsgType.SENSITIVE.type
                        this.channelKey = key
                        this.clientMsgId = msg.clientMsgId + ":SENSITIVE"
                        this.groupId = msg.groupId
                        this.replyMsg = msg
                        this.msgId = msg.msgId
                        this.ownerId = msg.ownerId
                        this.sendingState = SendMsgState.NONE.type
                        this.sender = msg.sender
                        this.countryCode = msg.countryCode
                        this.extContent = it
                    })
                    msg.extContent = null
                }
            }
            return result
        }
    }
}