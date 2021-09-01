package com.zj.imUi.items

import com.zj.imUi.interfaces.ImMsgIn

interface ImContentIn {

    fun onSetData(data: ImMsgIn?)

    fun onResume(data: ImMsgIn?)

    fun onStop(data: ImMsgIn?)

    fun onDestroy(data: ImMsgIn?)
}