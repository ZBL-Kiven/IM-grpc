package com.zj.im.chat.core

import com.zj.im.main.StatusHub
import com.zj.im.chat.modle.BaseMsgInfo
import com.zj.im.chat.modle.MessageHandleType
import com.zj.im.utils.CustomList
import com.zj.im.utils.cusListOf

/**
 * created by ZJJ
 * the message queue for sdk
 * thread-safety list will pop their top  by their priority
 * */
internal class DataStore<T> {

    //PRI = 0
    private val netWorkStateChanged = cusListOf<BaseMsgInfo<T>>()

    //PRI = 4
    private val sendMsg = cusListOf<BaseMsgInfo<T>>()

    //PRI = 2
    private val connectStateChanged = cusListOf<BaseMsgInfo<T>>()

    //PRI = 3
    private val sendStateChanged = cusListOf<BaseMsgInfo<T>>()

    //PRI = 5
    private val receivedMsg = cusListOf<BaseMsgInfo<T>>()

    //PRI = 1
    private val simpleStatusFound = cusListOf<BaseMsgInfo<T>>()

    //PRI = 6
    private val sendingProgress = cusListOf<BaseMsgInfo<T>>()

    fun put(info: BaseMsgInfo<T>): Int {
        when (info.type) {
            MessageHandleType.ROUTE_CLIENT, MessageHandleType.ROUTE_SERVER -> {
                simpleStatusFound.addOrSet(info) { o ->
                    info.callId == o.callId
                }
            }
            MessageHandleType.CONNECT_STATE -> {
                connectStateChanged.addOnly(info)
            }
            MessageHandleType.SEND_MSG -> {
                if (info.ignoreStateCheck) simpleStatusFound.add(info) else {
                    val index = if (info.joinInTop) 0 else -1
                    sendMsg.addIf(info, index = index) { `in`, other ->
                        `in`.callId != other.callId
                    }
                    sendMsg.sort { it.createdTs }
                }
            }
            MessageHandleType.RECEIVED_MSG -> {
                if (info.ignoreStateCheck) simpleStatusFound.add(info)
                else receivedMsg.add(info)
            }
            MessageHandleType.SEND_STATE_CHANGE -> {
                sendStateChanged.add(info)
            }
            MessageHandleType.NETWORK_STATE -> {
                netWorkStateChanged.addOnly(info)
            }
            MessageHandleType.SEND_PROGRESS_CHANGED -> {
                sendingProgress.addOnly(info)
            }
            MessageHandleType.LAYER_CHANGED -> {
                simpleStatusFound.add(info)
            }
        }
        return getTotal()
    }

    fun pop(): BaseMsgInfo<T>? {

        when {
            /**
             * when network status changed
             */
            netWorkStateChanged.isNotEmpty() -> {
                return getFirst(netWorkStateChanged) { it, lst ->
                    lst.remove(it)
                }
            }
            /**
             * when connection status changed
             */
            connectStateChanged.isNotEmpty() -> {
                return getFirst(connectStateChanged) { it, lst ->
                    lst.remove(it)
                }
            }
            /**
             * when heartbeats / auth received
             * */
            simpleStatusFound.isNotEmpty() -> {
                return getFirst(simpleStatusFound) { it, lst ->
                    lst.remove(it)
                }
            }
            /**
             * when sending progress changed
             * */
            sendingProgress.isNotEmpty() -> {
                return getFirst(sendingProgress) { _, lst ->
                    lst.clear()
                }
            }
            /**
             * when some msg send status changed
             */
            sendStateChanged.isNotEmpty() -> {
                return getFirst(sendStateChanged) { it, lst ->
                    lst.remove(it)
                }
            }
            /**
             * when send a msg
             */
            isSending() && sendMsg.isNotEmpty() -> {
                return getFirst(sendMsg) { it, lst ->
                    lst.remove(it)
                }
            }
            /**
             * when receive something
             */
            isReceiving() && receivedMsg.isNotEmpty() -> {
                return getFirst(receivedMsg) { it, lst ->
                    StatusHub.isReceiving = true
                    lst.remove(it)
                }
            }
        }
        return null
    }

    private fun <T> getFirst(lst: CustomList<T>, before: (T, CustomList<T>) -> Unit): T? {
        return lst.getFirst()?.apply {
            before.invoke(this, lst)
        }
    }

    private var isSending: () -> Boolean = { true }
    private var isReceiving: () -> Boolean = { true }

    fun canSend(isSending: () -> Boolean) {
        this.isSending = isSending
    }

    fun canReceive(isReceiving: () -> Boolean) {
        this.isReceiving = isReceiving
    }

    fun queryInMsgQueue(predicate: (BaseMsgInfo<T>) -> Boolean): Boolean {
        return sendMsg.contains(predicate)
    }

    fun deleteFormQueue(callId: String?) {
        callId?.let {
            sendMsg.removeIf { m ->
                m.callId == callId
            }
        }
    }

    private fun getTotal(): Int {
        return try {
            netWorkStateChanged.count + sendMsg.count + connectStateChanged.count + sendStateChanged.count + receivedMsg.count + simpleStatusFound.count + sendingProgress.count
        } catch (e: Exception) {
            0
        }
    }

    fun shutDown() {
        sendMsg.clear()
        connectStateChanged.clear()
        sendStateChanged.clear()
        receivedMsg.clear()
    }
}
