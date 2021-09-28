@file:Suppress("unused")

package com.zj.im.utils.log.logger

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

fun getLogUtils(): LogCollectionUtils.Config {
    return logUtils
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

internal fun printInFile(where: String, s: String?) {
    logUtils.printInFile(where, s, true)
}