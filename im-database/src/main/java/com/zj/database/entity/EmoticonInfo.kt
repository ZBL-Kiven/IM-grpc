package com.zj.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * @author: JayQiu
 * @date: 2021/12/10
 * @description:
 */
@Entity(tableName = "emoticonInfo")
class EmoticonInfo {
    @PrimaryKey var id: Int = 0
    var icon: String? = ""
    var url: String? = ""
    var packId: Int? = 0
}