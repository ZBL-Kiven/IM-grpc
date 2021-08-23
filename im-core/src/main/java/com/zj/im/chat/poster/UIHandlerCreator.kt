package com.zj.im.chat.poster

class UIHandlerCreator<T : Any, R : Any>(private val uniqueCode: Any, private val inCls: Class<T>, private val outCls: Class<R>) {
    fun <L : DataHandler<T, R>> addHandler(cls: Class<L>): UIHelperCreator<T, R, L> {
        return UIHelperCreator(uniqueCode, inCls, outCls, cls)
    }
}