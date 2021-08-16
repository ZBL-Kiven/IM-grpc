package com.zj.im.utils

import android.os.Handler
import android.os.Looper

/**
 * created by ZJJ
 *
 * Main looper running in main thread
 * */
internal object MainLooper : Handler(Looper.getMainLooper())
