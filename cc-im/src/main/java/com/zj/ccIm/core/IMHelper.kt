package com.zj.ccIm.core

import android.app.Application
import android.app.Notification
import android.util.Log
import com.zj.ccIm.core.bean.LastMsgReqBean
import com.zj.database.DbHelper
import com.zj.im.chat.core.BaseOption
import com.zj.im.chat.hub.ClientHub
import com.zj.im.chat.hub.ServerHub
import com.zj.im.main.impl.IMInterface
import com.zj.ccIm.core.fecher.Fetcher
import com.zj.ccIm.core.impl.ClientHubImpl
import com.zj.ccIm.core.impl.ServerHubImpl
import com.zj.ccIm.core.sp.SPHelper
import com.zj.protocol.grpc.GetImHistoryMsgReq
import com.zj.protocol.grpc.GetImMessageReq
import com.zj.protocol.grpc.LeaveImGroupReq

@Suppress("MemberVisibilityCanBePrivate", "unused")
object IMHelper : IMInterface<Any?>() {

    private var lastMsgRegister: LastMsgReqBean? = null
    internal lateinit var imConfig: ImConfigIn

    fun init(app: Application, imConfig: ImConfigIn) {
        IMHelper.imConfig = imConfig
        Constance.app = app
        Fetcher.init()
        SPHelper.init("im_sp_main", app)
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

    fun refreshSessions() {
        Fetcher.onFetchSessions(true)
    }

    fun refreshChatMsg() {
        lastMsgRegister?.let { getOfflineChatMsg(it.groupId, it.ownerId) }
    }

    fun refreshGroupMsg() {
        lastMsgRegister?.let { getOfflineGroupMsg(it.groupId, it.ownerId) }
    }

    fun registerMsgObserver(groupId: Long, ownerId: Long) {
        val callId = Constance.CALL_ID_REGISTER_CHAT
        val data = GetImMessageReq.newBuilder()
        data.groupId = groupId
        data.ownerId = ownerId
        this.lastMsgRegister = LastMsgReqBean(groupId, ownerId)
        IMHelper.send(data.build(), callId, Constance.CONNECTION_TIME_OUT, isSpecialData = false, ignoreConnecting = false, sendBefore = null)
    }

    fun leaveChatRoom(groupId: Long) {
        val callId = Constance.CALL_ID_LEAVE_CHAT_ROOM
        val data = LeaveImGroupReq.newBuilder()
        data.groupId = groupId
        this.lastMsgRegister = null
        IMHelper.send(data.build(), callId, Constance.CONNECTION_TIME_OUT, isSpecialData = false, ignoreConnecting = false, sendBefore = null)
    }

    private fun getOfflineChatMsg(groupId: Long, ownerId: Long) {
        val callId = Constance.CALL_ID_GET_OFFLINE_CHAT_MESSAGES
        val data = GetImHistoryMsgReq.newBuilder()
        data.groupId = groupId
        data.ownerId = ownerId
        IMHelper.send(data.build(), callId, Constance.CONNECTION_TIME_OUT, isSpecialData = false, ignoreConnecting = false, sendBefore = null)
    }

    private fun getOfflineGroupMsg(groupId: Long, ownerId: Long) {
        val callId = Constance.CALL_ID_GET_OFFLINE_GROUP_MESSAGES
        val data = GetImHistoryMsgReq.newBuilder()
        data.groupId = groupId
        data.ownerId = ownerId
        IMHelper.send(data.build(), callId, 3000, isSpecialData = false, ignoreConnecting = false, sendBefore = null)
    }

    internal fun tryToRegisterAfterConnected(): Boolean {
        lastMsgRegister?.let {
            registerMsgObserver(it.groupId, it.ownerId)
            return true
        }
        return false
    }

    internal fun postToUiObservers(data: Any?, payload: String? = null, onFinish: () -> Unit) {
        super.postToUi(data, payload, onFinish)
    }

    internal fun onMsgRegistered() {
        refreshChatMsg()
        refreshGroupMsg()
    }

    fun close() {
        lastMsgRegister = null
        Fetcher.cancel()
    }

    /**
     * must call when login out
     * */
    fun loginOut(app: Application) {
        close()
        SPHelper.clear()
        Thread {
            DbHelper.get(app)?.db?.clearAllTables()
        }.start()
    }
}