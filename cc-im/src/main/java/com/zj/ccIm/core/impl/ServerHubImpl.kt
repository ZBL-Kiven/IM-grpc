package com.zj.ccIm.core.impl

import android.app.Application
import com.zj.ccIm.core.Constance
import com.zj.ccIm.core.api.ImApi
import com.zj.ccIm.core.bean.SendMessageRespEn
import com.zj.database.entity.SendMessageReqEn
import com.zj.im.chat.interfaces.SendingCallBack
import com.zj.protocol.grpc.*
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.grpc.stub.StreamObserver
import retrofit2.HttpException
import com.zj.api.BaseApi
import com.zj.api.base.BaseRetrofit
import com.zj.api.utils.LoggerInterface
import com.zj.ccIm.core.api.IMRecordSizeApi
import com.zj.ccIm.core.api.ImApi.EH.SENSITIVE_WORDS
import com.zj.ccIm.core.bean.LastMsgReqBean
import com.zj.ccIm.logger.ImLogs

class ServerHubImpl : ServerImplGrpc(), LoggerInterface {

    private var subscribeTopics = mutableListOf<String>()
    private var getMsgRequestCompo: BaseRetrofit.RequestCompo? = null
    private var requestStreamObserver: StreamObserver<ListenTopicReq>? = null

    override fun init(context: Application?) {
        super.init(context)
        BaseApi.setLoggerInterface(IMRecordSizeApi::class.java, this)
    }

    override fun send(params: Any?, callId: String, callBack: SendingCallBack<Any?>): Long {
        val called = when (callId) {
            Constance.CALL_ID_SUBSCRIBE_REMOVE_TOPIC, Constance.CALL_ID_SUBSCRIBE_NEW_TOPIC -> {
                params?.toString()?.let {
                    if (callId == Constance.CALL_ID_SUBSCRIBE_NEW_TOPIC) subscribeTopics.add(it) else subscribeTopics.remove(it)
                    requestStreamObserver?.let {
                        registerTopicListener()
                    } ?: receiveTopic()
                }
            }
            Constance.CALL_ID_LEAVE_CHAT_ROOM -> {
                leaveChatRoom(params)
            }

            Constance.CALL_ID_REGISTER_CHAT -> {
                registerMsgReceiver(params)
            }
            else -> null
        }
        if (called == null) {
            sendMsg(params, callId, callBack)
        } else {
            callBack.result(true, null, null)
        }
        return 0 //send by http , so there is not data size to parse
    }

    override fun onRouteCall(callId: String?, data: Any?) {
        when (callId) {
            Constance.CALL_ID_GET_OFFLINE_CHAT_MESSAGES -> {
                if (isConnected()) getOfflineMessage(callId, data)
            }
        }
    }

    override fun onConnection() {
        receiveTopic()
    }

    /**
     * send msg to server
     * */
    private fun sendMsg(d: Any?, callId: String, callBack: SendingCallBack<Any?>) {
        if (d !is SendMessageReqEn) {
            callBack.result(false, null, IllegalArgumentException("the send msg type is not supported except SendMessageReqEn.class"))
            return
        }
        if (d.clientMsgId != callId) d.clientMsgId = callId
        ImApi.getRecordApi().request({ it.sendMsg(d) }) { isSuccess: Boolean, data: SendMessageRespEn?, throwable: HttpException?, a ->
            var resp = data
            val isOk = isSuccess && resp != null && !resp.black
            if (!isOk) {
                when ((a as? ImApi.EH.HttpErrorBody)?.code) {
                    SENSITIVE_WORDS -> {
                        if (resp == null) resp = SendMessageRespEn().apply {
                            this.clientMsgId = d.clientMsgId
                            this.msgStatus = 1
                            this.groupId = d.groupId
                            this.diamondNum = d.diamondNum
                            this.published = d.public
                        } else {
                            resp.msgStatus = 1
                        }
                    }
                }
            }
            callBack.result(isOk, resp, throwable)
        }
    }

    /**
     * Register the receiving channel of the corresponding message channel.
     * @param d ProtocolBuf request serialization object
     * @see [GetImMessageReq]
     * */
    private fun registerMsgReceiver(d: Any?) {
        val req = (d as? GetImMessageReq) ?: return
        ImLogs.requireToPrintInFile("server hub event ", "call on register msg receiver with ${d.groupId}")
        withChannel {
            it.getImMessage(req, object : CusObserver<ImMessage>() {
                override fun onResult(isOk: Boolean, data: ImMessage?, t: Throwable?) {
                    ImLogs.i("server hub event ", "new Msg in group ${data?.groupId} , isOk = $isOk ,msgClientId = ${data?.clientMsgId} , msgTextInfo ==> ${data?.textContent?.text}")
                    if (isOk && data != null) {
                        ImLogs.requireToPrintInFile("server hub event ", "new Msg [${data.clientMsgId}] received")
                        val size = data.serializedSize * 1L
                        postReceivedMessage(data.clientMsgId, data, false, size)
                    } else onParseError(t, false)
                }
            })
        }
    }

