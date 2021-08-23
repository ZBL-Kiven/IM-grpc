package com.zj.ccIm.core

interface ImConfigIn {

    fun getUserId(): Int

    fun getToken(): String

    fun getGrpcAddress(): Pair<String, Int>

    fun getIMHost(): String
}