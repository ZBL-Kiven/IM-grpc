package com.zj.ccIm.core.db

import com.zj.ccIm.core.IMHelper
import com.zj.ccIm.core.fecher.Fetcher
import com.zj.ccIm.core.impl.ClientHubImpl
import com.zj.ccIm.core.sp.SPHelper
import com.zj.ccIm.error.FetchPrivateChatSessionResult
import com.zj.database.IMDb
import com.zj.database.entity.MessageInfoEntity
import com.zj.database.entity.PrivateOwnerEntity
import com.zj.database.entity.SessionLastMsgInfo

internal object PrivateOwnerDbOperator {

    fun createPrivateChatInfoIfNotExits(db: IMDb? = null, msg: MessageInfoEntity) {
        IMHelper.withDb(imDb = db) {
            val chatDao = it.privateChatOwnerDao()
            val session = it.sessionDao().findSessionById(msg.groupId)
            val localChat = chatDao.findByGroupId(msg.groupId)
            if (session != null && localChat == null) {
                val newChat = PrivateOwnerEntity()
                newChat.avatar = session.logo
                newChat.groupId = session.groupId
                newChat.ownerId = session.ownerId
                newChat.ownerName = session.groupName
                val lastSessionMsg = SessionLastMsgInfo().apply {
                    this.newMsg = msg
                    this.groupId = session.ownerId.toLong()
                    this.msgNum = 1
                }
                it.sessionMsgDao().insertOrUpdateSessionMsgInfo(lastSessionMsg)
                newChat.sessionMsgInfo = lastSessionMsg
                chatDao.insertOrUpdate(newChat)
                IMHelper.postToUiObservers(newChat, ClientHubImpl.PAYLOAD_ADD)
            }
        }
    }

    fun insertANewChatInfo(info: PrivateOwnerEntity) {
        IMHelper.withDb {
            it.privateChatOwnerDao().insertOrUpdate(info)
            IMHelper.postToUiObservers(info, ClientHubImpl.PAYLOAD_ADD)
        }
    }

    fun notifyAllSession(callId: String) {
        IMHelper.withDb {
            val sessions = it.privateChatOwnerDao().findAll()
            val lstMsgDao = it.sessionMsgDao()
            for (s in sessions) {
                s.sessionMsgInfo = lstMsgDao.findSessionMsgInfoBySessionId(s.ownerId.toLong())
            }
            val isFirst = SPHelper[Fetcher.SP_FETCH_PRIVATE_OWNER_CHAT_SESSIONS_TS, 0L] ?: 0L <= 0
            IMHelper.postToUiObservers(FetchPrivateChatSessionResult(true, isFirst, sessions.isNullOrEmpty()))
            IMHelper.postToUiObservers(sessions, callId)
        }
    }

    fun deleteSession(ownerId: Long) {
        IMHelper.withDb {
            val session = it.privateChatOwnerDao().findByGroupId(ownerId)
            if (session != null) {
                it.privateChatOwnerDao().delete(session)
            }
            it.sessionMsgDao().deleteBySessionId(session.ownerId.toLong())
            IMHelper.postToUiObservers(session, ClientHubImpl.PAYLOAD_DELETE)
        }
    }
}