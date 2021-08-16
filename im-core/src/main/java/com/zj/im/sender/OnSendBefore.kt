package com.zj.im.sender

/**
 * Created by ZJJ
 */

interface OnSendBefore {
    fun call(onStatus: OnStatus)

}

interface OnStatus {
    fun call(callId: String, progress: Int, isOK: Boolean, isCancel: Boolean)
}