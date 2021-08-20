package com.zj.im.chat.enums

enum class ConnectionState(var case: String = "") {

    INIT, PING, PONG, CONNECTED, CONNECTION, CONNECTED_ERROR, NETWORK_STATE_CHANGE, RECONNECT;

    fun isConnected(): Boolean {
        return this == CONNECTED || this == PING || this == PONG
    }

    fun canConnect(): Boolean {
        return this == INIT || this == CONNECTED_ERROR || this == NETWORK_STATE_CHANGE
    }

    fun isValidState(): Boolean {
        return this != INIT && this != PING && this != PONG
    }

    fun case(s: String): ConnectionState {
        this.case = s
        return this
    }
}