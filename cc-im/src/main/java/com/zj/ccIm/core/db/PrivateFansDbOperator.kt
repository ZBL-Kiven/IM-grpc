package com.zj.ccIm.core.db

import com.zj.ccIm.CcIM
import com.zj.ccIm.core.IMHelper
import com.zj.ccIm.core.bean.PrivateFansEn
import com.zj.ccIm.core.impl.ClientHubImpl
import com.zj.database.ut.Constance

object PrivateFansDbOperator {

    fun deleteSession(callId: String, targetUserId: Int?, groupId: Long) {
        if (targetUserId == null) return
        val en = PrivateFansEn().apply { this.userId = targetUserId }
        BadgeDbOperator.notifyOwnerSessionBadgeWithFansSessionChanged(targetUserId, groupId)
        IMHelper.withDb {
            val lstMsgDao = it.sessionMsgDao()
            val key = Constance.generateKey(Constance.KEY_OF_PRIVATE_FANS, userId = targetUserId)
            lstMsgDao.deleteByKey(key)
            CcIM.postToUiObservers(en, ClientHubImpl.PAYLOAD_DELETE)
            SessionLastMsgDbOperator.notifyAllSessionDots(callId)
        }
    }
}