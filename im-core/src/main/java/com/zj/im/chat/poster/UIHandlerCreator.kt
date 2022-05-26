package com.zj.im.chat.poster

import androidx.lifecycle.LifecycleOwner

@Suppress("unused")
class UIHandlerCreator<T : Any, R : Any>(private val uniqueCode: Any, private val lifecycleOwner: LifecycleOwner? = null, private val inCls: Class<T>, private val outCls: Class<R>, private val innerCls: Class<*>? = null, private val inObserver: ObserverIn) {

    fun <L : DataHandler<T, R>> addHandler(cls: Class<L>): UIHelperCreator<T, R, L> {
        return UIHelperCreator(uniqueCode, lifecycleOwner, inCls, outCls, cls, innerCls, inObserver)
    }

    fun build(): UIHelperCreator<T, R, DataHandler<T, R>> {
        return UIHelperCreator(uniqueCode, lifecycleOwner, inCls, outCls, null, innerCls, inObserver)
    }
}