package com.zj.ccIm.core

import android.app.Application
import android.app.Notification
import android.util.Log
import com.zj.ccIm.core.api.ImApi
import com.zj.ccIm.core.bean.LastMsgReqBean
import com.zj.ccIm.core.bean.SessionConfigReqEn
import com.zj.database.DbHelper
import com.zj.im.chat.core.BaseOption
import com.zj.im.chat.hub.ClientHub
import com.zj.im.chat.hub.ServerHub
import com.zj.im.main.impl.IMInterface
import com.zj.ccIm.core.fecher.Fetcher
import com.zj.ccIm.core.impl.ClientHubImpl
import com.zj.ccIm.core.impl.ServerHubImpl
import com.zj.ccIm.core.sp.SPHelper
import com.zj.database.IMDb
import com.zj.database.entity.SessionInfoEntity
import com.zj.protocol.grpc.GetImMessageReq
import com.zj.protocol.grpc.LeaveImGroupReq
import com.zj.ccIm.annos.DeleteSessionType
import com.zj.ccIm.core.bean.MessageTotalDots
import com.zj.ccIm.core.fecher.FetchMsgChannel
import com.zj.ccIm.logger.ImLogs
import com.zj.database.entity.PrivateOwnerEntity
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType
import okhttp3.RequestBody
import com.google.gson.Gson
import com.zj.ccIm.core.bean.DeleteSessionInfo


@Suppress("MemberVisibilityCanBePrivate", "unused")
object IMHelper : IMInterface<Any?>() {

    private var lastMsgRegister: LastMsgReqBean? = null
    internal lateinit var imConfig: ImConfigIn

    fun init(app: Application, imConfig: ImConfigIn) {
        this.imConfig = imConfig
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
        when (cls) {
            SessionInfoEntity::class.java -> {
                routeToClient(null, Constance.CALL_ID_START_LISTEN_SESSION)
            }
            MessageTotalDots::class.java -> {
                routeToClient(null, Constance.CALL_ID_START_LISTEN_TOTAL_DOTS)
            }
            PrivateOwnerEntity::class.java -> {
                routeToClient(null, Constance.CALL_ID_START_LISTEN_PRIVATE_OWNER_CHAT)
            }
        }
    }

    fun refreshSessions() {
        Fetcher.onFetchSessions(true)
    }

    fun refreshOrGetChatMsg(bean: LastMsgReqBean? = lastMsgRegister) {
        pause(Constance.FETCH_OFFLINE_MSG_CODE)
        bean?.let { routeToServer(bean, Constance.CALL_ID_GET_OFFLINE_CHAT_MESSAGES) }
    }

    fun updateSessionStatus(groupId: Long, disturbType: Int? = null, top: Int? = null, groupName: String? = null, des: String? = null) {
        val conf = SessionConfigReqEn(groupId, disturbType, top, des, groupName)
        val requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), Gson().toJson(conf))
        ImApi.getRecordApi().call({ it.updateSessionInfo(requestBody) }, Schedulers.io(), Schedulers.newThread()) { i, d, _ ->
            if (i && d != null) {
                getAppContext()?.let {
                    val sd = DbHelper.get(it)?.db?.sessionDao()
                    val smd = DbHelper.get(it)?.db?.sessionMsgDao()
                    val local = sd?.findSessionById(d.groupId)
                    val localMsg = smd?.findSessionMsgInfoBySessionId(d.groupId)
                    local?.updateConfigs(disturbType, top, groupName, des)
                    local?.sessionMsgInfo = localMsg
                    if (local != null) {
                        sd.insertOrChangeSession(local)
                        postToUiObservers(local, ClientHubImpl.PAYLOAD_CHANGED)
                    }
                }
            }
        }
    }

    fun registerChatRoom(groupId: Long, ownerId: Long, targetUserId: Long? = null, vararg channel: FetchMsgChannel) {
        val names = channel.map { it.serializeName }
        this.registerChatRoom(groupId, ownerId, targetUserId, *names.toTypedArray())
    }

    private fun registerChatRoom(groupId: Long, ownerId: Long, targetUserId: Long? = null, vararg channel: String) {
        pause(Constance.FETCH_OFFLINE_MSG_CODE)
        val callId = Constance.CALL_ID_REGISTER_CHAT
        val data = GetImMessageReq.newBuilder()
        data.groupId = groupId
        data.ownerId = ownerId
        data.targetUserid = targetUserId ?: 0
        channel.forEach { data.addChannel(it) }
        this.lastMsgRegister = LastMsgReqBean(groupId, ownerId, targetUserId, null, 0, channel)
        send(data.build(), callId, Constance.SEND_MSG_DEFAULT_TIMEOUT, isSpecialData = true, ignoreConnecting = false, sendBefore = null)
        routeToClient(groupId, Constance.CALL_ID_CLEAR_SESSION_BADGE)
    }

    fun leaveChatRoom(groupId: Long) {
        val callId = Constance.CALL_ID_LEAVE_CHAT_ROOM
        val data = LeaveImGroupReq.newBuilder()
        data.groupId = groupId
        this.lastMsgRegister = null
        send(data.build(), callId, Constance.SEND_MSG_DEFAULT_TIMEOUT, isSpecialData = true, ignoreConnecting = false, sendBefore = null)
        routeToClient(groupId, Constance.CALL_ID_CLEAR_SESSION_BADGE)
    }

    fun deleteSession(@DeleteSessionType type: String, groupId: Long, ownerIdIfOwner: Long? = null, uidIfFans: Long? = null) {
        val errorMsg = "your call delete session with type :$type ,but %s is null"
        catching {
            val targetId = when (type) {
                Comment.DELETE_OWNER_SESSION -> ownerIdIfOwner ?: throw NullPointerException(String.format(errorMsg, "ownerIdIfOwner"))
                Comment.DELETE_FANS_SESSION -> uidIfFans ?: throw NullPointerException(String.format(errorMsg, "uidIfFans"))
                else -> throw IllegalArgumentException("the type never been registered!")
            }
            val data = DeleteSessionInfo(groupId, targetId, type)
            routeToServer(data, Constance.CALL_ID_DELETE_SESSION)
            routeToClient(data, Constance.CALL_ID_DELETE_SESSION)
        }
    }

    internal fun tryToRegisterAfterConnected(): Boolean {
        lastMsgRegister?.let {
            registerChatRoom(it.groupId, it.ownerId, it.targetUserid, *it.channel)
            return true
        }
        return false
    }

    internal fun postToUiObservers(data: Any?, payload: String? = null, onFinish: (() -> Unit)? = null) {
        super.postToUi(data, payload, onFinish ?: {})
    }

    internal fun onMsgRegistered(lrb: LastMsgReqBean) {
        refreshOrGetChatMsg(lrb)
    }

    fun close() {
        lastMsgRegister = null
        Fetcher.cancel()
    }

    override fun shutdown(case: String) {
        close()
        super.shutdown(case)
    }

    /**
     * must call when login out
     * */
    fun loginOut(app: Application) {
        Thread {
            SPHelper.clear()
            withDb(app) { it.clearAllTables() }
            shutdown("on logout called !")
        }.start()
    }


    fun <R> withDb(app: Application? = null, imDb: IMDb? = null, run: (IMDb) -> R?): R? {

        return try {
            val ctx = app ?: getAppContext() ?: throw NullPointerException()
            (imDb ?: DbHelper.get(ctx)?.db)?.let {
                run(it)
            }
        } catch (e: Exception) {
            ImLogs.requireToPrintInFile("IMHelper.OpenDb", "failed to open db ,case : ${e.message}");null
        }
    }
}