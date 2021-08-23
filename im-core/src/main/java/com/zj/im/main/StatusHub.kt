package com.zj.im.main

import com.zj.im.chat.enums.LifeType
import com.zj.im.chat.enums.ConnectionState
import com.zj.im.chat.modle.IMLifecycle
import com.zj.im.main.dispatcher.DataReceivedDispatcher

internal object StatusHub {

    var isRunningInBackground = false

    var isReceiving = false

    var curConnectionState: ConnectionState = ConnectionState.INIT

    private var lifeType = IMLifecycle(LifeType.START, "")
        set(value) {
            if (field != value) {
                field = value
                DataReceivedDispatcher.onLifeStateChanged(field)
            }
        }

    fun isRunning(): Boolean {
        return lifeType.type == LifeType.RESUME
    }

    fun isAlive(): Boolean {
        return lifeType.type != LifeType.STOP
    }

    fun isPaused(): Boolean {
        return lifeType.type == LifeType.PAUSE
    }

    fun onLifecycle(state: IMLifecycle) {
        lifeType = state
    }
}