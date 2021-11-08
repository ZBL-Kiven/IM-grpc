package com.zj.ccIm.logger

import android.util.Log
import com.zj.ccIm.core.IMHelper

object ImLogs {

    fun recordLogsInFile(where: String, log: String, append: Boolean = true) {
        IMHelper.recordLogs(where, log, append)
    }

    fun recordErrorInFile(where: String, log: String, append: Boolean = true) {
        IMHelper.recordError(where, log, append)
    }

    fun d(where: String, log: String) {
        if (IMHelper.imConfig?.logAble() == true) {
            Log.d("com.zj.im-cc:", "\n from : $where:\n case:$log\n")
        }
    }
}