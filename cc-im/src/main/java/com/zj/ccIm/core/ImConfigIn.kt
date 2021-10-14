package com.zj.ccIm.core

interface ImConfigIn {

    fun getUserId(): Int

    fun getUserName(): String

    fun getUserAvatar(): String

    fun getToken(): String

    fun onAuthenticationError()

    fun getGrpcAddress(): Pair<String, Int>

    fun getIMHost(): String

    fun getHeatBeatsTimeOut(): Long

    fun getIdleTimeOut(): Long
}