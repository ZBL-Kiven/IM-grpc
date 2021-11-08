package com.zj.im.chat.exceptions

/**
 * created by ZJJ
 *
 * base of chat exception
 * */
open class IMException(case: String?, private val body: Any? = null, @ErrorLevel val errorLevel: String = ERROR_LEVEL_ALERT) : Throwable(case) {

    companion object {
        const val ERROR_LEVEL_ALERT = "alert"
        const val ERROR_LEVEL_DEADLY = "deadly"
        const val ERROR_LEVEL_REINSTALL = "reinstall"
    }

    @Suppress("unused")
    fun getBodyData(): Any? {
        return body
    }
}
