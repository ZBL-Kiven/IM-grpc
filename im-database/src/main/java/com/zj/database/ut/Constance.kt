package com.zj.database.ut

import java.lang.IllegalArgumentException

object Constance {

    const val KEY_OF_PRIVATE_OWNER = "private_owner"
    const val KEY_OF_PRIVATE_FANS = "private_fans"
    const val KEY_OF_SESSIONS = "group"

    const val SP_FETCH_SESSIONS_TS = "fetch_last_modify_ts"
    const val SP_FETCH_PRIVATE_OWNER_CHAT_SESSIONS_TS = "fetch_last_modify_owner_ts"
    const val SP_LAST_DB_VERSION = "get_last_db_version"

    /**
     * the key set of , groupId in public chat , ownerId in private owner chat , -userId in private user chat.
     * */
    private fun generateKeyByTypedIds(@LastMsgTabType type: String, id: Any): String {
        return "$type:$id"
    }

    fun generateKey(@LastMsgTabType type: String, groupId: Long = -1, ownerId: Int = -1, userId: Int = -1): String {
        return when (type) {
            KEY_OF_PRIVATE_FANS -> generateKeyByTypedIds(type, userId)
            KEY_OF_PRIVATE_OWNER -> generateKeyByTypedIds(type, ownerId)
            KEY_OF_SESSIONS -> generateKeyByTypedIds(type, groupId)
            else -> throw IllegalArgumentException("no such type-value can compile this key!")
        }
    }
}