package com.zj.database.entity

import androidx.room.Entity
import androidx.room.Ignore
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
     * 群主名称
     */
    var ownerName: String? = null

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
     * 消息是否置顶 1 = 置顶， 0 非置顶
     * */
    var top: Int = 0

    /**
     * 群组状态 0正常 1停用 3取关
     */
    var groupStatus: Int = 0

    /**
     * 总收入
     * */
    var income: Int = 0

    /**
     * 问题数
     * */
    var questionNum: Int = 0

    /**
     * 当前在群里的角色 0 普通人， 2 管理员
     * */
    var role: Int = 0

    /**
     * 忽略数据库字段，由推送时拼装
     * */
    @Ignore var sessionMsgInfo: SessionLastMsgInfo? = null

    fun updateConfigs(disturbType: Int?, top: Int?, groupName: String?, des: String?) {
        if (disturbType != null) this.disturbStatus = disturbType
        if (top != null) this.top = top
        if (groupName != null) this.groupName = groupName
        if (des != null) this.description = des
    }

}