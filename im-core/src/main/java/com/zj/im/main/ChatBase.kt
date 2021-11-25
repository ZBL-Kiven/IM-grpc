@file:Suppress("unused")

package com.zj.im.main

import android.content.Intent
import android.os.Binder
import android.os.IBinder
import com.zj.im.main.dispatcher.DataReceivedDispatcher
import com.zj.im.utils.log.logger.NetRecordUtils
import com.zj.im.utils.log.logger.printInFile

/**
 * Created by ZJJ
 *
 * the internal base hub for IM SDK
 */

internal class ChatBase<T> : Runner<T>() {

    data class ConnectionBinder<T>(val service: ChatBase<T>) : Binder()

    override fun onBind(intent: Intent?): IBinder {
        return ConnectionBinder(this@ChatBase)
    }

    override fun initBase() {
        NetRecordUtils.addRecordListener {
            imi?.onRecordChange(it)
        }
        DataReceivedDispatcher.init(this)
    }

    fun onAppLayerChanged(isHidden: Boolean) {
        if (isHidden != StatusHub.isRunningInBackground) {
            StatusHub.isRunningInBackground = isHidden
            DataReceivedDispatcher.checkNetWork(isHidden)
        }
        catching {
            imi?.onAppLayerChanged(isHidden)
        }
        val changedNames = if (isHidden) {
            arrayOf("foreground", "background")
        } else {
            arrayOf("background", "foreground")
        }
        printInFile("BaseOption.onLayerChanged", "the running task changed from ${changedNames[0]} to ${changedNames[1]} ")
        imi?.getNotification()?.let {
            StatusHub.isRunningInBackground = if (isHidden) {
                startForeground(imi?.getSessionId() ?: -1, it)
                true
            } else {
                stopForeground(true)
                false
            }
        }
    }

    override fun shutDown() {
        catching { NetRecordUtils.removeRecordListener() }
        super.shutDown()
    }
}