package com.zj.im.chat.poster

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import com.zj.im.BuildConfig
import com.zj.im.chat.interfaces.MessageInterface
import java.util.*
import kotlin.collections.ArrayList

internal class UIOptions<T : Any, R : Any, L : DataHandler<T, R>>(private val uniqueCode: Any, private val creator: UIHelperCreator<T, R, L>, private val result: (R?, List<R>?, String?) -> Unit) {

    init {
        MessageInterface.putAnObserver(this)
    }

    private val pal = "payload"
    private val cag = "category"
    private val handleWhat = 0x1101
    private var dataHandler: DataHandler<T, R>? = null

    private val handler = Handler(Looper.getMainLooper()) {
        if (it.what == handleWhat) {
            val b = it.data
            val payload = if (b.containsKey(pal)) b.getString(pal) else null
            when (b.getInt(cag)) {
                0 -> {
                    castNotSafety<Any?, R?>(it.obj)?.let { r ->
                        result(r, null, payload)
                    } ?: println("the data ${it.obj} was handled but null result in cast transform")
                }
                1 -> {
                    castNotSafety<Any?, List<R>?>(it.obj)?.let { lst ->
                        result(null, lst, payload)
                    } ?: println("the data ${it.obj} was handled but null list result in cast transform")
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

    fun destroy() {
        try {
            MessageInterface.removeAnObserver(this)
            handler.removeCallbacksAndMessages(null)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun post(data: Any, payload: String?): Boolean {
        if (data is Collection<*>) {
            (data as? Collection<*>)?.let {
                it.firstOrNull()?.let { c ->
                    if (c.javaClass == creator.inCls) {
                        (data as? Collection<T>)?.let { co ->
                            postData(null, co, payload)
                            return true
                        }
                    }
                }
            }
        }
        if (data.javaClass == creator.inCls || data.javaClass.simpleName == creator.inCls.simpleName) {
            postData(data as? T?, null, payload)
            return true
        }
        return false
    }

    private fun postData(data: T?, lst: Collection<T>?, payload: String?) {
        if (data == null && lst.isNullOrEmpty()) {
            log("why are you post a null data and also register a type-null observer?");return
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
}

@Suppress("UNCHECKED_CAST")
@Throws(java.lang.ClassCastException::class, ClassCastException::class)
fun <I, O> castNotSafety(a: I): O {
    return a as O
}

fun log(str: String) {
    println("im-ui ----- $str")
}

fun debugLog(str: String) {
    if (BuildConfig.DEBUG) println("im-ui-debug ----- $str")
}