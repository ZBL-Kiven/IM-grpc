package com.zj.ccIm.core

import android.app.Application
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException


object Comment {
    const val DELETE_OWNER_SESSION = "delete_owner_session"
    const val DELETE_FANS_SESSION = "delete_fans_session"
}

internal object Constance {

    lateinit var app: Application

    /**-------------------------- EVENT CODE -------------------------------------------*/
    const val FETCH_SESSION_CODE = "fetch_session"
    const val FETCH_OFFLINE_MSG_CODE = "fetch_offline_message"

    /**-------------------------- HINT MSG ------------------------------*/

    const val CONNECTION_RESET: String = "reconnection because the connection is already terminate!"

    const val CONNECTION_UNAVAILABLE: String = "reconnection because the connection is unavailable!"


    /**-------------------------- CALL IDS ------------------------------*/
    const val CALL_ID_CLEAR_SESSION_BADGE = "internal_clear_session_badge"

    const val INTERNAL_CALL_ID_PREFIX = "internal_call"
    const val CALL_ID_START_LISTEN_SESSION = INTERNAL_CALL_ID_PREFIX + "_start_listen_session"
    const val CALL_ID_START_LISTEN_TOTAL_DOTS = INTERNAL_CALL_ID_PREFIX + "_start_listen_totalDots"

    const val CALL_ID_START_LISTEN_PRIVATE_OWNER_CHAT = INTERNAL_CALL_ID_PREFIX + "_start_listen_privateOwnerChat"
    const val CALL_ID_SUBSCRIBE_NEW_TOPIC = INTERNAL_CALL_ID_PREFIX + "_subscribe_new_topic_type"

    const val CALL_ID_SUBSCRIBE_REMOVE_TOPIC = INTERNAL_CALL_ID_PREFIX + "_subscribe_remove_topic_type"
    const val CALL_ID_LEAVE_CHAT_ROOM = INTERNAL_CALL_ID_PREFIX + "_leave_chat_room"
    const val CALL_ID_REGISTER_CHAT = INTERNAL_CALL_ID_PREFIX + "_register_chat_room"

    const val CALL_ID_REGISTERED_CHAT = INTERNAL_CALL_ID_PREFIX + "_registered_chat_room"

    const val CALL_ID_DELETE_SESSION = INTERNAL_CALL_ID_PREFIX + "_delete_session"

    const val CALL_ID_GET_MESSAGES = INTERNAL_CALL_ID_PREFIX + "_get_message"
    const val CALL_ID_GET_OFFLINE_MESSAGES_SUCCESS = CALL_ID_GET_MESSAGES + "_offline_done"
    const val CALL_ID_GET_OFFLINE_CHAT_MESSAGES = CALL_ID_GET_MESSAGES + "_offline"
    const val CALL_ID_GET_MORE_MESSAGES = CALL_ID_GET_MESSAGES + "_more"

    /**-------------------------- TOPIC CHANNEL ------------------------------*/

    const val TOPIC_CONN_SUCCESS = "cc://im-rpc-req/ListenTopicData"
    const val TOPIC_MSG_REGISTRATION = "cc://im-rpc-req/GetImMessage"

    const val TOPIC_GROUP_INFO = "cc://group-info-topic"

    const val TOPIC_IM_MSG = "cc://im-msg-topic/"
    const val TOPIC_CHAT_OWNER_INFO = "cc://chat-owner-info-topic"
    const val TOPIC_CHAT_FANS_INFO = "cc://chat-fans-info-topic"

    /**-------------------------- SERVER EVENT CONSTANCE ------------------------------*/

    const val SEND_MSG_DEFAULT_TIMEOUT = 15000L
}

internal fun String.toMd5(): String {
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

internal fun <R> catching(run: () -> R?): R? {
    return try {
        run()
    } catch (e: Exception) {
        IMHelper.postError(e);null
    } catch (e: java.lang.Exception) {
        IMHelper.postError(e);null
    }
}

internal fun <R> catching(run: () -> R?, deal: (() -> Unit)? = null): R? {
    return try {
        run()
    } catch (e: Exception) {
        IMHelper.postError(e);deal?.invoke();null
    } catch (e: java.lang.Exception) {
        IMHelper.postError(e);deal?.invoke();null
    }
}

@Suppress("unused")
enum class MsgType(val type: String) {

    TEXT("text"), IMG("img"), AUDIO("audio"), VIDEO("video"), QUESTION("question");

    companion object {

        fun hasUploadType(type: String?): Boolean {
            return type == AUDIO.type || type == VIDEO.type || type == IMG.type
        }

        fun canRetryType(type: String?): Boolean {
            return type != QUESTION.type
        }

        fun canRetryWhenBootStartType(type: String?): Boolean {
            return type != QUESTION.type && !hasUploadType(type)
        }
    }
}