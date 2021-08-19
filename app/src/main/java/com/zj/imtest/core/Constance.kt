package com.zj.imtest.core

import java.net.URL
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException


object Constance {
    /**-------------------------- HINT MSG ------------------------------*/

    const val CONNECTION_RESET: String = "reconnection because the connection is already terminate!"
    const val CONNECTION_UNAVAILABLE: String = "reconnection because the connection is unavailable!"
    const val PING_TIMEOUT = "reconnection because the ping was no response too many times!"

    /**-------------------------- CALL IDS ------------------------------*/
    const val INTERNAL_CALL_ID_PREFIX = "internal_call"
    const val CALL_ID_SUBSCRIBE_NEW_TOPIC = INTERNAL_CALL_ID_PREFIX + "_subscribe_new_topic_type"
    const val CALL_ID_SUBSCRIBE_REMOVE_TOPIC = INTERNAL_CALL_ID_PREFIX + "_subscribe_remove_topic_type"
    const val CALL_ID_LEAVE_CHAT_ROOM = INTERNAL_CALL_ID_PREFIX + "_leave_chat_room"
    const val CALL_ID_REGISTER_CHAT = INTERNAL_CALL_ID_PREFIX + "_register_chat_room"

    const val CALL_ID_GET_OFFLINE_MESSAGES = INTERNAL_CALL_ID_PREFIX + "_get_offline_"
    const val CALL_ID_GET_OFFLINE_CHAT_MESSAGES = CALL_ID_GET_OFFLINE_MESSAGES + "_chat_messages"
    const val CALL_ID_GET_OFFLINE_GROUP_MESSAGES = CALL_ID_GET_OFFLINE_MESSAGES + "_group_messages"


    /**-------------------------- TOPIC CHANNEL ------------------------------*/

    const val TOPIC_CONN_SUCCESS = "cc://im-rpc-req/ListenTopicData"
    const val TOPIC_MSG_REGISTRATION = "cc://im-rpc-req/GetImMessage"
    const val TOPIC_OWNER_GROUP_TOPIC = "cc://owner-topic"
    const val TOPIC_GROUP_INFO_TOPIC = "cc://group-info-topic"
    const val TOPIC_CC_USER = "cc://user" // in test

    /**-------------------------- SERVER EVENT CONSTANCE ------------------------------*/

    const val CONNECTION_EVENT = 0xf1378
    const val CONNECT_TYPE_TOPIC = 0xf1912
    const val CONNECT_TYPE_MESSAGE = 0xf1367
    const val RECONNECTION_TIME = 2000L
    const val RECONNECTION_TIME_5000 = 5000L
    const val CONNECTION_TIME_OUT = 3000

    const val HEART_BEATS_EVENT = 0xf1365
    const val HEART_BEATS_BASE_TIME = 5000L
    const val CALL_ID_HEART_BEATS = "call_heart_beats"

    /**-------------------------- SERVER EVENT CONSTANCE ------------------------------*/

    const val PRIMARY_LOCAL_ID_V = "message_primary_local_id_v_said"
    const val PRIMARY_LOCAL_ID_NORMAL = "message_primary_local_id_normal"

    fun getUserId(): Int {
        return 120539
    }

    fun getToken(): String {
        return "ZDU2OTk4ODctMGI1Yi00MjdmLWFhYTUtMTU1ODMxOWY3ZmVh"
    }

    fun getGrpcAddress(): URL {
        return URL("grpc.ccdev.lerjin.com:50003")
    }

    fun getIMHost(): String {
        return "https://im.ccdev.lerjin.com"
    }

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

    enum class MsgType {
        text, img, audio, video
    }
}