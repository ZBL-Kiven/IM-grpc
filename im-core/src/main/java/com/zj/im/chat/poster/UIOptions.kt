package com.zj.im.chat.poster

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.zj.im.chat.interfaces.MessageInterface
import com.zj.im.utils.cast
import com.zj.im.utils.log.logger.d
import java.util.*
import kotlin.collections.ArrayList

internal class UIOptions<T : Any, R : Any, L : DataHandler<T, R>>(private val uniqueCode: Any, private val lifecycleOwner: LifecycleOwner? = null, private val creator: UIHelperCreator<T, R, L>, inObserver: (Class<R>) -> Unit, private val result: (R?, List<R>?, String?) -> Unit) : LifecycleObserver {

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
        inObserver(creator.outerCls)
    }

    private val pal = "payload"
    private val cag = "category"
    private val handleWhat = 0x1101
    private var dataHandler: DataHandler<T, R>? = null
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

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun destroy() {
        try {
            MessageInterface.removeAnObserver(this.getUnique())
            handler.removeCallbacksAndMessages(null)
            lifecycleOwner?.lifecycle?.removeObserver(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun post(cls: Class<*>?, data: Any?, ld: Collection<*>?, payload: String?): Boolean {
        if (cls == creator.inCls || cls?.simpleName == creator.inCls.simpleName) {
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
            val a: Any = d ?: (ls ?: return@run)
            postToMain(a, p, if (a == d) 0 else 1)
        }
    }

    private fun postToMain(data: Any, payload: String?, c: Int) {
        handler.sendMessage(Message.obtain().apply {
            what = handleWhat
            obj = data
            val b = Bundle()
            if (!payload.isNullOrEmpty()) b.putString(pal, payload)
            b.putInt(cag, c)
            this.data = b
        })
    }

    private fun run(data: T?, lst: Collection<T>?, payload: String?, finished: (R?, ArrayList<R>?, String?) -> Unit) {

        try {
            val ds = arrayListOf<T>()
            if (lst != null) ds.addAll(lst)
            if (data != null) ds.add(data)

            val handled = arrayListOf<R>()
            ds.forEach {
                postData(it, payload)?.let { r ->
                    handled.add(r)
                }
            }
            if (handled.isNullOrEmpty()) finished(null, null, payload) else {
                val hd = if (handled.size == 1) handled.first() else null
                val hds = if (handled.size > 1) handled else null
                finished(hd, hds, payload)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun postData(data: T, payload: String?): R? {
        return creator.filterIn?.let {
            if (it.invoke(data, payload)) postFilterInData(data, payload)
            else {
                log("the data $data may abandon with filter in")
                return@postData null
            }
        } ?: postFilterInData(data, payload)
    }

    private fun postFilterInData(data: T, payload: String?): R? {
        if (dataHandler == null) dataHandler = creator.handlerCls?.newInstance()
        val out: R = dataHandler?.handle(data) ?: castNotSafety(data)
        return postHandlerData(out, payload)
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
}

@Suppress("UNCHECKED_CAST")
@Throws(java.lang.ClassCastException::class, ClassCastException::class)
internal fun <I, O> castNotSafety(a: I): O {
    return a as O
}