package com.zj.ccIm.logger

import com.zj.im.utils.log.logger.getLogUtils

object ImLogs {

    fun requireToPrintInFile(where: String, log: String) {
        getLogUtils().printInFile(where, log, true)
    }

    fun i(where: String, log: String) {
        getLogUtils().i(where, log)
    }

    fun d(where: String, log: String) {
        getLogUtils().d(where, log)
    }

    fun e(where: String, log: String) {
        getLogUtils().e(where, log)
    }
}