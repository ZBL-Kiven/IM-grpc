package com.zj.im.sender

/**
 * Created by ZJJ
 */

interface OnSendBefore<T> {

    fun call(onStatus: OnStatus<T>)

}

