package com.zj.ccIm.core

interface ImConfigIn {

    fun getUserId(): Int

    fun getUserName(): String

    fun getUserHeadPic(): String

    fun getToken(): String

    fun getGrpcAddress(): Pair<String, Int>

    fun getIMHost(): String
}