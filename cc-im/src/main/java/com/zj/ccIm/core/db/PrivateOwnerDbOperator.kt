package com.zj.ccIm.core.db

import com.zj.ccIm.CcIM
import com.zj.ccIm.core.IMHelper
import com.zj.ccIm.core.impl.ClientHubImpl
import com.zj.database.IMDb
import com.zj.database.entity.MessageInfoEntity
import com.zj.database.entity.PrivateOwnerEntity
import com.zj.database.entity.SessionInfoEntity
import com.zj.database.entity.SessionLastMsgInfo
import com.zj.database.ut.Constance
import com.zj.database.ut.Constance.generateKey

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
                newChat.ownerName = session.ownerName
                val lastSessionMsg = SessionLastMsgInfo().apply {
                    this.key = generateKey(Constance.KEY_OF_PRIVATE_OWNER, ownerId = session.ownerId)
                    this.newMsg = msg
                    this.groupId = session.groupId
                    this.ownerId = session.ownerId
                    this.msgNum = 1
                }
                it.sessionMsgDao().insertOrUpdateSessionMsgInfo(lastSessionMsg)
                newChat.sessionMsgInfo = lastSessionMsg
                chatDao.insertOrUpdate(newChat)
                CcIM.postToUiObservers(newChat, ClientHubImpl.PAYLOAD_ADD)

            }
        }
    }

    fun notifyAllSession(callId: String) {
       IMHelper.withDb {
            val sessions = it.privateChatOwnerDao().findAll()
            val lstMsgDao = it.sessionMsgDao()
            for (s in sessions) {
                val key = generateKey(Constance.KEY_OF_PRIVATE_OWNER, ownerId = s.ownerId)
                s.sessionMsgInfo = lstMsgDao.findSessionMsgInfoByKey(key)
            }
            CcIM.postToUiObservers(PrivateOwnerEntity::class.java, sessions, callId)
        }
    }

    fun deleteSession(groupId: Long) {
       IMHelper.withDb {
            val session = it.privateChatOwnerDao().findByGroupId(groupId)
            if (session != null) {
                it.privateChatOwnerDao().delete(session)
            }
            val key = generateKey(Constance.KEY_OF_PRIVATE_OWNER, ownerId = session.ownerId)
            it.sessionMsgDao().deleteByKey(key)
            CcIM.postToUiObservers(session, ClientHubImpl.PAYLOAD_DELETE)
        }
    }

    fun updateSessionInfo(sessionInfoEntity: SessionInfoEntity) {
       IMHelper.withDb {
            val privateOwnerDb = it.privateChatOwnerDao()
            val lastMsgDao = it.sessionMsgDao()
            val en = privateOwnerDb.findByGroupId(sessionInfoEntity.groupId)
            val lastMsgId = generateKey(Constance.KEY_OF_PRIVATE_OWNER, ownerId = sessionInfoEntity.ownerId)
            en?.ownerName = sessionInfoEntity.ownerName
            en?.avatar = sessionInfoEntity.logo
            en?.sessionMsgInfo = lastMsgDao.findSessionMsgInfoByKey(lastMsgId)
            CcIM.postToUiObservers(en, ClientHubImpl.PAYLOAD_CHANGED)
        }
    }
}