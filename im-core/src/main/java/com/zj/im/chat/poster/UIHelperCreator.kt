package com.zj.im.chat.poster

import androidx.lifecycle.LifecycleOwner


@Suppress("unused", "MemberVisibilityCanBePrivate")
class UIHelperCreator<T : Any, R : Any, L : DataHandler<T>>(val uniqueCode: Any, private val lifecycleOwner: LifecycleOwner? = null, val inCls: Class<T>, val outerCls: Class<R>, val handlerCls: Class<L>?, val innerCls: Class<*>? = null, private val inObserver: ObserverIn) {

    var withData: Any? = null
    internal var filterIn: ((T, String?) -> Boolean)? = null
    internal var filterOut: ((R, String?) -> Boolean)? = null
    internal var ignoreNullData: Boolean = true
    internal var logAble = false
    private var onDataReceived: ((R?, List<R>?, String?) -> Unit)? = null
    private var isPaused: Boolean = false
    private val cacheData = hashSetOf<CacheData<R>>()
    private var options: UIOptions<T, R, L>? = null

    fun withData(withData: Any?): UIHelperCreator<T, R, L> {
        this.withData = withData
        return this
    }

    fun ignoreNullData(ignore: Boolean): UIHelperCreator<T, R, L> {
        this.ignoreNullData = ignore
        return this
    }

    fun filterIn(filter: (T, String?) -> Boolean): UIHelperCreator<T, R, L> {
        this.filterIn = filter
        return this
    }

    fun filterOut(filter: (R, String?) -> Boolean): UIHelperCreator<T, R, L> {
        this.filterOut = filter
        return this
    }

    fun log(): UIHelperCreator<T, R, L> {
        logAble = true;return this
    }

    fun listen(onDataReceived: (r: R?, lr: List<R>?, payload: String?) -> Unit): UIHelperCreator<T, R, L> {
        this.onDataReceived = onDataReceived
        options = UIOptions(uniqueCode, lifecycleOwner, this, inObserver) { d, s, pl ->
            cacheData.add(CacheData(d, s, pl))
            if (!isPaused) {
                notifyDataChanged()
            }
        }
        return this
    }

    private fun notifyDataChanged() {
        cacheData.forEach {
            onDataReceived?.invoke(it.d, it.lst, it.payload)
        }
        cacheData.clear()
    }

    fun pause() {
        isPaused = true
    }

    fun resume() {
        isPaused = false
        notifyDataChanged()
    }

    fun shutdown() {
        options?.destroy()
        cacheData.clear()
        options = null
        onDataReceived = null
        isPaused = false
        filterIn = null
        filterOut = null
    }
}