package com.zj.ccIm.core.impl

import android.app.Application
import io.grpc.stub.StreamObserver
import retrofit2.HttpException
import com.zj.api.BaseApi
import com.zj.api.utils.LoggerInterface
import com.zj.database.entity.SendMessageReqEn
import com.zj.im.chat.interfaces.SendingCallBack
import com.zj.protocol.grpc.*
import com.zj.ccIm.core.Constance
import com.zj.ccIm.core.api.ImApi
import com.zj.ccIm.core.bean.SendMessageRespEn
import com.zj.ccIm.core.api.IMRecordSizeApi
import com.zj.ccIm.core.bean.DeleteSessionInfo
import com.zj.ccIm.core.bean.ChannelRegisterInfo
import com.zj.ccIm.core.fecher.MessageFetcher
import com.zj.ccIm.logger.ImLogs
import io.reactivex.schedulers.Schedulers

internal open class ServerHubImpl : ServerImplGrpc(), LoggerInterface {

    private var subscribeTopics = mutableListOf<String>()
    private var topicStreamObserver: StreamObserver<ListenTopicReq>? = null
    private var messageStreamObserver: StreamObserver<ImMessageReq>? = null

    override fun init(context: Application?) {
        super.init(context)
        BaseApi.setLoggerInterface(IMRecordSizeApi::class.java, this)
    }

    override fun onConnection() {
        receiveMessage()
        receiveTopic()
    }

    override fun send(params: Any?, callId: String, callBack: SendingCallBack<Any?>): Long {
        val called = when {
            callId.startsWith(Constance.CALL_ID_REGISTER_CHAT) -> {
                (params as? ChannelRegisterInfo)?.let { d ->
                    updateMsgReceiver(d, true)
                }
            }
            callId.startsWith(Constance.CALL_ID_LEAVE_CHAT_ROOM) -> {
                (params as? ChannelRegisterInfo)?.let { d ->
                    updateMsgReceiver(d, false)
                }
            }
            callId == Constance.CALL_ID_SUBSCRIBE_REMOVE_TOPIC || callId == Constance.CALL_ID_SUBSCRIBE_NEW_TOPIC -> {
                params?.toString()?.let {
                    if (callId == Constance.CALL_ID_SUBSCRIBE_NEW_TOPIC) subscribeTopics.add(it) else subscribeTopics.remove(it)
                    topicStreamObserver?.let {
                        registerTopicListener()
                    } ?: receiveTopic()
                }
            }
            else -> null
        }
        if (called == null) {
            sendMsg(params, callId, callBack)
        } else {
            callBack.result(true, null, false, null, null)
        }
        return 0 //send by http , so there is not data size to parse
    }

    override fun onRouteCall(callId: String?, data: Any?) {
        when (callId) {
            Constance.CALL_ID_GET_OFFLINE_CHAT_MESSAGES -> {
                if (isConnected()) {
                    MessageFetcher.getOfflineMessage(callId, data as ChannelRegisterInfo, false) {
                        if (it.isOK && it.data != null) {
                            postReceivedMessage(callId, it.data, true, 0)
                            postReceivedMessage(Constance.CALL_ID_GET_OFFLINE_MESSAGES_SUCCESS, it.rq, true, 0)
                        } else onParseError(it.e)
                    }
                }
            }
            Constance.CALL_ID_DELETE_SESSION -> {
                deleteSession(data as DeleteSessionInfo)
            }
        }
    }

    /**
     * send msg to server
     * */
    private fun sendMsg(d: Any?, callId: String, callBack: SendingCallBack<Any?>) {
        if (d !is SendMessageReqEn) {
            callBack.result(false, null, false, IllegalArgumentException("the send msg type is not supported except SendMessageReqEn.class"), null)
            return
        }
        if (d.clientMsgId != callId) d.clientMsgId = callId
        ImApi.getRecordApi().request({ it.sendMsg(d) }, Schedulers.io(), Schedulers.io()) { isSuccess: Boolean, data: SendMessageRespEn?, throwable: HttpException?, a ->
            var resp = data
            val isOk = isSuccess && resp != null && !resp.black
            if (!isOk) {
                resp = setErrorMsgResult(resp, d, (a as? ImApi.EH.HttpErrorBody)?.code ?: 0)
            }
            callBack.result(isOk, resp, d.autoRetryResend, throwable, a)
        }
    }

