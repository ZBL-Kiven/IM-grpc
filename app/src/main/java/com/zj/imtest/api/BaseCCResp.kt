package com.zj.imtest.api

class BaseCCResp<T> {
    var code: Int = 0
    var msg: String = ""
    var date: Long = 0
    var data: T? = null
}