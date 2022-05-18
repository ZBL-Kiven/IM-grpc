package com.zj.im.main.looper

import com.zj.im.utils.cusListOf

internal class MsgHandlerQueue {

    private val queue = cusListOf<MsgExecutor>()

    fun hasData(): Boolean {
        return queue.any { it.hasData() }
    }

    fun enqueue(executor: MsgExecutor) {
        queue.add(executor)
    }

    fun loopOnce(interval: Long) {
        queue.forEachSafely {
            val next = it.next()
            if (next.isDropped) {
                it.remove()
            }
            kotlin.runCatching {
                next.loop(interval)
            }
        }
    }
}