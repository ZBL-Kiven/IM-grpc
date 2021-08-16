package com.zj.im.chat.enums

/**
 * created by ZJJ
 *
 * the msg sending state
 * */

@Suppress("unused")
enum class SendMsgState(val type: Int) {
    TIME_OUT(-2), FAIL(-1), NONE(0), SENDING(1), ON_SEND_BEFORE_END(2), SUCCESS(3);

    companion object {
        fun parseStateByType(type: Int?): SendMsgState? {
            var state: SendMsgState? = null
            values().forEach {
                if (it.type == type) {
                    state = it
                    return@forEach
                }
            }
            return state
        }
    }
}