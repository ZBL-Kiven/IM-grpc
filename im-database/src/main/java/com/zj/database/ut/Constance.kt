package com.zj.database.ut

object Constance {

    const val KEY_OF_PRIVATE_OWNER = "private_owner"
    const val KEY_OF_PRIVATE_FANS = "private_fans"
    const val KEY_OF_SESSIONS = "group"

    /**
     * the key set of , groupId in public chat , ownerId in private owner chat , -userId in private user chat.
     * */
    fun generateKeyByTypedIds(@LastMsgTabType type: String, id: Any): String {
        return "$type:$id"
    }
}