package com.zj.ccIm.core.impl

import android.app.Application
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
import com.zj.ccIm.core.catching
import com.zj.ccIm.core.fecher.MessageFetcher
import com.zj.ccIm.error.StreamFinishException
import com.zj.ccIm.logger.ImLogs
import io.grpc.stub.StreamObserver
import io.reactivex.schedulers.Schedulers

internal open class ServerHubImpl : ServerImplGrpc(), LoggerInterface {

    private var subscribeTopics = mutableListOf<String>()
    private var topicStreamObserver: StreamObserver<ListenTopicReq>? = null
    private var messageStreamObserver: StreamObserver<ImMessageReq>? = null

    override fun init(context: Application?) {
        super.init(context)
        BaseApi.setLoggerInterface(IMRecordSizeApi::class.java, this)
    }

    override fun onConnection(connectId: String) {
        catching {
            topicStreamObserver?.onCompleted()
        }
        catching {
            messageStreamObserver?.onCompleted()
        }
        ImLogs.recordLogsInFile("on connecting...", "start connect to server with id : $connectId")
        messageStreamObserver = null
        topicStreamObserver = null
        receiveMessage(connectId)
        receiveTopic(connectId)
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
                    } ?: receiveTopic(currentConnectId)
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
                            postReceivedMessage(callId, it.data, isSpecialData = true, size = 0)
                            postReceivedMessage(Constance.CALL_ID_GET_OFFLINE_MESSAGES_SUCCESS, it.rq, isSpecialData = true, 0)
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
            resp?.channelKey = d.key
            val isOk = isSuccess && resp != null && !resp.black && !resp.forbiddenSpeak
            if (!isOk) {
                resp = setErrorMsgResult(resp, d, (a as? ImApi.EH.HttpErrorBody)?.code ?: 0)
            }
            MessageFetcher.dealMsgExtendsContent(resp).forEach { i ->
                callBack.result(isOk, i, d.autoRetryResend, throwable, a)
            }
        }
    }

    /**
     * Register the receiving channel of the corresponding message channel.
     * @param d ProtocolBuf request serialization object
     * @see [ChannelRegisterInfo]
     * */
    private fun updateMsgReceiver(d: ChannelRegisterInfo, join: Boolean) {
        val callId = if (join) Constance.CALL_ID_REGISTERED_CHAT + d.key else Constance.CALL_ID_LEAVE_FROM_CHAT_ROOM + d.key
        MessageFetcher.cancelFetchOfflineMessage(d.key)
        messageStreamObserver?.let {
            try {
                val data = ImMessageReq.newBuilder()
                data.groupId = d.groupId
                data.ownerId = d.ownerId?.toLong() ?: 0
                data.targetUserId = d.targetUserid?.toLong() ?: 0
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
     * Use Grpc to create a session for monitoring Topic information for the connection???@see [ListenTopicReply]
     * */
    private fun receiveTopic(connectId: String) {
        topicStreamObserver = withChannel {
            it?.listenTopicData(object : CusObserver<ListenTopicReply>("topic", connectId, true) {
                override fun onResult(isOk: Boolean, data: ListenTopicReply?, t: Throwable?) {
                    if (isOk && data != null) {
                        ImLogs.recordLogsInFile("server hub event ", "onTopic ==> observer = $this , topic = ${data.topic} \n  content = ${data.data}")
                        when (data.topic) {
                            Constance.TOPIC_CONN_SUCCESS -> {
                                postOnConnected()
                                if (subscribeTopics.isNotEmpty()) {
                                    registerTopicListener()
                                }
                            }
                            Constance.TOPIC_KICK_OUT -> {
                                postReceivedMessage(data.topic, data.data, isSpecialData = true, 0)
                            }
                            else -> {
                                val size = data.serializedSize.toLong()
                                postReceivedMessage(data.topic, data.data, isSpecialData = false, size)
                            }
                        }
                    } else {
                        if (t is StreamFinishException) topicStreamObserver = null
                        onParseError(t)
                    }
                }
            })
        }
    }

    private fun receiveMessage(connectId: String) {
        messageStreamObserver = withChannel {
            it?.onlineImMessage(object : CusObserver<ImMessageReply>("message", connectId, true) {
                override fun onResult(isOk: Boolean, data: ImMessageReply?, t: Throwable?) {
                    if (isOk && data != null) {
                        ImLogs.recordLogsInFile("server hub event ", "onMessage ==> observer =  $this: type = ${data.type} seq = ${data.reqContext?.seq}")
                        when (data.type) {
                            1 -> {
                                val d = data.reqContext
                                val callId = d.seq
                                postReceivedMessage(callId, d, isSpecialData = true, data.reqContext.serializedSize.toLong())
                            }
                            0 -> {
                                val callId = data.imMessage.clientMsgId
                                val key = ChannelRegisterInfo.createKey(data.reqContext.channel, data.reqContext.groupId, data.reqContext.ownerId, data.reqContext.targetUserId)
                                MessageFetcher.dealMessageExtContent(data.imMessage, key).forEach { d ->
                                    postReceivedMessage(callId, d, isSpecialData = true, data.serializedSize.toLong())
                                }
                            }
                        }
                    } else {
                        if (t is StreamFinishException) messageStreamObserver = null
                        onParseError(t)
                    }
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
            this.coinsNum = d.coinsNum
            this.published = d.public
            this.channelKey = d.key
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