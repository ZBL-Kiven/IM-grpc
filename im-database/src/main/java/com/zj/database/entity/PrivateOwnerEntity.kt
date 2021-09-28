package com.zj.database.entity

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "private_owner")
class PrivateOwnerEntity {

    /**
     * 群id
     */
    @PrimaryKey var groupId: Long? = null

    var ownerId: Int? = null

    /**
     * 群主名称
     */
    var ownerName: String? = null

    /**
     * 头像
     */
    var avatar: String? = null

    /**
     * 性别
     */
    var gender: Int? = null

    /**
     * clap code
     * */
    var code: String? = null

    /**
     * 忽略数据库字段，由推送时拼装
     * */
    @Ignore var sessionMsgInfo: SessionLastMsgInfo? = null
}