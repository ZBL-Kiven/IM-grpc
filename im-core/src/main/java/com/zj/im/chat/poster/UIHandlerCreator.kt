package com.zj.im.chat.poster

import androidx.lifecycle.LifecycleOwner

class UIHandlerCreator<T : Any, R : Any>(private val uniqueCode: Any, private val lifecycleOwner: LifecycleOwner? = null, private val inCls: Class<T>, private val outCls: Class<R>) {
    fun <L : DataHandler<T, R>> addHandler(cls: Class<L>): UIHelperCreator<T, R, L> {
        return UIHelperCreator(uniqueCode, lifecycleOwner, inCls, outCls, cls)
    }
}