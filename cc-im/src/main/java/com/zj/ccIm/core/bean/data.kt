@file:Suppress("unused")

package com.zj.ccIm.core.bean

import com.zj.database.entity.MessageInfoEntity
import java.io.Serializable


data class AssetsChanged(val spark: Int?, val diamondNum: Int?)

data class DeleteSessionInfo(val groupId: Long, val targetUserId: Int?, val status: Int)

data class MessageTotalDots(val dotsOfAll: DotsInfo, val clapHouseDots: DotsInfo, val privateOwnerDots: DotsInfo, val privateFansDots: DotsInfo)

/**
 * @property role 角色 0普通用户 2管理员
 * */
data class RoleInfo(val groupId: Long, val role: Int)

data class LiveStateInfo(val userId: String, val livingStatus: Boolean)

data class RoteInfo<CLS : Any>(val data: CLS?, val pending: Any? = null)


/**
 * ============================================================ unobservable classes =============================================================================================================================
 * */

data class GetMoreMessagesResult(val callId: String, val isOK: Boolean, val data: List<MessageInfoEntity?>?, val rq: ChannelRegisterInfo, val pl: Any?, val e: Throwable?)

class FetchResult(val success: Boolean, val isFirstFetch: Boolean, val isNullData: Boolean, val errorMsg: String? = null) : Serializable

data class DotsInfo(var unreadMessages: Int = 0, var unreadQuestions: Int = 0, var questionNum: Int = 0)

