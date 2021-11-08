@file:Suppress("unused")

package com.zj.im.utils.log.logger

import android.app.Application
import com.zj.im.utils.now
import com.zj.im.utils.today

/**
 * Created by ZJJ
 *
 * IM status log collection utils
 *
 * collectionAble = auto
 * */
private val logUtils = object : LogCollectionUtils.Config() {

    override fun overriddenFolderName(folderName: String): String {
        return "$folderName/ConnectionStatus"
    }

    override val subPath: () -> String
        get() = { today() }
    override val fileName: () -> String
        get() = { now() }
}

private val errorCollector = object : LogCollectionUtils.Config() {

    override fun overriddenFolderName(folderName: String): String {
        return "$folderName/errorLogs"
    }

    override val subPath: () -> String
        get() = { today() }
    override val fileName: () -> String
        get() = { now() }
}

fun initLogCollectors(context: Application?, diskPathName: String, debugEnable: Boolean, logsCollectionAble: () -> Boolean, logsMaxRetain: Long) {
    logUtils.init(context, diskPathName, debugEnable, logsCollectionAble, logsMaxRetain)
    errorCollector.init(context, diskPathName, debugEnable, logsCollectionAble, logsMaxRetain)
    NetRecordUtils.init(context, diskPathName, debugEnable, logsCollectionAble, logsMaxRetain)
}

internal fun i(where: String, s: String) {
    logUtils.i(where, s)
}

internal fun e(where: String, s: String) {
    logUtils.e(where, s)
}

internal fun d(where: String, s: String) {
    logUtils.d(where, s)
}

internal fun w(where: String, s: String) {
    logUtils.w(where, s)
}

internal fun printInFile(where: String, s: String?, append: Boolean = true) {
    logUtils.printInFile(where, s, append)
}

internal fun printErrorInFile(where: String, s: String?, append: Boolean = true) {
    errorCollector.printErrorInFile(where, s, append)
}