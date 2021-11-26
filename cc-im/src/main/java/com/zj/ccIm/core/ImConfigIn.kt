package com.zj.ccIm.core

import com.zj.im.chat.exceptions.IMException

interface ImConfigIn {

    fun getUserId(): Int

    fun getToken(): String

    fun getIMHost(): String

    fun getGrpcAddress(): Pair<String, Int>

    fun getApiHeader(): Map<String, String>

    fun logAble(): Boolean

    fun debugAble(): Boolean

    fun getUserName(): String

    fun getUserAvatar(): String

    fun useLive(): Boolean = false

    fun getHeatBeatsTimeOut(): Long

    fun getIdleTimeOut(): Long

    fun onAlertError(e: IMException)

    fun onAuthenticationError()

    fun onSdkDeadlyError(e: IMException)

}