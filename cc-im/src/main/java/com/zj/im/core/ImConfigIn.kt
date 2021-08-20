package com.zj.im.core

import java.net.URL

interface ImConfigIn {

    fun getUserId(): Int

    fun getToken(): String

    fun getGrpcAddress(): URL

    fun getIMHost(): String
}