package com.zj.im.chat.interfaces

import com.zj.im.chat.enums.ConnectionState
import com.zj.im.chat.modle.IMLifecycle
import com.zj.im.chat.poster.UIOptions
import com.zj.im.utils.MainLooper
import com.zj.im.utils.netUtils.NetWorkInfo
import com.zj.im.main.StatusHub
import com.zj.im.utils.log.NetWorkRecordInfo
import com.zj.im.utils.log.NetRecordChangedListener
import com.zj.im.utils.log.logger.d
import java.util.concurrent.ConcurrentHashMap

abstract class MessageInterface<T> {

    @Suppress("unused")
    companion object {

        private var msgObservers: ConcurrentHashMap<Any, UIOptions<*, *, *>>? = null
            get() {
                if (field == null) field = ConcurrentHashMap()
                return field
            }

        internal fun putAnObserver(option: UIOptions<*, *, *>?) {
            if (option == null) return
            msgObservers?.put(option.getUnique(), option)
        }

        internal fun hasObserver(unique: Any): Boolean {
            return msgObservers?.contains(unique) ?: false
        }

        internal fun removeAnObserver(unique: Any): UIOptions<*, *, *>? {
            return msgObservers?.remove(unique)
        }

        /**
         * post a data into msg processor ,
         * only supported type Data or List<Data>
         * */
        internal fun postToUIObservers(data: Any?, payload: String? = null, onFinish: () -> Unit) {
            if (data == null) {
                onFinish()
                return
            }
            var isUsed = false
            msgObservers?.forEach { (_, v) ->
                if (v.post(data, payload)) {
                    isUsed = true
                    d("postToUIObservers", "the observer names ${v.getUnique()} and subscribe of ${v.getSubscribeClassName()}.class successful and received the data")
                }
            }
            onFinish()
            val cls = if (data is Collection<*>) data.firstOrNull()?.javaClass else data.javaClass
            if (!isUsed) d("postToUIObservers", "the data ${cls?.simpleName}.class has been abandon with none consumer")
        }
    }

    private var connectionStateObserver: MutableMap<String, ((ConnectionState) -> Unit)?>? = null
        get() {
            if (field == null) field = mutableMapOf()
            return field
        }

    fun registerConnectionStateChangeListener(name: String, observer: (ConnectionState) -> Unit) {
        this.connectionStateObserver?.put(name, observer)
        MainLooper.post {
            observer(StatusHub.curConnectionState)
        }
    }

    fun removeConnectionStateChangeListener(name: String) {
        this.connectionStateObserver?.remove(name)
    }

    internal fun onConnectionStatusChanged(state: ConnectionState) {
        MainLooper.post {
            connectionStateObserver?.forEach { (_, p) ->
                p?.invoke(state)
            }
        }
    }

    private var netWorkStateObserver: MutableMap<String, ((NetWorkInfo) -> Unit)?>? = null
        get() {
            if (field == null) field = mutableMapOf()
            return field
        }

    fun registerNetWorkStateChangeListener(name: String, observer: (NetWorkInfo) -> Unit) {
        this.netWorkStateObserver?.put(name, observer)

    }

    fun removeNetWorkStateChangeListener(name: String) {
        this.netWorkStateObserver?.remove(name)
    }

    internal fun onNetWorkStatusChanged(state: NetWorkInfo) {
        MainLooper.post {
            netWorkStateObserver?.forEach { (_, p) ->
                p?.invoke(state)
            }
        }
    }

    private var lifecycleListeners: HashMap<String, (IMLifecycle) -> Unit>? = null
        get() {
            if (field == null) field = hashMapOf()
            return field
        }

    fun registerLifecycleListener(name: String, l: (IMLifecycle) -> Unit) {
        lifecycleListeners?.put(name, l)
    }

    fun unRegisterLifecycleListener(name: String) {
        lifecycleListeners?.remove(name)
    }

    internal fun onLifecycle(state: IMLifecycle) {
        lifecycleListeners?.forEach { (_, v) ->
            MainLooper.post {
                try {
                    v(state)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private var changedListeners: HashMap<String, NetRecordChangedListener>? = null
        get() {
            if (field == null) field = hashMapOf()
            return field
        }

    fun addRecordListener(name: String, tc: NetRecordChangedListener) {
        changedListeners?.put(name, tc)
    }

    fun removeRecordListener(name: String) {
        changedListeners?.remove(name)
    }

    internal open fun onRecordChange(info: NetWorkRecordInfo) {
        changedListeners?.forEach { (_, v) ->
            MainLooper.post {
                v.onChanged(info)
            }
        }
    }
}