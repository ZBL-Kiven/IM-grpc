package com.zj.ccIm.core.db

import com.zj.database.entity.SessionLastMsgInfo

internal interface SessionOperateIn {
    fun onDealSessionLastMsgInfo(info: SessionLastMsgInfo?): Pair<String, Any?>?
    fun onDealPrivateFansSessionLastMsgInfo(info: SessionLastMsgInfo?): Pair<String, Any?>?
    fun onDealPrivateOwnerSessionLastMsgInfo(info: SessionLastMsgInfo?): Pair<String, Any?>?
}