package com.zj.imtest.core


object Constance {

    const val CONNECTION_RESET: String = "reconnection because the connection is already terminate!"
    const val CONNECTION_UNAVAILABLE: String = "reconnection because the connection is unavailable!"
    const val PING_TIMEOUT = "reconnection because the ping was no response too many times!"

    const val INTERNAL_CALL_ID_PREFIX = "internal_call"
    const val CALL_ID_SUBSCRIBE_NEW_TOPIC = INTERNAL_CALL_ID_PREFIX + "_subscribe_new_topic_type"
    const val CALL_ID_SUBSCRIBE_REMOVE_TOPIC = INTERNAL_CALL_ID_PREFIX + "_subscribe_remove_topic_type"
    const val CALL_ID_LEAVE_CHAT_ROOM = INTERNAL_CALL_ID_PREFIX + "_leave_chat_room"
    const val CALL_ID_REGISTER_CHAT = INTERNAL_CALL_ID_PREFIX + "_register_chat_room"
    const val CALL_ID_GET_OFFLINE_CHAT_MESSAGES = INTERNAL_CALL_ID_PREFIX + "_get_offline_chat_messages"
    const val CALL_ID_GET_OFFLINE_GROUP_MESSAGES = INTERNAL_CALL_ID_PREFIX + "_get_offline_group_messages"


    const val TOPIC_CONN_SUCCESS = "cc://im-rpc-req/ListenTopicData"
    const val TOPIC_MSG_REGISTRATION = "cc://im-rpc-req/GetImMessage"
    const val TOPIC_CC_USER = "cc://user"


    const val CONNECTION_EVENT = 0xf1378
    const val CONNECT_TYPE_TOPIC = 0xf1912
    const val CONNECT_TYPE_MESSAGE = 0xf1367
    const val RECONNECTION_TIME = 2000L
    const val RECONNECTION_TIME_5000 = 5000L
    const val CONNECTION_TIME_OUT = 3000

    const val HEART_BEATS_EVENT = 0xf1365
    const val HEART_BEATS_BASE_TIME = 5000L
    const val CALL_ID_HEART_BEATS = "call_heart_beats"

}