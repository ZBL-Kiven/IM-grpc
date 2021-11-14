package com.zj.ccIm.logger

import android.util.Log
import com.zj.ccIm.CcIM

object ImLogs {

    fun recordLogsInFile(where: String, log: String, append: Boolean = true) {
        CcIM.recordLogs(where, log, append)
    }

    fun recordErrorInFile(where: String, log: String, append: Boolean = true) {
        CcIM.recordError(where, log, append)
    }

    fun d(where: String, log: String) {
        if (CcIM.imConfig?.logAble() == true) {
            Log.d("com.zj.im-cc:", "\n from : $where:\n case:$log\n")
        }
    }
}