package com.zj.database.entity

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey


@Entity(tableName = "groupStatus")
class GroupInfoEntity {

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
     * 群组状态 0正常 1停用 3取关
     */
    var groupStatus: Int? = 0

    /**
     * 公告时间
     */
    var noticeTime: Long? = 0

    /**
     * 群公告
     */
    var description: String? = null

    /**
     * 活动时间
     */
    var activityTime: Long? = 0


}