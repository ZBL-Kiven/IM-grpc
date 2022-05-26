package com.zj.im.chat.interfaces

import com.zj.im.chat.enums.ConnectionState
import com.zj.im.chat.modle.IMLifecycle
import com.zj.im.chat.poster.UIOptions
import com.zj.im.utils.MainLooper
import com.zj.im.utils.netUtils.NetWorkInfo
import com.zj.im.main.StatusHub
import com.zj.im.utils.log.NetWorkRecordInfo
import com.zj.im.utils.log.NetRecordChangedListener
import java.lang.RuntimeException
import java.util.concurrent.ConcurrentHashMap

abstract class MessageInterface<T> {

    @Suppress("unused")
    companion object {

        private var msgObservers = ConcurrentHashMap<Any, UIOptions<*, *, *>>()

        internal fun putAnObserver(option: UIOptions<*, *, *>) {
            if (!msgObservers.isNullOrEmpty()) {
                val last = msgObservers[option.getUnique()]
                if (last != null) {
                    option.hasPendingCount++
                }
            }
            msgObservers[option.getUnique()] = option
        }

        internal fun removeAnObserver(unique: Any): UIOptions<*, *, *>? {
            val info = msgObservers[unique] ?: return null
            return if (info.hasPendingCount > 0) {
                info.hasPendingCount--;null
            } else {
                msgObservers.remove(info)
            }
        }

        fun hasObserver(unique: Any): Boolean {
            return msgObservers.containsKey(unique)
        }

        /**
         * post a data into msg processor ,
         * only supported type Data or List<Data>
         * */
        internal fun postToUIObservers(cls: Class<*>?, data: Any?, payload: String? = null, onFinish: () -> Unit) {
            if (cls == null && data == null) {
                onFinish()
                return
            }
            val c: Class<*>?
            var d: Any? = null
            var ld: Collection<*>? = null
            if (data is Collection<*>) {
                c = data.firstOrNull()?.javaClass ?: cls
                ld = data
            } else {
                c = if (data != null) data::class.java else cls
                d = data
            }
            if (cls != null && cls != c) throw RuntimeException("The sent non-empty type [${c?.name}] is inconsistent with the declared type ${cls.name}!")
            msgObservers.forEach { (_, v) ->
                if (v.post(c, d, ld, payload)) {
                    v.log("the observer names ${v.getUnique()} and subscribe of ${v.getSubscribeClassName()}.class successful and received the data")
                }
            }
            onFinish()
        }
    }

    private val connectionStateObserver = ConcurrentHashMap<String, ((ConnectionState) -> Unit)>()

    fun registerConnectionStateChangeListener(name: String, observer: (ConnectionState) -> Unit) {
        this.connectionStateObserver[name] = observer
        MainLooper.post {
            observer(StatusHub.curConnectionState)
        }
    }

    fun removeConnectionStateChangeListener(name: String) {
        this.connectionStateObserver.remove(name)
    }

    internal fun onConnectionStatusChanged(state: ConnectionState) {
        MainLooper.post {
            connectionStateObserver.forEach { (_, p) ->
                p(state)
            }
        }
    }

    private val netWorkStateObserver = ConcurrentHashMap<String, ((NetWorkInfo) -> Unit)>()

    fun registerNetWorkStateChangeListener(name: String, observer: (NetWorkInfo) -> Unit) {
        this.netWorkStateObserver[name] = observer

    }

    fun removeNetWorkStateChangeListener(name: String) {
        this.netWorkStateObserver.remove(name)
    }

    internal fun onNetWorkStatusChanged(state: NetWorkInfo) {
        MainLooper.post {
            netWorkStateObserver.forEach { (_, p) ->
                p(state)
            }
        }
    }

    private var lifecycleListeners = ConcurrentHashMap<String, ((IMLifecycle) -> Unit)>()

    fun registerLifecycleListener(name: String, l: (IMLifecycle) -> Unit) {
        lifecycleListeners[name] = l
    }

    fun unRegisterLifecycleListener(name: String) {
        lifecycleListeners.remove(name)
    }

    internal fun onLifecycle(state: IMLifecycle) {
        lifecycleListeners.forEach { (_, v) ->
            MainLooper.post {
                try {
                    v(state)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private var changedListeners = ConcurrentHashMap<String, NetRecordChangedListener>()

    fun addRecordListener(name: String, tc: NetRecordChangedListener) {
        changedListeners[name] = tc
    }

    fun removeRecordListener(name: String) {
        changedListeners.remove(name)
    }

    internal open fun onRecordChange(info: NetWorkRecordInfo) {
        changedListeners.forEach { (_, v) ->
            MainLooper.post {
                v.onChanged(info)
            }
        }
    }
}