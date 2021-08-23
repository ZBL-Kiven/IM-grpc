package com.zj.im.chat.poster

import com.zj.im.chat.interfaces.MessageInterface

@Suppress("unused")
class UIHelperCreator<T : Any, R : Any, L : DataHandler<T, R>>(private val uniqueCode: Any, internal val inCls: Class<T>, internal val outerCls: Class<R>, internal val handlerCls: Class<L>?) {

    var filterIn: ((T, String?) -> Boolean)? = null
    var filterOut: ((R, String?) -> Boolean)? = null
    private var onDataReceived: ((R?, List<R>?, String?) -> Unit)? = null
    private var isPaused: Boolean = false
    private val cacheData = hashSetOf<CacheData<R>>()
    private var options: UIOptions<T, R, L>? = null

    fun filterIn(filter: (T, String?) -> Boolean): UIHelperCreator<T, R, L> {
        this.filterIn = filter
        return this
    }

    fun filterOut(filter: (R, String?) -> Boolean): UIHelperCreator<T, R, L> {
        this.filterOut = filter
        return this
    }

    fun listen(onDataReceived: (r: R?, lr: List<R>?, payload: String?) -> Unit) {
        this.onDataReceived = onDataReceived
        options = UIOptions(uniqueCode, this) { d, s, pl ->
            cacheData.add(CacheData(d, s, pl))
            if (!isPaused) {
                notifyDataChanged()
            }
        }
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
        MessageInterface.removeAnObserver(options)
        options?.destroy()
        cacheData.clear()
        options = null
        onDataReceived = null
        isPaused = false
        filterIn = null
        filterOut = null
    }
}