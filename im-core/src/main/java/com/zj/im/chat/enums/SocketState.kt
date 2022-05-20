package com.zj.im.chat.enums

@Suppress("unused")
sealed class ConnectionState(internal val code: Int) {

    object INIT : ConnectionState(0x11981)
    object PING : ConnectionState(0x11982)
    object PONG : ConnectionState(0x11983)
    class CONNECTION(val fromReconnect: Boolean) : ConnectionState(0x11984)
    class RECONNECT(val reason: String) : ConnectionState(0x11985)
    class CONNECTED(val fromReconnect: Boolean) : ConnectionState(0x11986)
    class ERROR(val reason: String) : ConnectionState(0x11987)
    object OFFLINE : ConnectionState(0x11988)

    fun isConnected(): Boolean {
        return this is CONNECTED || this is PING || this is PONG
    }

    fun canConnect(): Boolean {
        return this is INIT || this is ERROR || this is OFFLINE || this is RECONNECT
    }

    fun isValidState(): Boolean {
        return this !is INIT && this !is PING && this !is PONG
    }

    fun isErrorType(): Boolean {
        return this is ERROR || this is OFFLINE || this is RECONNECT
    }
}