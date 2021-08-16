package com.zj.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "sessions")
class SessionInfoEntity {

    /**
     * 群id
     */
    @PrimaryKey var groupId: Long = 0

    /**
     * 群名称
     */
    var groupName: String? = null

    /**
     * 群主id
     */
    var ownerId: Int = 0

    /**
     * 群成员数量
     */
    var groupMemberNum: Int = 0

    /**
     * logo
     */
    var logo: String? = null

    /**
     * 群公告
     */
    var description: String? = null

    /**
     * 免打扰状态 1免打扰 0正常
     */
    var disturbStatus: Int = 0

    /**
     * 群组状态 0正常 1停用
     */
    var groupStatus: Int = 0

}