package com.zj.ccIm.core

import android.app.Application
import android.app.Notification
import androidx.lifecycle.LifecycleOwner
import com.google.gson.Gson
import com.zj.ccIm.CcIM
import com.zj.ccIm.annos.DeleteSessionType
import com.zj.ccIm.core.api.ImApi
import com.zj.ccIm.core.bean.*
import com.zj.ccIm.core.db.MessageDbOperator
import com.zj.ccIm.core.fecher.*
import com.zj.ccIm.core.fecher.Fetcher
import com.zj.ccIm.core.fecher.GroupSessionFetcher
import com.zj.ccIm.core.fecher.MessageFetcher
import com.zj.ccIm.core.fecher.PrivateOwnerSessionFetcher
import com.zj.ccIm.core.impl.ClientHubImpl
import com.zj.ccIm.core.sender.MsgSender
import com.zj.ccIm.core.sender.SendMsgConfig
import com.zj.ccIm.error.DBFileException
import com.zj.ccIm.live.LiveIMHelper
import com.zj.ccIm.logger.ImLogs
import com.zj.database.DbHelper
import com.zj.database.IMDb
import com.zj.database.entity.MessageInfoEntity
import com.zj.database.entity.SessionInfoEntity
import com.zj.im.chat.core.BaseOption
import com.zj.im.chat.interfaces.MessageInterface
import com.zj.im.chat.interfaces.MessageInterface.Companion.hasObserver
import com.zj.im.chat.poster.UIHandlerCreator
import com.zj.im.chat.poster.UIHelperCreator
import com.zj.im.utils.log.NetWorkRecordInfo
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType
import okhttp3.RequestBody

@Suppress("MemberVisibilityCanBePrivate", "unused")
object IMHelper {

    val Sender: MsgSender; get() = MsgSender().withConfig(SendMsgConfig())

    val CustomSender: SendMsgConfig; get() = SendMsgConfig(true)

    fun <CLS : Any> route(data: RoteInfo<CLS>, callId: String) {
        CcIM.postToUiObservers(data, callId)
    }

    fun queryUIObserver(uniqueCode: Any): Boolean {
        return hasObserver(uniqueCode)
    }

    inline fun <reified T : Any, reified R : Any> addTransferObserver(uniqueCode: Any, lifecycleOwner: LifecycleOwner? = null): UIHandlerCreator<T, R> {
        return addTransferObserver(T::class.java, R::class.java, uniqueCode, lifecycleOwner)
    }

    inline fun <reified T : Any> addReceiveObserver(uniqueCode: Any, lifecycleOwner: LifecycleOwner? = null): UIHelperCreator<T, T, *> {
        return addObserver(T::class.java, uniqueCode, lifecycleOwner)
    }

    fun <T : Any> addObserver(classT: Class<T>, uniqueCode: Any, lifecycleOwner: LifecycleOwner? = null): UIHelperCreator<T, T, *> {
        if (classT == MessageInfoEntity::class.java) {
            throw IllegalStateException("please use [ChannelRegisterInfo] to register message observer!")
        }
        return CcIM.addReceiveObserver(classT, uniqueCode, lifecycleOwner)
    }

    fun <T : Any, R : Any> addTransferObserver(classT: Class<T>, classR: Class<R>, uniqueCode: Any, lifecycleOwner: LifecycleOwner? = null): UIHandlerCreator<T, R> {
        if (classT == MessageInfoEntity::class.java) {
            throw IllegalStateException("please use [ChannelRegisterInfo] to register message observer!")
        }
        return CcIM.addTransferObserver(classT, classR, uniqueCode, lifecycleOwner)
    }

    fun getIMInterface(): MessageInterface<*> {
        return CcIM.getImInterface()
    }

    fun init(app: Application, imConfig: ImConfigIn) {
        Constance.app = app
        Constance.dbId = imConfig.getUserId()
        Fetcher.init()
        if (getDbHelper()?.init() != true) {
            imConfig.onSdkDeadlyError(DBFileException())
        }
        val option = BaseOption.create(app)
        if (imConfig.debugAble()) option.debug()
        if (imConfig.logAble()) option.logsCollectionAble { true }.logsFileName("IM").setLogsMaxRetain(3L * 24 * 60 * 60 * 1000)
        option.setNotify(Notification())
        CcIM.init(imConfig, option.build())
    }

    fun refreshSessions(result: FetchResultRunner) {
        Fetcher.refresh(GroupSessionFetcher, result)
    }

    fun refreshPrivateOwnerSessions(result: FetchResultRunner) {
        Fetcher.refresh(PrivateOwnerSessionFetcher, result)
    }

