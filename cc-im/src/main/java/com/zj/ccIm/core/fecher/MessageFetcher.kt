package com.zj.ccIm.core.fecher

import com.google.gson.Gson
import com.zj.api.base.BaseRetrofit
import com.zj.ccIm.MainLooper
import com.zj.ccIm.core.*
import com.zj.ccIm.core.api.ImApi
import com.zj.ccIm.core.bean.GetMoreMessagesResult
import com.zj.ccIm.core.bean.ChannelRegisterInfo
import com.zj.ccIm.logger.ImLogs
import com.zj.database.entity.BaseMessageInfo
import com.zj.database.entity.MessageInfoEntity
import com.zj.im.chat.enums.SendMsgState
import com.zj.im.utils.cast
import com.zj.protocol.grpc.ImMessage
import com.zj.protocol.utl.ProtoBeanUtils
import org.json.JSONObject

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
                        val mappedLst = arrayListOf<Any?>()
                        data?.get(rq.curChannelName)?.forEach {
                            mappedLst.addAll(dealMessageExtContent(it, k))
                        }
                        val rsp = GetMoreMessagesResult(callId, isOk, cast(mappedLst), rq, a, t)
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
            run with@{
                IMHelper.getDb()?.sendMsgDao()?.findByCallId(msg?.clientMsgId)?.let {
                    if (it.key == key) return@with
                }
                msg?.channelKey = key
                msg?.messageType = MessageType.MESSAGE.type
                if (msg?.questionContent?.questionStatus == 3) { // refused message
                    msg.messageType = MessageType.SYSTEM.type
                    msg.systemMsgType = SystemMsgType.REFUSED.type
                }
                return dealMsgExtendsContent(msg)
            }
            return arrayListOf()
        }
    }

    fun <T : BaseMessageInfo?> dealMsgExtendsContent(info: T?): List<Any?> {
        if (info == null) return arrayListOf()
        val lst = mutableListOf<Any?>(info)
        info.extContent?.toMutableMap()?.let {
            if (it.containsKey(ExtMsgType.EXTENDS_TYPE_RECALL)) {
                info.messageType = MessageType.SYSTEM.type
                info.systemMsgType = SystemMsgType.RECALLED.type
            }
            if (it.containsKey(ExtMsgType.EXTENDS_TYPE_SENSITIVE_HINT)) {
                val str = info.extContent?.get(ExtMsgType.EXTENDS_TYPE_SENSITIVE_HINT)
                try {
                    val strJson = JSONObject(str)
                    if (strJson.has("other")) {
                        if (strJson.getBoolean("other")) {
                            return@let
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                val systemInfo = info.copyTo(MessageInfoEntity())
                systemInfo?.messageType = MessageType.SYSTEM.type
                systemInfo?.sendingState = SendMsgState.NONE.type
                systemInfo?.systemMsgType = SystemMsgType.SENSITIVE.type
                systemInfo?.clientMsgId = info.clientMsgId + ":SENSITIVE"
                lst.add(systemInfo)
                it.remove(ExtMsgType.EXTENDS_TYPE_SENSITIVE_HINT)
            }
        }
        return lst
    }


    data class RiskMsg(val other: Boolean?, val riskId: Int?, val multilingual: Map<String, String>?) {}
}