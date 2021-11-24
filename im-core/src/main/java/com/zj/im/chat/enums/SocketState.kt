package com.zj.im.chat.enums

@Suppress("unused")
sealed class ConnectionState {

    object INIT : ConnectionState()
    object PING : ConnectionState()
    object PONG : ConnectionState()
    class CONNECTION(val fromReconnect: Boolean) : ConnectionState()
    class RECONNECT(val reason: String) : ConnectionState()
    class CONNECTED(val fromReconnect: Boolean) : ConnectionState()
    class ERROR(val reason: String) : ConnectionState()
    object OFFLINE : ConnectionState()

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