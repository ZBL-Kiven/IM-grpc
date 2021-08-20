package com.zj.ccIm.core.impl

import android.util.Log
import com.zj.ccIm.core.bean.SendMessageReqEn
import com.zj.im.chat.interfaces.SendingCallBack
import com.zj.ccIm.core.Constance
import com.zj.ccIm.core.api.ImApi
import com.zj.ccIm.core.bean.SendMessageRespEn
import com.zj.protocol.grpc.*
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.grpc.stub.StreamObserver
import retrofit2.HttpException
import java.lang.IllegalArgumentException
import java.lang.IllegalStateException

class ServerHubImpl : ServerImplGrpc() {

    private var subscribeTopics = mutableListOf<String>()
    private var requestStreamObserver: StreamObserver<ListenTopicReq>? = null

    override fun send(params: Any?, callId: String, callBack: SendingCallBack<Any?>): Long {
        var sendSize = 0L
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

            Constance.CALL_ID_GET_OFFLINE_CHAT_MESSAGES, Constance.CALL_ID_GET_OFFLINE_GROUP_MESSAGES -> {
                getOfflineMessage(callId, params)
            }
            else -> null
        }
        if (called == null) {
            sendSize = sendMsg(params, callId, callBack)
        } else {
            callBack.result(true, null, null)
        }
        return sendSize
    }

    override fun onConnection() {
        receiveTopic()
    }

    override fun onConnected(connectType: Int) {
        when (connectType) {
            Constance.CONNECT_TYPE_TOPIC -> {
                super.onConnected(connectType)

                //subscribe current topics
                if (subscribeTopics.isNotEmpty()) {
                    registerTopicListener()
                }
            }
            Constance.CONNECT_TYPE_MESSAGE -> {
                postReceivedMessage(Constance.CALL_ID_REGISTERED_CHAT, null, true, 0)
            }
        }
    }

    /**
     * send msg to server
     * */
    private fun sendMsg(d: Any?, callId: String, callBack: SendingCallBack<Any?>): Long {
        if (d !is SendMessageReqEn) {
            callBack.result(false, null, IllegalArgumentException("the send msg type is not supported except SendMessageReqEn.class"))
            return 0
        }
        if (d.clientMsgId != callId) d.clientMsgId = callId

        ImApi.getSenderApi().request({ it.sendMsg(d) }) { isSuccess: Boolean, data: SendMessageRespEn?, throwable: HttpException? ->
            var isOk = isSuccess
            if (isSuccess && data != null) {
                isOk = data.success && !data.black
            }
            callBack.result(isOk, data, throwable)
        }
        return d.uploadDataTotalByte + d.content.toString().toByteArray().size
    }

    /**
     * Register the receiving channel of the corresponding message channel.
     * @param d ProtocolBuf request serialization object
     * @see [GetImMessageReq]
     * */
    private fun registerMsgReceiver(d: Any?) {
        val req = (d as? GetImMessageReq) ?: return
        withChannel {
            it.getImMessage(req, object : CusObserver<ImMessage>() {
                override fun onResult(isOk: Boolean, data: ImMessage?, t: Throwable?) {
                    if (isOk && data != null) {
                        val size = data.serializedSize * 1L
                        postReceivedMessage(data.clientMsgId, data, false, size)
                    } else onParseError(t)
                }
            })
        }
    }

    /**
     * Use Grpc to create a session for monitoring Topic information for the connection，@see [ListenTopicReply]
     * */
    private fun receiveTopic() {
        print("on connecting", "trying to receive topic")
        requestStreamObserver = withChannel(true) {
            it.listenTopicData(object : CusObserver<ListenTopicReply>() {
                override fun onResult(isOk: Boolean, data: ListenTopicReply?, t: Throwable?) {
                    print("on connecting result", "topic = ${data?.topic}")
                    if (isOk && data != null) {
                        when (data.topic) {
                            Constance.TOPIC_CONN_SUCCESS -> {
                                onConnected(Constance.CONNECT_TYPE_TOPIC)
                            }
                            Constance.TOPIC_MSG_REGISTRATION -> {
                                onConnected(Constance.CONNECT_TYPE_MESSAGE)
                            }
                            else -> {
                                val size = data.serializedSize * 1L
                                postReceivedMessage(data.topic, data.data, false, size)
                            }
                        }
                    } else onParseError(t)
                }
            })
        }
    }

    /**
     * Get the latest news of the conversation during the offline period, here is a distinction between group and single chat
     * */
    private fun getOfflineMessage(type: String, d: Any?) {
        val rq = (d as? GetImHistoryMsgReq) ?: return
        val observer = object : CusObserver<BatchMsg>() {
            override fun onResult(isOk: Boolean, data: BatchMsg?, t: Throwable?) {
                if (isOk && data != null) {
                    val size = data.serializedSize * 1L
                    postReceivedMessage(type, data.imMessageList, true, size)
                } else onParseError(t)
            }
        }
        withChannel {
            if (type == Constance.CALL_ID_GET_OFFLINE_GROUP_MESSAGES) {
                it.getGroupHistoryMessage(rq, observer)
            } else {
                it.getChatHistoryMessage(rq, observer)
            }
        }
    }

    /**
     * Listen for Topic type messages, and update subscriptions by passing in new topic types.
     * [subscribeTopics] all types that need to be subscribed, such as [Constance.TOPIC_CC_USER]
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
        withChannel(false) {
            it.leaveImGroup(rq, object : StreamObserver<LeaveImGroupReply> {
                override fun onNext(value: LeaveImGroupReply?) {
                    Log.e("------ ", "success !! ${value?.success}")
                }

                override fun onError(t: Throwable?) {
                    onParseError(t)
                }

                override fun onCompleted() {
                    Log.e("------ ", " onCompleted ")
                }
            })
        }
    }

    override fun onParseError(t: Throwable?) {
        print("server.onParseError", "${t?.message}")
        (t as? StatusRuntimeException)?.let {
            when (it.status.code) {
                Status.Code.CANCELLED -> {
                    print("------ ", "onCanceled with message : ${t.message}")
                }
                else -> {
                    super.onParseError(IllegalStateException("server error ${it.status.code.name} ; code = ${it.status.code.value()} "))
                }
            }
        } ?: super.onParseError(t)
    }
}