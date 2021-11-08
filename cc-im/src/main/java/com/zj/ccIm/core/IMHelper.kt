package com.zj.ccIm.core

import android.app.Application
import android.app.Notification
import com.zj.ccIm.core.api.ImApi
import com.zj.database.DbHelper
import com.zj.im.chat.core.BaseOption
import com.zj.im.chat.hub.ClientHub
import com.zj.im.chat.hub.ServerHub
import com.zj.im.main.impl.IMInterface
import com.zj.ccIm.core.impl.ClientHubImpl
import com.zj.ccIm.core.sp.SPHelper
import com.zj.database.IMDb
import com.zj.database.entity.SessionInfoEntity
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
import com.zj.ccIm.core.impl.ServerHubImpl
import com.zj.ccIm.core.sender.MsgSender
import com.zj.ccIm.core.sender.SendMsgConfig
import com.zj.ccIm.error.ConnectionError
import com.zj.ccIm.error.DBFileException
import com.zj.ccIm.error.InitializedException
import com.zj.ccIm.live.LiveIMHelper
import com.zj.ccIm.live.impl.LiveClientHubImpl
import com.zj.ccIm.live.impl.LiveServerHubImpl
import com.zj.database.entity.SendMessageReqEn
import com.zj.im.chat.exceptions.IMException
import com.zj.im.sender.OnSendBefore
import com.zj.im.utils.log.NetWorkRecordInfo
import java.lang.StringBuilder


@Suppress("MemberVisibilityCanBePrivate", "unused")
object IMHelper : IMInterface<Any?>() {

    private var lastMsgRegister: GetMsgReqBean? = null
    internal var imConfig: ImConfigIn? = null
        get() {
            return if (field == null) {
                postIMError(InitializedException("get IM Config"));null
            } else field
        }

    fun init(app: Application, imConfig: ImConfigIn) {
        this.imConfig = imConfig
        Constance.app = app
        Fetcher.init()
        SPHelper.init("im_sp_main", app)
        val option = BaseOption.create(app)
        if (imConfig.debugAble()) option.debug()
        if (imConfig.logAble()) option.logsCollectionAble { true }.logsFileName("IM").setLogsMaxRetain(3L * 24 * 60 * 60 * 1000)
        option.setNotify(Notification())
        initChat(option.build())
    }

    override fun getClient(): ClientHub<Any?> {
        return if (imConfig?.useLive() == true) LiveClientHubImpl() else ClientHubImpl()
    }

    override fun getServer(): ServerHub<Any?> {
        return if (imConfig?.useLive() == true) LiveServerHubImpl() else ServerHubImpl()
    }

    override fun onError(e: IMException) {
        if (BuildConfig.DEBUG && e !is ConnectionError) throw  e
    }

