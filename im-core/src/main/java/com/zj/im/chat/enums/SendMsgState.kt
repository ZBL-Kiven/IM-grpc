package com.zj.im.chat.enums

/**
 * created by ZJJ
 *
 * the msg sending state
 * */

@Suppress("unused")
enum class SendMsgState(val type: Int) {

    TIME_OUT(-2), FAIL(-1), NONE(0), SENDING(1), ON_SEND_BEFORE_END(2), SUCCESS(3);

    private var specialBody: Any? = null

    internal fun setSpecialBody(specialBody: Any?): SendMsgState {
        this.specialBody = specialBody
        return this
    }

    internal fun peekSpecialBody(): Any? {
        return specialBody
    }

    fun getSpecialBody(): Any? {
        val sb = specialBody
        this.specialBody = null
        return sb
    }

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