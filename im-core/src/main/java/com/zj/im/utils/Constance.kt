package com.zj.im.utils

import com.zj.im.BuildConfig

internal object Constance {

    const val LOOPER_EFFICIENCY = "the looper change runtime efficiency to %s!"

    const val CONNECT_ERROR = "connection error , unable to connect to server!"

    const val LOG_FILE_NAME_EMPTY_ERROR = "must set a log path with open the log collectors!"

    const val FOLDER_NAME = "${BuildConfig.LIBRARY_PACKAGE_NAME}_IM"

    const val MAX_RETAIN_TCP_LOG = 5 * 24 * 60 * 60 * 1000L

    const val DEFAULT_TIMEOUT = 10000L

    const val DEFAULT_RECONNECT_TIME = 3000L
}
