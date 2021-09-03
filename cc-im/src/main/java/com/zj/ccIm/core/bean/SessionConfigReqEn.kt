package com.zj.ccIm.core.bean


data class SessionConfigReqEn(
    var groupId: Long = 0,
    var disturbType: Int? = null,
    var top: Int? = null,
    var des: String? = null,
    var groupName: String? = null,
)