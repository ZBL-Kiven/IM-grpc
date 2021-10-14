package com.zj.ccIm.core.bean

import com.zj.database.entity.MessageInfoEntity


data class AssetsChanged(val spark: Int?, val diamondNum: Int?)

data class DeleteSessionInfo(val groupId: Long, val targetId: Int?, val pl: String)

data class GetMoreMessagesInfo(val callId: String, val isOK: Boolean, val data: Map<String, List<MessageInfoEntity?>?>?, val rq: GetMsgReqBean, val pl: Any?, val e: Throwable?)