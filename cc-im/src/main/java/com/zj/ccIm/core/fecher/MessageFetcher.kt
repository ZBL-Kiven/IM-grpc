package com.zj.ccIm.core.fecher

import com.zj.api.base.BaseRetrofit
import com.zj.ccIm.core.ExtMsgType
import com.zj.ccIm.core.MsgType
import com.zj.ccIm.core.api.ImApi
import com.zj.ccIm.core.bean.GetMoreMessagesResult
import com.zj.ccIm.core.bean.ChannelRegisterInfo
import com.zj.ccIm.logger.ImLogs
import com.zj.database.entity.MessageInfoEntity
import com.zj.im.chat.enums.SendMsgState
import com.zj.im.utils.cast
import com.zj.protocol.grpc.ImMessage
import com.zj.protocol.utl.ProtoBeanUtils
import java.util.*

internal object MessageFetcher {

    private var fetchingRunners = mutableMapOf<String, FetchMsgRunner>()

    /**
     * Get the latest news of the conversation during the offline period, here is a distinction between group and single chat
     * */
    fun getOfflineMessage(callId: String, rq: ChannelRegisterInfo, onCalled: (GetMoreMessagesResult) -> Unit) {
        var fetching = fetchingRunners[rq.key]
        if (fetching == null) {
            fetching = FetchMsgRunner(callId, rq)
            fetching.run(rq.key, onCalled)
        } else fetching.updateRequest(rq.key, onCalled)
        fetchingRunners[rq.key] = fetching
    }

    fun cancelFetchOfflineMessage(key: String) {
        fetchingRunners.remove(key)?.cancel()
    }

    private class FetchMsgRunner(private val callId: String, private val rq: ChannelRegisterInfo) {

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
                        v.invoke(rsp)
                    }
                    callIdObservers.clear()
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

    fun <T : Any> dealMessageExtContent(d: T?, key: String): List<Any?> {
        if (d == null) return arrayListOf()
        if (d is Collection<*>) {
            if (d.isEmpty()) return arrayListOf()
            val lst = arrayListOf<Any?>()
            d.forEach {
                lst.addAll(dealMessageExtContent(it, key))
            }
            return lst
        } else {
            val msg: MessageInfoEntity? = (d as? MessageInfoEntity) ?: (d as? ImMessage)?.let {
                ProtoBeanUtils.toPojoBean(MessageInfoEntity::class.java, d as? ImMessage)
            }
            val result = arrayListOf<Any?>(msg)
            msg?.channelKey = key
            msg?.originalMessageType = msg?.msgType
            msg?.extContent?.let {
                if (it.containsKey(ExtMsgType.EXTENDS_TYPE_RECALL)) {
                    msg.msgType = MsgType.RECALLED.type
                }
                if (it.containsKey(ExtMsgType.EXTENDS_TYPE_SENSITIVE_HIT)) {
                    result.add(MessageInfoEntity().apply {
                        this.msgType = MsgType.SENSITIVE.type
                        this.channelKey = key
                        this.clientMsgId = UUID.randomUUID().toString()
                        this.groupId = msg.groupId
                        this.replyMsg = msg
                        this.msgId = msg.msgId
                        this.ownerId = msg.ownerId
                        this.sendingState = SendMsgState.NONE.type
                        this.sender = msg.sender
                        this.countryCode = msg.countryCode
                        this.extContent = it
                    })
                }
            }
            return result
        }
    }
}