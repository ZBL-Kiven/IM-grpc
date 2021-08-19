package com.zj.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.zj.database.dao.MessageDao
import com.zj.database.dao.SessionDao
import com.zj.database.entity.MessageInfoEntity
import com.zj.database.entity.SessionInfoEntity

@Database(entities = [SessionInfoEntity::class, MessageInfoEntity::class], version = 1, exportSchema = false)
abstract class IMDb : RoomDatabase() {

    abstract fun sessionDao(): SessionDao

    abstract fun messageDao(): MessageDao

}