    /**
     * Register the receiving channel of the corresponding message channel.
     * @param d ProtocolBuf request serialization object
     * @see [ChannelRegisterInfo]
     * */
    private fun updateMsgReceiver(d: ChannelRegisterInfo, join: Boolean) {
        val callId = if (join) Constance.CALL_ID_REGISTERED_CHAT + d.key else Constance.CALL_ID_LEAVE_CHAT_ROOM + d.key
        MessageFetcher.cancelFetchOfflineMessage(d.key)
        messageStreamObserver?.let {
            try {
                val data = ImMessageReq.newBuilder()
                data.groupId = d.groupId
                data.ownerId = d.ownerId?.toLong() ?: -1
                data.targetUserId = d.targetUserid?.toLong() ?: -1
                data.channel = d.mChannel.serializeName
                data.seq = callId
                data.op = if (join) ImMessageReq.Op.JOIN else ImMessageReq.Op.LEAVE
                it.onNext(data.build())
                ImLogs.recordLogsInFile("server hub event ", "call ${if (join) "add" else "remove"} msg receiver to $it with ${d.key}")
            } catch (e: Exception) {
                it.onError(e)
            }
        }
    }

    /**
     * Use Grpc to create a session for monitoring Topic information for the connection，@see [ListenTopicReply]
     * */
    private fun receiveTopic() {
        runCatching {
            topicStreamObserver?.onCompleted()
        }
        ImLogs.recordLogsInFile("on connecting", "trying to receive topic")
        topicStreamObserver = withChannel {
            it.listenTopicData(object : CusObserver<ListenTopicReply>(true) {
                override fun onResult(isOk: Boolean, data: ListenTopicReply?, t: Throwable?) {
                    ImLogs.recordLogsInFile("server hub event ", "topic = ${data?.topic} \n  content = ${data?.data}")
                    if (isOk && data != null) {
                        when (data.topic) {
                            Constance.TOPIC_CONN_SUCCESS -> {
                                postOnConnected()
                                if (subscribeTopics.isNotEmpty()) {
                                    registerTopicListener()
                                }
                            }
                            else -> {
                                val size = data.serializedSize.toLong()
                                postReceivedMessage(data.topic, data.data, false, size)
                            }
                        }
                    } else onParseError(t)
                }
            })
        }
    }

    private fun receiveMessage() {
        runCatching {
            messageStreamObserver?.onCompleted()
        }
        ImLogs.recordLogsInFile("on connecting", "trying to receive messages")
        messageStreamObserver = withChannel {
            it.onlineImMessage(object : CusObserver<ImMessageReply>(true) {
                override fun onResult(isOk: Boolean, data: ImMessageReply?, t: Throwable?) {
                    if (isOk && data != null) {
                        ImLogs.recordLogsInFile("server hub event ", "on server message arrived : type = ${data.type} seq = ${data.reqContext?.seq}")
                        when (data.type) {
                            1 -> {
                                val d = data.reqContext
                                val callId = d.seq
                                postReceivedMessage(callId, d, true, data.reqContext.serializedSize.toLong())
                            }
                            0 -> {
                                val key = ChannelRegisterInfo.createKey(data.reqContext.channel, data.reqContext.groupId, data.reqContext.ownerId, data.reqContext.targetUserId)
                                MessageFetcher.dealMessageExtContent(data.imMessage, key).forEach { d ->
                                    postReceivedMessage(key, d, true, data.serializedSize.toLong())
                                }
                            }
                        }
                    } else onParseError(t)
                }
            })
        }
    }

    /**
     * Listen for Topic type messages, and update subscriptions by passing in new topic types.
     * [subscribeTopics] all types that need to be subscribed
     * */
    private fun registerTopicListener() {
        topicStreamObserver?.let {
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

    private fun deleteSession(data: DeleteSessionInfo) {
        ImApi.getFunctionApi().call({ it.deleteSession(data) })
    }

    private fun setErrorMsgResult(r: SendMessageRespEn?, d: SendMessageReqEn, status: Int): SendMessageRespEn {
        var resp = r
        if (resp == null) resp = SendMessageRespEn().apply {
            this.clientMsgId = d.clientMsgId
            this.msgStatus = status
            this.groupId = d.groupId
            this.diamondNum = d.diamondNum
            this.published = d.public
        } else {
            resp.msgStatus = status
        }
        return resp
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