    /**
     * Use Grpc to create a session for monitoring Topic information for the connection，@see [ListenTopicReply]
     * */
    private fun receiveTopic() {
        ImLogs.requireToPrintInFile("on connecting", "trying to receive topic")
        requestStreamObserver = withChannel(true) {
            it.listenTopicData(object : CusObserver<ListenTopicReply>() {
                override fun onResult(isOk: Boolean, data: ListenTopicReply?, t: Throwable?) {
                    onDealTopicReceived(isOk, data, t)
                }
            })
        }
    }

    private fun onDealTopicReceived(isOk: Boolean, data: ListenTopicReply?, t: Throwable?) {
        ImLogs.requireToPrintInFile("server hub event ", "topic = ${data?.topic} \n  content = ${data?.data}")
        if (isOk && data != null) {
            when (data.topic) {
                Constance.TOPIC_CONN_SUCCESS -> {
                    postOnConnected()
                    if (subscribeTopics.isNotEmpty()) {
                        registerTopicListener()
                    }
                }
                Constance.TOPIC_MSG_REGISTRATION -> {
                    postReceivedMessage(Constance.CALL_ID_REGISTERED_CHAT, data.data, true, 0)
                }
                else -> {
                    val size = data.serializedSize * 1L
                    postReceivedMessage(data.topic, data.data, false, size)
                }
            }
        } else onParseError(t, false)
    }

    /**
     * Get the latest news of the conversation during the offline period, here is a distinction between group and single chat
     * */
    private fun getOfflineMessage(callId: String, d: Any?) {
        val rq = (d as? LastMsgReqBean) ?: return
        getMsgRequestCompo = ImApi.getMsgList(rq) { isOk, data, t ->
            ImLogs.d("server hub event ", "get offline msg for type [$callId] -> ${if (isOk) "success" else "failed"} with ${d.groupId} ${if (!isOk) ", error case: ${t?.message}" else ""}")
            if (isOk && data != null) {
                postReceivedMessage(callId, data, true, 0)
                postReceivedMessage(Constance.CALL_ID_GET_OFFLINE_MESSAGES_SUCCESS, rq, true, 0)
            } else onParseError(t, false)
        }
    }

    /**
     * Listen for Topic type messages, and update subscriptions by passing in new topic types.
     * [subscribeTopics] all types that need to be subscribed
     * */
    private fun registerTopicListener() {
        requestStreamObserver?.let {
            val topics = subscribeTopics
            try {
                val topic = ListenTopicReq.newBuilder()
                val method = if (topics.isNullOrEmpty()) ListenTopicReq.Method.UnSubscribe else ListenTopicReq.Method.Subscribe
                topics.forEach { t -> topic.addTopic(t) }
                topic.method = method
                it.onNext(topic.build())
            } catch (e: Exception) {
                it.onError(e)
            }
        }
    }

    /**
     * When you no longer want to receive a message from a chat room,
     * trigger this message through CallId and exit this type of channel monitoring.
     * @param d The ID protocolBuf builder of the group to leave
     * */
    private fun leaveChatRoom(d: Any?) {
        val rq = (d as? LeaveImGroupReq) ?: return
        getMsgRequestCompo?.cancel()
        getMsgRequestCompo = null
        ImLogs.requireToPrintInFile("server hub event ", "leave from receiver with ${d.groupId}")
        withChannel(false) {
            it.leaveImGroup(rq, object : CusObserver<LeaveImGroupReply>() {
                override fun onResult(isOk: Boolean, data: LeaveImGroupReply?, t: Throwable?) {
                    if (!isOk) t?.let { onParseError(t, false) }
                }
            })
        }
    }

    override fun onParseError(t: Throwable?, deadly: Boolean) {
        ImLogs.requireToPrintInFile("server.onParseError", "${t?.message}")
        (t as? StatusRuntimeException)?.let {
            when (it.status.code) {
                Status.Code.CANCELLED -> {
                    ImLogs.requireToPrintInFile("------ ", "onCanceled with message : ${t.message}")
                }
                else -> {
                    super.onParseError(IllegalStateException("server error ${it.status.code.name} ; code = ${it.status.code.value()} "), deadly)
                }
            }
        } ?: super.onParseError(t, deadly)
    }

    /**
     * Any traffic used by HTTP requests initiated via [IMRecordSizeApi] will pass through here.
     * */
    override fun onSizeParsed(fromCls: String, isSend: Boolean, size: Long) {
        if (fromCls != IMRecordSizeApi::class.java.simpleName) return
        ImLogs.d("on http data streaming", "from $fromCls API : ${if (isSend) "send --> " else "received <-- "} $size - bytes content")
        if (isSend) recordOtherSendNetworkDataSize(size) else recordOtherReceivedNetworkDataSize(size)
    }
}