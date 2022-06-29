package com.zj.im.chat.poster

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import androidx.lifecycle.*
import com.zj.im.chat.interfaces.MessageInterface
import com.zj.im.chat.modle.RouteInfo
import com.zj.im.utils.cast
import com.zj.im.utils.log.logger.d

internal class UIOptions<T : Any, R : Any, L : DataHandler<T>>(private val uniqueCode: Any, private val lifecycleOwner: LifecycleOwner? = null, private val creator: UIHelperCreator<T, R, L>, private val inObserver: ObserverIn, private val result: (R?, List<R>?, String?) -> Unit) : LifecycleEventObserver {

    init {
        lifecycleOwner?.let {
            try {
                if (MessageInterface.hasObserver(uniqueCode)) {
                    MessageInterface.removeAnObserver(uniqueCode)?.let { old ->
                        it.lifecycle.removeObserver(old)
                    }
                }
                it.lifecycle.addObserver(this)
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        MessageInterface.putAnObserver(this)
        inObserver.onObserverRegistered(creator)
    }

    private val pal = "payload"
    private val cag = "category"
    private val handleWhat = 0x1101
    private var dataHandler: DataHandler<T>? = null

    /**Compatible call order is affected by LifecycleOwner * */
    internal var hasPendingCount = 0

    private val handler = Handler(Looper.getMainLooper()) {
        if (it.what == handleWhat) {
            val b = it.data
            val payload = if (b.containsKey(pal)) b.getString(pal) else null
            when (b.getInt(cag)) {
                0 -> {
                    castNotSafety<Any?, R?>(it.obj)?.let { r ->
                        result(r, null, payload)
                    } ?: log("the data ${it.obj} was handled but null result in cast transform")
                }
                1 -> {
                    castNotSafety<Any?, List<R>?>(it.obj)?.let { lst ->
                        result(null, lst, payload)
                    } ?: log("the data ${it.obj} was handled but null list result in cast transform")
                }
                2 -> {
                    result(null, null, payload)
                }
            }
        }
        return@Handler false
    }

    override fun equals(other: Any?): Boolean {
        if (other !is UIOptions<*, *, *>) return false
        return other.uniqueCode == uniqueCode
    }

    override fun hashCode(): Int {
        return uniqueCode.hashCode()
    }

    fun getSubscribeClassName(): String {
        return creator.inCls.simpleName
    }

    fun getUnique(): Any {
        return uniqueCode
    }

    fun post(cls: Class<*>?, data: Any?, ld: Collection<*>?, payload: String?): Boolean {
        fun sameClass(c: Class<*>?, c1: Class<*>?): Boolean {
            if (c == null || c1 == null) return false
            return c == c1 || c.simpleName == c1.simpleName
        }

        val dataIsRouter = sameClass(cls, RouteInfo::class.java)
        val creatorIsRouter = sameClass(creator.inCls, RouteInfo::class.java)
        val isRouteHandle = if (dataIsRouter && creatorIsRouter) {
            sameClass((data as? RouteInfo<*>)?.data?.javaClass, creator.innerCls)
        } else false
        val canHandle = !creatorIsRouter && sameClass(cls, creator.inCls)
        if (isRouteHandle || canHandle) {
            postData(cls, cast(data), cast(ld), payload)
            return true
        }
        return false
    }

    private fun postData(cls: Class<*>?, data: T?, lst: Collection<T>?, payload: String?) {
        if (creator.ignoreNullData && data == null && lst.isNullOrEmpty()) {
            log("the null data with type [${cls?.name}] are ignored by filter ,you can receive it by set ignoreNullData(false) in your Observer.")
            return
        }
        run(data, lst, payload) { d, ls, p ->
            val a: Any? = d ?: ls
            postToMain(a, p, if (a == null) 2 else if (a == d) 0 else 1)
        }
    }

    private fun postToMain(data: Any?, payload: String?, c: Int) {
        handler.sendMessage(Message.obtain().apply {
            what = handleWhat
            obj = data
            val b = Bundle()
            if (!payload.isNullOrEmpty()) b.putString(pal, payload)
            b.putInt(cag, c)
            this.data = b
        })
    }

    private fun run(data: T?, lst: Collection<T>?, payload: String?, finished: (R?, List<R?>?, String?) -> Unit) {
        try {
            data?.let {
                postData(it, payload).forEach { d ->
                    finished(d, null, payload)
                }
            }
            lst?.let { l ->
                val dsh = mutableListOf<R?>()
                l.forEach {
                    dsh.addAll(postData(it, payload))
                }
                finished(null, dsh, payload)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun postData(data: T, payload: String?): List<R> {
        return creator.filterIn?.let {
            if (it.invoke(data, payload)) postFilterInData(data, payload)
            else {
                log("the data $data may abandon with filter in")
                return@postData mutableListOf()
            }
        } ?: postFilterInData(data, payload)
    }

    private fun postFilterInData(data: T, payload: String?): List<R> {
        if (dataHandler == null) dataHandler = creator.handlerCls?.newInstance()
        val o = dataHandler?.handle(data)
        val os = mutableListOf<R?>()
        if (o != null && o is Collection<*>) {
            o.forEach {
                val o1 = parseAnyObj(it, data, payload)
                if (o1 != null) os.add(o1)
            }
        } else {
            os.add(parseAnyObj(o, data, payload))
        }
        return os.mapNotNull { postHandlerData(it, payload) }
    }

    private fun parseAnyObj(o: Any?, data: T, payload: String?): R? {
        val out: R? = kotlin.runCatching { castNotSafety<Any, R>(o ?: data) }.getOrNull()
        return if ((o != null && out == null) || (out != null && out::class.java != creator.outerCls)) {
            (out ?: o)?.let {
                MessageInterface.postToUIObservers(it::class.java, it, payload) {}
            }
            null
        } else {
            out
        }
    }

    private fun postHandlerData(data: R?, payload: String?): R? {
        return (creator.filterOut?.let {
            if (data != null && it.invoke(data, payload)) data
            else {
                log("the data $data may abandon with filter out")
                return@postHandlerData null
            }
        } ?: data)
    }

    internal fun log(str: String) {
        if (creator.logAble) d("im-ui", str)
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        if (event == Lifecycle.Event.ON_DESTROY) destroy()
    }

    internal fun destroy() {
        try {
            MessageInterface.removeAnObserver(this.getUnique())
            inObserver.onObserverUnRegistered(creator)
            handler.removeCallbacksAndMessages(null)
            lifecycleOwner?.lifecycle?.removeObserver(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

@Suppress("UNCHECKED_CAST")
@Throws(java.lang.ClassCastException::class, ClassCastException::class)
internal fun <I, O> castNotSafety(a: I): O {
    return a as O
}