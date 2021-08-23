package com.zj.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.zj.database.dao.MessageDao
import com.zj.database.dao.SendMsgDao
import com.zj.database.dao.SessionDao
import com.zj.database.dao.SessionLastMessageDao
import com.zj.database.entity.SessionLastMsgInfo
import com.zj.database.entity.MessageInfoEntity
import com.zj.database.entity.SendMessageReqEn
import com.zj.database.entity.SessionInfoEntity

@Database(entities = [SessionInfoEntity::class, MessageInfoEntity::class, SendMessageReqEn::class, SessionLastMsgInfo::class], version = 1, exportSchema = false)
abstract class IMDb : RoomDatabase() {

    abstract fun sessionDao(): SessionDao

    abstract fun messageDao(): MessageDao

    abstract fun sessionMsgDao(): SessionLastMessageDao

    abstract fun sendMsgDao(): SendMsgDao

}