    override fun onSdkDeadlyError(e: IMException) {
        imConfig?.onSdkDeadlyError(e)
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
                    val mk = com.zj.database.ut.Constance.generateKey(com.zj.database.ut.Constance.KEY_OF_SESSIONS, d.groupId)
                    val localMsg = smd?.findSessionMsgInfoByKey(mk)
                    local?.updateConfigs(disturbType, top, groupName, des)
                    local?.sessionMsgInfo = localMsg
                    if (local != null) {
                        sd.insertOrChangeSession(local)
                        postToUiObservers(SessionInfoEntity::class.java, local, ClientHubImpl.PAYLOAD_CHANGED)
                    }
                }
            }
        }
    }

    fun registerChatRoom(groupId: Long, ownerId: Int, targetUserId: Int? = null, vararg channel: FetchMsgChannel) {
        if (!channel.isNullOrEmpty()) beginMessageTempRecord(channel[0].serializeName)
        this.lastMsgRegister = GetMsgReqBean(groupId, ownerId.coerceAtLeast(0), targetUserId?.coerceAtLeast(0), null, null, channel)
        if (this.lastMsgRegister?.checkValid() != true) return
        pause(Constance.FETCH_OFFLINE_MSG_CODE)
        send(lastMsgRegister?.getCopyData(), Constance.CALL_ID_REGISTER_CHAT, Constance.SEND_MSG_DEFAULT_TIMEOUT, isSpecialData = true, ignoreConnecting = false, sendBefore = null)
    }

    fun leaveChatRoom(): NetWorkRecordInfo? {
        val last = lastMsgRegister?.getCopyData()
        val callId = Constance.CALL_ID_LEAVE_CHAT_ROOM
        send(last, callId, Constance.SEND_MSG_DEFAULT_TIMEOUT, isSpecialData = true, ignoreConnecting = false, sendBefore = null)
        this.lastMsgRegister = null
        return last?.channels?.let {
            if (it.isNotEmpty()) endMessageTempRecord(it[0].serializeName) else null
        }
    }

    fun deleteSession(@DeleteSessionType type: Int, groupId: Long, ownerIdIfOwner: Int? = null, uidIfFans: Int? = null) {
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

    fun deleteMsgByClientId(clientId: String) {
        MessageDbOperator.deleteMsg(clientId)
    }

    internal fun tryToRegisterAfterConnected(): Boolean {
        lastMsgRegister?.let {
            registerChatRoom(it.groupId, it.ownerId, it.targetUserid, *it.channels)
            return true
        }
        return false
    }

    internal fun <T : Any> postToUiObservers(data: T, payload: String? = null, onFinish: (() -> Unit)? = null) {
        val cls = if (data is Collection<*>) {
            throw NullPointerException("To send an array, you need to specify the Class type, and call [postToUiObservers(cls: Class<>?,...)] to handle this problem.")
        } else {
            data::class.java
        }
        postToUiObservers(cls, data, payload, onFinish)
    }

    internal fun <T : Any> postToUiObservers(cls: Class<*>?, data: T?, payload: String? = null, onFinish: (() -> Unit)? = null) {
        super.postToUi(cls, data, payload, onFinish ?: {})
    }

    internal fun onMsgRegistered(lrb: GetMsgReqBean) {
        pause(Constance.FETCH_OFFLINE_MSG_CODE)
        routeToServer(lrb, Constance.CALL_ID_GET_OFFLINE_CHAT_MESSAGES)
    }

    private fun close() {
        lastMsgRegister = null
        Fetcher.cancelAll()
        LiveIMHelper.close()
    }

    override fun shutdown(case: String) {
        close()
        super.shutdown(case)
    }

    /**
     * must call when login out
     * */
    fun loginOut() {
        Thread {
            close()
            SPHelper.clear()
            withDb { it.clearAllTables() }
            shutdown("on logout called !")
        }.start()
    }

    fun deleteSendingMsgByClientId(clientId: String) {
        withDb {
            it.sendMsgDao().deleteByCallId(clientId)
        }
    }

    internal fun getDb(): IMDb? {
        val ctx = Constance.app ?: getAppContext() ?: return null
        return try {
            DbHelper.get(ctx)?.db
        } catch (e: Exception) {
            postIMError(DBFileException());null
        }
    }

    internal fun <R> withDb(imDb: IMDb? = null, run: (IMDb) -> R?): R? {
        return try {
            (imDb ?: getDb())?.let {
                run(it)
            }
        } catch (e: Exception) {
            ImLogs.recordErrorInFile("IMHelper.OpenDb", "failed to open db ,case : ${e.message}");null
        }
    }

    fun sendMsgWithChannel(sen: SendMessageReqEn, clientMsgId: String, sendMsgDefaultTimeout: Long, isSpecialData: Boolean, ignoreConnecting: Boolean, sendBefore: OnSendBefore<Any?>?) {
        sen.key = getCurrentChannelSendingKey(sen.groupId)
        ImLogs.recordLogsInFile("sendMsgWithChannel", "send new Msg by sending key:${sen.key}")
        super.send(sen, clientMsgId, sendMsgDefaultTimeout, isSpecialData, ignoreConnecting, sendBefore)
    }

    val Sender: MsgSender; get() = MsgSender().createConfig(SendMsgConfig())

    val CustomSender: SendMsgConfig; get() = SendMsgConfig(true)

    fun queryAllDBColumnsCount(): StringBuilder {
        val sb = StringBuilder()
        withDb {
            sb.append("messageCount = ${it.messageDao().findAll().size}\n")
            sb.append("sendingCount = ${it.sendMsgDao().findAll().size}\n")
            sb.append("sessionsCount = ${it.sessionDao().findAll().size}\n")
            sb.append("privateOwnerSession = ${it.privateChatOwnerDao().findAll().size}\n")
            sb.append("sessionLastMsgCount = ${it.sessionMsgDao().findAll().size}")
        }
        return sb
    }

    internal fun getCurrentChannelSendingKey(groupId: Long): String {
        return if (lastMsgRegister != null) {
            val channelGroups = lastMsgRegister?.channels?.groupBy { it.classification }
            val groupPrefix = channelGroups?.keys?.joinToString { "$it:" }
            if (!groupPrefix.isNullOrEmpty()) "$groupPrefix$groupId" else ""
        } else ""
    }
}