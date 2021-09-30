package com.zj.ccIm.core.bean

data class AssetsChanged(val spark: Int?, val diamondNum: Int?)

data class DeleteSessionInfo(val groupId: Long, val targetId: Long?, val pl: String)