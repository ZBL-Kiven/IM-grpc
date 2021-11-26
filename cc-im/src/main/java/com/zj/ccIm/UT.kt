package com.zj.ccIm

import android.os.Handler
import android.os.Looper
import com.zj.ccIm.core.IMHelper
import com.zj.database.entity.PrivateOwnerEntity
import com.zj.database.entity.SendMessageReqEn
import com.zj.database.entity.SessionInfoEntity
import com.zj.database.entity.SessionLastMsgInfo
import java.lang.StringBuilder

internal object MainLooper : Handler(Looper.getMainLooper())

object UT {

    fun queryAllSessions(): List<SessionInfoEntity>? {
        return IMHelper.withDb {
            it.sessionDao().findAll()
        }
    }

    fun queryAllSessionsLastMessages(): List<SessionLastMsgInfo>? {
        return IMHelper.withDb {
            it.sessionMsgDao().findAll()
        }
    }

    fun queryAllPrivateOwnerSessions(): List<PrivateOwnerEntity>? {
        return IMHelper.withDb {
            it.privateChatOwnerDao().findAll()
        }
    }

    fun queryAllSendingMsg(): List<SendMessageReqEn>? {
        return IMHelper.withDb {
            it.sendMsgDao().findAll()
        }
    }

    fun queryAllDBColumnsCount(): StringBuilder {
        val sb = StringBuilder()
        IMHelper.withDb {
            sb.append("messageCount = ${it.messageDao().findAll().size}\n")
            sb.append("sendingCount = ${it.sendMsgDao().findAll().size}\n")
            sb.append("sessionsCount = ${it.sessionDao().findAll().size}\n")
            sb.append("privateOwnerSession = ${it.privateChatOwnerDao().findAll().size}\n")
            sb.append("sessionLastMsgCount = ${it.sessionMsgDao().findAll().size}")
        }
        return sb
    }
}