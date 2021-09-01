package com.zj.ccIm.core

import android.app.Application
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException


internal object Constance {

    lateinit var app: Application

    /**-------------------------- HINT MSG ------------------------------*/

    const val CONNECTION_RESET: String = "reconnection because the connection is already terminate!"
    const val CONNECTION_UNAVAILABLE: String = "reconnection because the connection is unavailable!"
    const val PING_TIMEOUT = "reconnection because the ping was no response too many times!"

    /**-------------------------- CALL IDS ------------------------------*/
    const val CALL_ID_CLEAR_SESSION_BADGE = "internal_clear_session_badge"


    const val INTERNAL_CALL_ID_PREFIX = "internal_call"
    const val CALL_ID_START_LISTEN_SESSION = INTERNAL_CALL_ID_PREFIX + "_start_listen_session"
    const val CALL_ID_SUBSCRIBE_NEW_TOPIC = INTERNAL_CALL_ID_PREFIX + "_subscribe_new_topic_type"
    const val CALL_ID_SUBSCRIBE_REMOVE_TOPIC = INTERNAL_CALL_ID_PREFIX + "_subscribe_remove_topic_type"
    const val CALL_ID_LEAVE_CHAT_ROOM = INTERNAL_CALL_ID_PREFIX + "_leave_chat_room"
    const val CALL_ID_REGISTER_CHAT = INTERNAL_CALL_ID_PREFIX + "_register_chat_room"
    const val CALL_ID_REGISTERED_CHAT = INTERNAL_CALL_ID_PREFIX + "_registered_chat_room"
    const val CALL_ID_GET_OFFLINE_MESSAGES = INTERNAL_CALL_ID_PREFIX + "_get_offline"
    const val CALL_ID_GET_OFFLINE_CHAT_MESSAGES = CALL_ID_GET_OFFLINE_MESSAGES + "_chat_messages"
    const val CALL_ID_GET_OFFLINE_GROUP_MESSAGES = CALL_ID_GET_OFFLINE_MESSAGES + "_group_messages"


    /**-------------------------- TOPIC CHANNEL ------------------------------*/

    const val TOPIC_CONN_SUCCESS = "cc://im-rpc-req/ListenTopicData"
    const val TOPIC_MSG_REGISTRATION = "cc://im-rpc-req/GetImMessage"
    const val TOPIC_IM_MSG = "cc://im-msg-topic/"
    const val TOPIC_GROUP_INFO = "cc://group-info-topic"

    /**-------------------------- SERVER EVENT CONSTANCE ------------------------------*/

    const val CONNECTION_EVENT = 0xf1378
    const val CONNECT_TYPE_TOPIC = 0xf1912
    const val CONNECT_TYPE_MESSAGE = 0xf1367
    const val RECONNECTION_TIME = 2000L
    const val RECONNECTION_TIME_5000 = 5000L
    const val SEND_MSG_DEFAULT_TIMEOUT = 15000L

    const val HEART_BEATS_EVENT = 0xf1365
    const val HEART_BEATS_BASE_TIME = 5000L

    /**-------------------------- EVENT CODE -------------------------------------------*/
    const val FETCH_SESSION_CODE = "fetch_session"
    const val FETCH_OFFLINE_MSG_CODE = "fetch_offline_message"

    fun String.toMd5(): String {
        try {
            val instance: MessageDigest = MessageDigest.getInstance("MD5")
            val digest: ByteArray = instance.digest(toByteArray())
            val sb = StringBuffer()
            for (b in digest) {
                val i: Int = b.toInt() and 0xff
                var hexString = Integer.toHexString(i)
                if (hexString.length < 2) {
                    hexString = "0$hexString"
                }
                sb.append(hexString)
            }
            return sb.toString()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }
        return ""
    }
}

@Suppress("unused")
enum class MsgType(val type: String) {

    TEXT("text"), IMG("img"), AUDIO("audio"), VIDEO("video"), QUESTION("question");

    companion object {

        fun hasUploadType(type: String?): Boolean {
            return type == AUDIO.type || type == VIDEO.type || type == IMG.type
        }
    }

}