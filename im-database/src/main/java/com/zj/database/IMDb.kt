package com.zj.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.zj.database.dao.*
import com.zj.database.entity.*

@Database(entities = [SessionInfoEntity::class, MessageInfoEntity::class, SendMessageReqEn::class, SessionLastMsgInfo::class, PrivateOwnerEntity::class, GroupInfoEntity::class,EmoticonInfo::class], version = 11, exportSchema = false)
abstract class IMDb : RoomDatabase() {

    abstract fun sessionDao(): SessionDao

    abstract fun messageDao(): MessageDao

    abstract fun sessionMsgDao(): SessionLastMessageDao

    abstract fun sendMsgDao(): SendMsgDao

    abstract fun privateChatOwnerDao(): PrivateChatOwnerDao

    abstract fun groupInfoDao(): GroupInfoDao

    abstract fun emoticonInfoDao(): EmoticonInfoDao
}