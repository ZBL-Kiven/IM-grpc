package com.zj.protocol

data class GrpcConfig(val host: String, val port: Int, val keepAliveTimeOut: Long, val idleTimeOut: Long)