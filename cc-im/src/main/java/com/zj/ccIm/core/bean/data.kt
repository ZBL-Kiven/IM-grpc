package com.zj.ccIm.core.bean

import com.zj.database.entity.MessageInfoEntity
import java.io.Serializable


data class AssetsChanged(val spark: Int?, val diamondNum: Int?)

data class DeleteSessionInfo(val groupId: Long, val targetUserId: Int?, val status: Int)

data class GetMoreMessagesInfo(val callId: String, val isOK: Boolean, val data: Map<String, List<MessageInfoEntity?>?>?, val rq: GetMsgReqBean, val pl: Any?, val e: Throwable?)

data class MessageTotalDots(val dots: Int)

class FetchResult(val success: Boolean, val isFirstFetch: Boolean, val isNullData: Boolean, val errorMsg: String? = null) : Serializable