    fun getChatMsg(req: ChannelRegisterInfo, onCalled: (GetMoreMessagesResult) -> Unit) {
        if (!req.checkValid()) {
            val result = GetMoreMessagesResult(req.key, false, null, req, null, IllegalArgumentException("request check valid failed ,check your params and try again!"))
            onCalled.invoke(result)
            return
        }
        if (req.type == null) throw java.lang.NullPointerException("get offline messages with type [1:History] or [0:Newest] , type can not be null!")
        MessageFetcher.getOfflineMessage(req.key, req, true, onCalled)
    }

    fun updateSessionStatus(groupId: Long, disturbType: Int? = null, top: Int? = null, groupName: String? = null, des: String? = null) {
        val conf = SessionConfigReqEn(groupId, disturbType, top, des, groupName)
        val requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), Gson().toJson(conf))
        ImApi.getRecordApi().call({ it.updateSessionInfo(requestBody) }, Schedulers.io(), Schedulers.io()) { i, d, _ ->
            if (i && d != null) {
                CcIM.getAppContext()?.let {
                    val sd = getDb()?.sessionDao()
                    val smd = getDb()?.sessionMsgDao()
                    val local = sd?.findSessionById(d.groupId)
                    val mk = com.zj.database.ut.Constance.generateKey(com.zj.database.ut.Constance.KEY_OF_SESSIONS, d.groupId)
                    val localMsg = smd?.findSessionMsgInfoByKey(mk)
                    local?.updateConfigs(disturbType, top, groupName, des)
                    local?.sessionMsgInfo = localMsg
                    if (local != null) {
                        sd.insertOrChangeSession(local)
                        CcIM.postToUiObservers(SessionInfoEntity::class.java, local, ClientHubImpl.PAYLOAD_CHANGED)
                    }
                }
            }
        }
    }

    fun registerChatRoom(req: ChannelRegisterInfo): String? {
        IMChannelManager.offerLast(req)
        return resumedChatRoomIfConnection(req)
    }

    fun leaveChatRoom(key: String) {
        val r = IMChannelManager.destroy(key)
        if (r != null) {
            CcIM.send(r, Constance.CALL_ID_LEAVE_CHAT_ROOM + r.key, Constance.SEND_MSG_DEFAULT_TIMEOUT, isSpecialData = false, ignoreConnecting = false, sendBefore = null)
        }
    }

    fun startImTempRecord(key: String) {
        CcIM.beginMessageTempRecord(key)
    }

    fun endImTempRecord(key: String): NetWorkRecordInfo? {
        return CcIM.endMessageTempRecord(key)
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
            CcIM.routeToClient(data, Constance.CALL_ID_DELETE_SESSION)
        }
    }

    fun deleteMsgByClientId(clientId: String) {
        MessageDbOperator.deleteMsg(clientId)
    }

    internal fun resumedChatRoomIfConnection(req: ChannelRegisterInfo): String? {
        if (!req.checkValid()) return null
        CcIM.pause(Constance.FETCH_OFFLINE_MSG_CODE)
        CcIM.send(req, Constance.CALL_ID_REGISTER_CHAT + req.key, Constance.SEND_MSG_DEFAULT_TIMEOUT, isSpecialData = false, ignoreConnecting = false, sendBefore = null)
        return req.key
    }

    internal fun onMsgRegistered(lrb: ChannelRegisterInfo) {
        CcIM.pause(Constance.FETCH_OFFLINE_MSG_CODE)
        CcIM.routeToServer(lrb, Constance.CALL_ID_GET_OFFLINE_CHAT_MESSAGES)
    }

    fun deleteSendingMsgByClientId(clientId: String) {
        withDb {
            it.sendMsgDao().deleteByCallId(clientId)
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

    internal fun getDb(): IMDb? {
        return getDbHelper()?.getDb()
    }

    internal fun getDbHelper(): DbHelper? {
        val ctx = Constance.app ?: CcIM.getAppContext() ?: return null
        val dbId = Constance.dbId ?: return null
        return DbHelper.get(ctx, dbId)
    }

    fun getMineRole(groupId: Long?): Int {
        if (groupId == null) return 0
        return withDb {
            it.sessionDao().findSessionById(groupId).role
        } ?: 0
    }

    fun shutdown() {
        CcIM.shutdown("called from app!")
    }

    /**
     * must call when login out
     * */
    fun loginOut() {
        CcIM.shutdown("login out")
        getDbHelper()?.clearAll()
    }

    internal fun close() {
        IMChannelManager.clear()
        Fetcher.cancelAll()
        LiveIMHelper.close()
    }
}