package com.zj.im.main.looper

import com.zj.im.chat.enums.ConnectionState
import com.zj.im.utils.log.logger.d
import java.util.concurrent.ConcurrentHashMap

internal class MsgExecutor(queue: MsgHandlerQueue, private val callback: (what: Int, obj: Any?) -> Unit) {

    private val messages = ConcurrentHashMap<Int, Message>()
    var isDropped = false

    init {
        queue.enqueue(this)
    }

    fun hasData(): Boolean {
        return if (isDropped) return false else messages.isNotEmpty()
    }

    fun contains(what: Int): Boolean {
        return if (isDropped) return false else messages.contains(what)
    }

    fun enqueue(what: Int, delay: Long = 0, obj: Any? = null) {
        if (isDropped) {
            d("MsgExecutor.enqueue", "failed to enqueue message because the MsgLooper has been dropped!")
        } else messages[what] = Message(delay, obj)
    }

    fun enqueue(connectionState: ConnectionState, delay: Long = 0) {
        enqueue(connectionState.code, delay, connectionState)
    }

    fun remove(connectionState: ConnectionState) {
        removeMessages(connectionState.code)
    }

    fun removeMessages(what: Int) {
        messages.remove(what)
    }

    fun loop(interval: Long) {
        val mMessages = arrayListOf<Pair<Int, Any?>>()
        val iterator = messages.entries.iterator()
        while (iterator.hasNext()) {
            val next = iterator.next()
            next.value.delay -= interval
            if (next.value.delay <= 0) {
                mMessages.add(Pair(next.key, next.value.obj))
                iterator.remove()
            }
        }
        mMessages.forEach {
            callback(it.first, it.second)
        }
    }

    fun clearAndDrop() {
        isDropped = true
        messages.clear()
    }
}