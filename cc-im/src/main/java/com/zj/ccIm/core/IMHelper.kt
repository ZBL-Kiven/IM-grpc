package com.zj.ccIm.core

import android.app.Application
import android.app.Notification
import android.util.Log
import com.zj.ccIm.core.api.ImApi
import com.zj.database.DbHelper
import com.zj.im.chat.core.BaseOption
import com.zj.im.chat.hub.ClientHub
import com.zj.im.chat.hub.ServerHub
import com.zj.im.main.impl.IMInterface
import com.zj.ccIm.core.impl.ClientHubImpl
import com.zj.ccIm.core.impl.ServerHubImpl
import com.zj.ccIm.core.sp.SPHelper
import com.zj.database.IMDb
import com.zj.database.entity.SessionInfoEntity
import com.zj.protocol.grpc.GetImMessageReq
import com.zj.protocol.grpc.LeaveImGroupReq
import com.zj.ccIm.annos.DeleteSessionType
import com.zj.ccIm.logger.ImLogs
import com.zj.database.entity.PrivateOwnerEntity
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType
import okhttp3.RequestBody
import com.google.gson.Gson
import com.zj.ccIm.BuildConfig
import com.zj.ccIm.core.bean.*
import com.zj.ccIm.core.db.MessageDbOperator
import com.zj.ccIm.core.fecher.*
import com.zj.ccIm.core.fecher.Fetcher
import com.zj.ccIm.core.fecher.GroupSessionFetcher
import com.zj.ccIm.core.fecher.PrivateOwnerSessionFetcher
import com.zj.ccIm.core.sender.MsgSender
import com.zj.ccIm.core.sender.SendMsgConfig
import com.zj.ccIm.error.ConnectionError
import com.zj.database.entity.SessionLastMsgInfo


@Suppress("MemberVisibilityCanBePrivate", "unused")
object IMHelper : IMInterface<Any?>() {

    private var lastMsgRegister: GetMsgReqBean? = null
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
        if (BuildConfig.DEBUG && e !is ConnectionError) throw  e
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

    fun refreshSessions(result: FetchResultRunner) {
        Fetcher.refresh(GroupSessionFetcher, result)
    }

    fun refreshPrivateOwnerSessions(result: FetchResultRunner) {
        Fetcher.refresh(PrivateOwnerSessionFetcher, result)
    }

    fun getChatMsg(bean: GetMsgReqBean, callId: String) {
        bean.callIdPrivate = callId
        routeToServer(bean, Constance.CALL_ID_GET_MORE_MESSAGES)
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
                    val mk = SessionLastMsgInfo.generateKey(com.zj.database.ut.Constance.KEY_OF_SESSIONS, d.groupId)
                    val localMsg = smd?.findSessionMsgInfoByKey(mk)
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

    fun registerChatRoom(groupId: Long, ownerId: Int, targetUserId: Int? = null, vararg channel: FetchMsgChannel) {
        this.lastMsgRegister = GetMsgReqBean(groupId, ownerId.coerceAtLeast(0), targetUserId?.coerceAtLeast(0), null, null, channel)
        if (this.lastMsgRegister?.checkValid() != true) return
        pause(Constance.FETCH_OFFLINE_MSG_CODE)
        val callId = Constance.CALL_ID_REGISTER_CHAT
        val data = GetImMessageReq.newBuilder()
        data.groupId = groupId
        data.ownerId = ownerId.toLong()
        data.targetUserid = targetUserId?.toLong() ?: 0
        channel.forEach { data.addChannel(it.serializeName) }
        send(data.build(), callId, Constance.SEND_MSG_DEFAULT_TIMEOUT, isSpecialData = true, ignoreConnecting = false, sendBefore = null)
        clearBadges(lastMsgRegister?.getCopyData())
    }

    fun leaveChatRoom(groupId: Long) {
        val callId = Constance.CALL_ID_LEAVE_CHAT_ROOM
        val data = LeaveImGroupReq.newBuilder()
        data.groupId = groupId
        send(data.build(), callId, Constance.SEND_MSG_DEFAULT_TIMEOUT, isSpecialData = true, ignoreConnecting = false, sendBefore = null)
        clearBadges(lastMsgRegister?.getCopyData())
        this.lastMsgRegister = null
    }

    fun deleteSession(@DeleteSessionType type: String, groupId: Long, ownerIdIfOwner: Int? = null, uidIfFans: Int? = null) {
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

    fun clearBadges(copyData: GetMsgReqBean?) {
        routeToClient(copyData, Constance.CALL_ID_CLEAR_SESSION_BADGE)
    }

    fun deleteMsgByClientId(clientId: String) {
        MessageDbOperator.deleteMsg(clientId)
    }

    internal fun tryToRegisterAfterConnected(): Boolean {
        lastMsgRegister?.let {
            registerChatRoom(it.groupId, it.ownerId, it.targetUserId, *it.channels)
            return true
        }
        return false
    }

    internal fun postToUiObservers(data: Any?, payload: String? = null, onFinish: (() -> Unit)? = null) {
        super.postToUi(data, payload, onFinish ?: {})
    }

    internal fun onMsgRegistered(lrb: GetMsgReqBean) {
        pause(Constance.FETCH_OFFLINE_MSG_CODE)
        routeToServer(lrb, Constance.CALL_ID_GET_OFFLINE_CHAT_MESSAGES)
    }

    fun close() {
        lastMsgRegister = null
        Fetcher.cancelAll()
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

    object Sender : MsgSender(SendMsgConfig())

    fun withCustomSender(): SendMsgConfig {
        return SendMsgConfig(true)
    }
}