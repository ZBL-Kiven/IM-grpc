package com.zj.ccIm.core

import android.app.Application
import com.zj.ccIm.CcIM
import com.zj.ccIm.error.InitializedException
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

object Comment {
    const val DELETE_OWNER_SESSION = 1
    const val DELETE_FANS_SESSION = 2
}

object ExtMsgType {
    const val EXTENDS_TYPE_RECALL = "revokeMsg"
    const val EXTENDS_TYPE_SENSITIVE_HINT = "riskMsg"
}

object ExtMsgKeys {
    const val SENSITIVE_OTHER = "other"
}

internal object Constance {
    var app: Application? = null
        get() {
            return if (field == null) {
                CcIM.postIMError(InitializedException("app"));null
            } else field
        }

    var dbId: Int? = null
        get() {
            return if (field == null) {
                CcIM.postIMError(InitializedException("dbId"));null
            } else field
        }

    /**-------------------------- EVENT CODE -------------------------------------------*/
    const val FETCH_SESSION_CODE = "fetch_session"
    const val FETCH_OFFLINE_MSG_CODE = "fetch_offline_message"

    /**-------------------------- CALL IDS ------------------------------*/

    const val INTERNAL_CALL_ID_PREFIX = "internal_call"
    const val CALL_ID_START_LISTEN_SESSION = INTERNAL_CALL_ID_PREFIX + "_start_listen_session"
    const val CALL_ID_START_LISTEN_TOTAL_DOTS = INTERNAL_CALL_ID_PREFIX + "_start_listen_totalDots"
    const val CALL_ID_START_LISTEN_PRIVATE_OWNER_CHAT = INTERNAL_CALL_ID_PREFIX + "_start_listen_privateOwnerChat"

    const val CALL_ID_SUBSCRIBE_NEW_TOPIC = INTERNAL_CALL_ID_PREFIX + "_subscribe_new_topic_type"

    const val CALL_ID_SUBSCRIBE_REMOVE_TOPIC = INTERNAL_CALL_ID_PREFIX + "_subscribe_remove_topic_type"
    const val CALL_ID_LEAVE_CHAT_ROOM = INTERNAL_CALL_ID_PREFIX + "_leave_chat_room_"
    const val CALL_ID_REGISTER_CHAT = INTERNAL_CALL_ID_PREFIX + "_register_chat_room_"

    const val CALL_ID_REGISTERED_CHAT = INTERNAL_CALL_ID_PREFIX + "_registered_chat_room_"
    const val CALL_ID_LEAVE_FROM_CHAT_ROOM = INTERNAL_CALL_ID_PREFIX + "_leave_from_chat_room_"

    const val CALL_ID_DELETE_SESSION = INTERNAL_CALL_ID_PREFIX + "_delete_session"

    const val CALL_ID_GET_MESSAGES = INTERNAL_CALL_ID_PREFIX + "_get_message"
    const val CALL_ID_GET_OFFLINE_MESSAGES_SUCCESS = CALL_ID_GET_MESSAGES + "_offline_done"
    const val CALL_ID_GET_OFFLINE_CHAT_MESSAGES = CALL_ID_GET_MESSAGES + "_offline"

    /**-------------------------- TOPIC CHANNEL ------------------------------*/

    const val TOPIC_CONN_SUCCESS = "cc://im-rpc-req/ListenTopicData"

    const val TOPIC_IM_MSG = "cc://im-msg-topic/"

    const val TOPIC_KICK_OUT = "cc://kick-out"

    const val TOPIC_GROUP_INFO = "cc://group-info-topic"

    const val TOPIC_CHAT_FANS_INFO = "cc://chat-fans-info-topic"

    const val TOPIC_CHAT_OWNER_INFO = "cc://chat-owner-info-topic"

    const val TOPIC_ROLE = "cc://group-role-topic"

    const val TOPIC_ASSETS_CHANGED = "cc://change-balance"

    const val TOPIC_LIVE_STATE = "cc://live-status-change/all"

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
        CcIM.postError(e);null
    } catch (e: java.lang.Exception) {
        CcIM.postError(e);null
    }
}

internal fun <R> catching(run: () -> R?, deal: (() -> R?)? = null): R? {
    return try {
        run()
    } catch (e: Exception) {
        CcIM.postError(e);deal?.invoke()
    } catch (e: java.lang.Exception) {
        CcIM.postError(e);deal?.invoke()
    }
}

/**
 * Local build type
 * */
enum class MessageType(val type: String) {
    SYSTEM("system"), MESSAGE("message")
}

/**
 * Local build type
 * */
enum class SystemMsgType(val type: String) {
    RECALLED("recall"), SENSITIVE("sensitive"), REFUSED("refuse")
}

/**
 * Server message type
 * */
@Suppress("unused")
enum class MsgType(val type: String) {

    TEXT("text"), IMG("img"), AUDIO("audio"), CC_VIDEO("cc_video"), VIDEO("video"), QUESTION("question"), LIVE("live"), EMOTION("emotion"), GIFT("gift");

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