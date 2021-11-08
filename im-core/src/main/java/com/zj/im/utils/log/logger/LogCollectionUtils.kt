package com.zj.im.utils.log.logger

import android.app.Application
import android.util.Log
import com.zj.im.utils.Constance
import com.zj.im.utils.nio
import com.zj.im.utils.now
import com.zj.im.utils.today
import java.io.File
import java.lang.Exception
import java.lang.NullPointerException

/**
 * Created by ZJJ
 */

@Suppress("unused")
sealed class LogCollectionUtils {

    companion object {
        const val TAG = "com.zj.im-core:%s"
    }

    private var subPath: () -> String = { today() }
    private var fileName: () -> String = { now() }
    private var debugEnable: Boolean = false
    private var collectionAble: () -> Boolean = { false }

    protected fun init(appContext: Application?, folderName: String, subPath: () -> String, fileName: () -> String, debugEnable: Boolean, collectionAble: (() -> Boolean)?, maxRetain: Long) {
        fileUtils = FileUtils.init(appContext, folderName)
        this.subPath = subPath
        this.fileName = fileName
        this.debugEnable = debugEnable
        this.collectionAble = collectionAble ?: { false }
        if (removeAble()) removeOldFiles(maxRetain)
    }

    private var fileUtils: FileUtils? = null

    private fun getTag(what: String?) = String.format(TAG, what)

    fun i(where: String, s: String?) {
        if (debugEnable) {
            Log.i(getTag(ErrorType.I.errorName), getLogText(where, s))
        }
    }

    fun d(where: String, s: String?) {
        if (debugEnable) {
            Log.d(getTag(ErrorType.D.errorName), getLogText(where, s))
        }
    }

    fun w(where: String, s: String?) {
        if (debugEnable) {
            Log.w(getTag(ErrorType.W.errorName), getLogText(where, s))
        }
    }

    fun e(where: String, s: String?) {
        if (debugEnable) {
            Log.e(getTag(ErrorType.E.errorName), getLogText(where, s))
        }
    }

    fun printInFile(where: String, s: String?, append: Boolean) {
        val type = ErrorType.D
        val txt = getLogText(where, s)
        if (debugEnable) {
            Log.d(getTag(type.errorName), txt)
        }
        if (collectionAble()) {
            onLogCollection(type, txt, append)
        }
    }

    fun printErrorInFile(where: String, s: String?, append: Boolean) {
        val type = ErrorType.E
        val txt = getLogText(where, s)
        if (debugEnable) {
            Log.d(getTag(type.errorName), txt)
        }
        if (collectionAble()) {
            onLogCollection(type, txt, append)
        }
    }

    fun getLogFile(path: String, name: String): File? {
        return fileUtils?.getFile(path, name)
    }

    fun getCollectionFolder(): File? {
        return fileUtils?.getHomePathFile()
    }

    fun getLogText(logFile: File?): String? {
        return fileUtils?.getTxt(logFile)
    }

    private fun getLogText(where: String, s: String?): String {
        return "\n from : $where:\n case:$s\n"
    }

    private fun onLogCollection(type: ErrorType, log: String?, append: Boolean = true) {
        fileUtils?.save(subPath(), fileName(), " \n type:${type.errorName} : on  ${nio()}:$log ", append)
    }

    protected fun write(what: String?) {
        fileUtils?.save(subPath(), fileName(), what ?: "", false)
    }

    open fun removeAble(): Boolean {
        return true
    }

    private fun removeOldFiles(maxRetain: Long) {
        try {
            fileUtils?.getHomePathFile()?.let { file ->
                if (!file.isDirectory) return
                val paths = arrayListOf<String>()
                file.listFiles()?.forEach {
                    if (System.currentTimeMillis() - it.lastModified() > maxRetain) {
                        paths.add(file.path)
                    }
                }
                paths.forEach { fileUtils?.deleteFolder(it) }
            }
        } catch (e: Exception) {
            e("remove5DaysAgoLogFiles", "error case : ${e.message}")
        }
    }

    private enum class ErrorType(var errorName: String?) {
        E("ERROR"), D("DEBUG"), W("WARMING"), I("INFO")
    }

    abstract class Config : LogCollectionUtils() {
        private var collectionAble: (() -> Boolean)? = null
        private var maxRetain: Long = 0
        private var debugEnable: Boolean = false
        abstract val subPath: () -> String
        abstract val fileName: () -> String

        open fun overriddenFolderName(folderName: String): String {
            return folderName
        }

        open fun prepare() {}

        /**
         * must call init() before use
         * */
        fun init(appContext: Application?, folderName: String, debugEnable: Boolean, collectionAble: () -> Boolean, logsMaxRetain: Long) {
            if (collectionAble.invoke() && folderName.isEmpty()) {
                throw NullPointerException(Constance.LOG_FILE_NAME_EMPTY_ERROR)
            }
            this.collectionAble = collectionAble
            this.maxRetain = logsMaxRetain
            this.debugEnable = debugEnable
            initConfig(appContext, overriddenFolderName(folderName))
        }

        private fun initConfig(appContext: Application?, folderName: String) {
            super.init(appContext, folderName, subPath, fileName, debugEnable, collectionAble, maxRetain)
            prepare()
        }
    }
}
