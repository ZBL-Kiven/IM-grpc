package com.zj.imtest.core

import android.app.Application
import android.app.Notification
import android.util.Log
import com.zj.im.chat.core.BaseOption
import com.zj.im.chat.hub.ClientHub
import com.zj.im.chat.hub.ServerHub
import com.zj.im.main.impl.IMInterface
import com.zj.imtest.core.impl.ClientHubImpl
import com.zj.imtest.core.impl.ServerHubImpl
import com.zj.protocol.grpc.GetImMessageReq
import com.zj.protocol.grpc.LeaveImGroupReq

object IMHelper : IMInterface<Any?>() {

    fun init(app: Application) {
        val option = BaseOption.create(app).debug().logsCollectionAble { true }.logsFileName("IM").setLogsMaxRetain(3L * 24 * 60 * 60 * 1000).setNotify(Notification()).build()
        initChat(option)

    }

    override fun getClient(): ClientHub<Any?> {
        return ClientHubImpl()
    }

    override fun getServer(): ServerHub<Any?> {
        return ServerHubImpl()
    }

    override fun onError(e: Throwable) {
        Log.e("------ ", "IM Error case : ${e.message}")
    }

    fun registerMsgObserver(groupId: Long, ownerId: Long) {
        val callId = Constance.CALL_ID_REGISTER_CHAT
        val data = GetImMessageReq.newBuilder()
        data.groupId = groupId
        data.ownerId = ownerId
        IMHelper.send(data.build(), callId, 3000, isSpecialData = false, ignoreConnecting = false, sendBefore = null)
    }

    fun leaveChatRoom(groupId: Long) {
        val callId = Constance.CALL_ID_LEAVE_CHAT_ROOM
        val data = LeaveImGroupReq.newBuilder()
        data.groupId = groupId
        IMHelper.send(data.build(), callId, 3000, isSpecialData = false, ignoreConnecting = false, sendBefore = null)
    }
}