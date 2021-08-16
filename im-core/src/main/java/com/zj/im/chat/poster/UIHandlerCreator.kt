package com.zj.im.chat.poster

class UIHandlerCreator<T : Any, R : Any>(private val uniqueCode: Any, private val inCls: Class<T>, private val outCls: Class<R>) {
    fun addHandler(cls: Class<DataHandler<T, R>>): UIHelperCreator<T, R> {
        return UIHelperCreator(uniqueCode, inCls, outCls, cls)
    }
}