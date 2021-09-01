package com.zj.ccIm.core

import android.app.Application
import android.app.Notification
import android.util.Log
import com.zj.ccIm.core.api.ImApi
import com.zj.ccIm.core.bean.LastMsgReqBean
import com.zj.database.DbHelper
import com.zj.im.chat.core.BaseOption
import com.zj.im.chat.hub.ClientHub
import com.zj.im.chat.hub.ServerHub
import com.zj.im.main.impl.IMInterface
import com.zj.ccIm.core.fecher.Fetcher
import com.zj.ccIm.core.impl.ClientHubImpl
import com.zj.ccIm.core.impl.ServerHubImpl
import com.zj.ccIm.core.sender.Sender
import com.zj.ccIm.core.sp.SPHelper
import com.zj.database.IMDb
import com.zj.database.entity.SessionInfoEntity
import com.zj.im.chat.poster.log
import com.zj.protocol.grpc.GetImHistoryMsgReq
import com.zj.protocol.grpc.GetImMessageReq
import com.zj.protocol.grpc.LeaveImGroupReq
import io.reactivex.schedulers.Schedulers
import java.lang.NullPointerException


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

    override fun onNewListenerRegistered(cls: Class<*>) {
        if (cls == SessionInfoEntity::class.java) {
            routeToClient(null, Constance.CALL_ID_START_LISTEN_SESSION)
        }
    }

    fun refreshSessions() {
        Fetcher.onFetchSessions(true)
    }

    fun refreshChatMsg() {
        pause(Constance.FETCH_OFFLINE_MSG_CODE)
        lastMsgRegister?.let { getOfflineChatMsg(it.groupId, it.ownerId) }
    }

    fun refreshGroupMsg() {
        pause(Constance.FETCH_OFFLINE_MSG_CODE)
        lastMsgRegister?.let { getOfflineGroupMsg(it.groupId, it.ownerId) }
    }

    fun updateSessionStatus(groupId: Long, disturbType: Int? = null, top: Int? = null, groupName: String? = null, des: String? = null) {
        ImApi.getOptionApi().call({ it.updateSessionInfo(groupId, disturbType, top, des, groupName) }, Schedulers.io(), Schedulers.newThread()) { i, d, _ ->
            if (i && d != null) {
                getAppContext()?.let {
                    val sd = DbHelper.get(it)?.db?.sessionDao()
                    val local = sd?.findSessionById(d.groupId)
                    local?.updateConfigs(disturbType, top, groupName, des)
                    if (local != null) {
                        sd.insertOrChangeSession(local)
                        postToUiObservers(local, ClientHubImpl.PAYLOAD_CHANGED) {}
                    }
                }
            }
        }
    }

    fun registerChatRoom(groupId: Long, ownerId: Long) {
        pause(Constance.FETCH_OFFLINE_MSG_CODE)
        val callId = Constance.CALL_ID_REGISTER_CHAT
        val data = GetImMessageReq.newBuilder()
        data.groupId = groupId
        data.ownerId = ownerId
        this.lastMsgRegister = LastMsgReqBean(groupId, ownerId)
        IMHelper.send(data.build(), callId, Constance.SEND_MSG_DEFAULT_TIMEOUT, isSpecialData = true, ignoreConnecting = false, sendBefore = null)
    }

    fun leaveChatRoom(groupId: Long) {
        val callId = Constance.CALL_ID_LEAVE_CHAT_ROOM
        val data = LeaveImGroupReq.newBuilder()
        data.groupId = groupId
        this.lastMsgRegister = null
        IMHelper.send(data.build(), callId, Constance.SEND_MSG_DEFAULT_TIMEOUT, isSpecialData = true, ignoreConnecting = false, sendBefore = null)
    }

    private fun getOfflineChatMsg(groupId: Long, ownerId: Long) {
        val callId = Constance.CALL_ID_GET_OFFLINE_CHAT_MESSAGES
        val data = GetImHistoryMsgReq.newBuilder()
        data.groupId = groupId
        data.ownerId = ownerId
        IMHelper.send(data.build(), callId, Constance.SEND_MSG_DEFAULT_TIMEOUT, isSpecialData = false, ignoreConnecting = false, sendBefore = null)
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
            registerChatRoom(it.groupId, it.ownerId)
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

    internal fun onOfflineMsgPatched() {
        val gid = lastMsgRegister?.groupId ?: return
        withDb {
            val resendMsg = it?.sendMsgDao()?.findAllBySessionId(gid)
            resendMsg?.forEach { msg ->
                Sender.resendMessage(msg)
            }
        }
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
            withDb(app) { it?.clearAllTables() }
        }.start()
    }

    fun <R> withDb(app: Application? = null, run: (IMDb?) -> R?): R? {
        return try {
            val ctx = app ?: getAppContext() ?: throw NullPointerException()
            run(DbHelper.get(ctx)?.db)
        } catch (e: Exception) {
            log("failed to open db ,case : ${e.message}");null
        }
    }
}