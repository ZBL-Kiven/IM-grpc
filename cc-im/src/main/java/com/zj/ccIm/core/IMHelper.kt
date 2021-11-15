package com.zj.ccIm.core

import android.app.Application
import android.app.Notification
import androidx.lifecycle.LifecycleOwner
import com.google.gson.Gson
import com.zj.ccIm.CcIM
import com.zj.ccIm.annos.DeleteSessionType
import com.zj.ccIm.core.api.ImApi
import com.zj.ccIm.core.bean.DeleteSessionInfo
import com.zj.ccIm.core.bean.GetMoreMessagesResult
import com.zj.ccIm.core.bean.ChannelRegisterInfo
import com.zj.ccIm.core.bean.SessionConfigReqEn
import com.zj.ccIm.core.db.MessageDbOperator
import com.zj.ccIm.core.fecher.*
import com.zj.ccIm.core.fecher.Fetcher
import com.zj.ccIm.core.fecher.GroupSessionFetcher
import com.zj.ccIm.core.fecher.MessageFetcher
import com.zj.ccIm.core.fecher.PrivateOwnerSessionFetcher
import com.zj.ccIm.core.impl.ClientHubImpl
import com.zj.ccIm.core.sender.MsgSender
import com.zj.ccIm.core.sender.SendMsgConfig
import com.zj.ccIm.core.sp.SPHelper
import com.zj.ccIm.error.DBFileException
import com.zj.ccIm.live.LiveIMHelper
import com.zj.ccIm.logger.ImLogs
import com.zj.database.DbHelper
import com.zj.database.IMDb
import com.zj.database.entity.MessageInfoEntity
import com.zj.database.entity.SessionInfoEntity
import com.zj.im.chat.core.BaseOption
import com.zj.im.chat.interfaces.MessageInterface
import com.zj.im.chat.poster.UIHandlerCreator
import com.zj.im.chat.poster.UIHelperCreator
import com.zj.im.utils.log.NetWorkRecordInfo
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType
import okhttp3.RequestBody
import java.lang.StringBuilder

@Suppress("MemberVisibilityCanBePrivate", "unused")
object IMHelper {

    val Sender: MsgSender; get() = MsgSender().withConfig(SendMsgConfig())

    val CustomSender: SendMsgConfig; get() = SendMsgConfig(true)

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
        Fetcher.init()
        SPHelper.init("im_sp_main", app)
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
        MessageFetcher.getOfflineMessage(req.key, req, onCalled)
    }

    fun updateSessionStatus(groupId: Long, disturbType: Int? = null, top: Int? = null, groupName: String? = null, des: String? = null) {
        val conf = SessionConfigReqEn(groupId, disturbType, top, des, groupName)
        val requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), Gson().toJson(conf))
        ImApi.getRecordApi().call({ it.updateSessionInfo(requestBody) }, Schedulers.io(), Schedulers.io()) { i, d, _ ->
            if (i && d != null) {
                CcIM.getAppContext()?.let {
                    val sd = DbHelper.get(it)?.db?.sessionDao()
                    val smd = DbHelper.get(it)?.db?.sessionMsgDao()
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
        CcIM.pause(Constance.FETCH_OFFLINE_MSG_CODE)
        if (!req.checkValid()) return null
        IMChannelManager.offerLast(req)
        CcIM.send(req, Constance.CALL_ID_REGISTER_CHAT, Constance.SEND_MSG_DEFAULT_TIMEOUT, isSpecialData = false, ignoreConnecting = false, sendBefore = null)
        return req.key
    }

    fun leaveChatRoom(key: String) {
        if (key.isNotEmpty()) {
            val last = IMChannelManager.destroy(key) ?: return
            CcIM.send(last, Constance.CALL_ID_LEAVE_CHAT_ROOM, Constance.SEND_MSG_DEFAULT_TIMEOUT, isSpecialData = false, ignoreConnecting = false, sendBefore = null)
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

    internal fun onMsgRegistered(lrb: ChannelRegisterInfo) {
        CcIM.pause(Constance.FETCH_OFFLINE_MSG_CODE)
        CcIM.routeToServer(lrb, Constance.CALL_ID_GET_OFFLINE_CHAT_MESSAGES)
    }

    fun deleteSendingMsgByClientId(clientId: String) {
        withDb {
            it.sendMsgDao().deleteByCallId(clientId)
        }
    }

    internal fun getDb(): IMDb? {
        val ctx = Constance.app ?: CcIM.getAppContext() ?: return null
        return try {
            DbHelper.get(ctx)?.db
        } catch (e: Exception) {
            CcIM.postIMError(DBFileException());null
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

    fun queryAdminState(groupId: Long?): Int {
        if (groupId == null) return 0
        withDb {
            return@withDb it.sessionDao().findSessionById(groupId).role
        }
        return 0
    }

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

    fun shutdown() {
        CcIM.shutdown("called from app!")
    }

    fun close() {
        IMChannelManager.clear()
        Fetcher.cancelAll()
        LiveIMHelper.close()
    }

    /**
     * must call when login out
     * */
    fun loginOut() {
        CcIM.loginOut()
    }
}