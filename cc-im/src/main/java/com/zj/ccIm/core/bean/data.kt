package com.zj.ccIm.core.bean

import com.zj.database.entity.MessageInfoEntity
import java.io.Serializable


data class AssetsChanged(val spark: Int?, val diamondNum: Int?)

data class DeleteSessionInfo(val groupId: Long, val targetUserId: Int?, val status: Int)

data class MessageTotalDots(val dots: Int)

/**
 * @property role 角色 0普通用户 2管理员
 * */
data class RoleInfo(val groupId: Long, val role: Int)


/**
 * ============================================================ unobservable classes =============================================================================================================================
 * */

data class GetMoreMessagesResult(val callId: String, val isOK: Boolean, val data: Map<String, List<MessageInfoEntity?>?>?, val rq: ChannelRegisterInfo, val pl: Any?, val e: Throwable?)

class FetchResult(val success: Boolean, val isFirstFetch: Boolean, val isNullData: Boolean, val errorMsg: String? = null) : Serializable
