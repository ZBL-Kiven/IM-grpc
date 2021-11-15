package com.zj.database

import android.content.Context
import androidx.annotation.WorkerThread
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import com.zj.database.sp.SPHelper
import com.zj.database.ut.Constance

class DbHelper private constructor(context: Context) {

    var db: IMDb
    private var dbName = "clipClaps-im"

    private val migrations = arrayOf<Migration>()

    init {
        SPHelper.init("im_sp_main", context)
        val builder = Room.databaseBuilder(context, IMDb::class.java, dbName)
        builder.setJournalMode(RoomDatabase.JournalMode.TRUNCATE)
        builder.allowMainThreadQueries()
        if (migrations.isNotEmpty()) {
            builder.addMigrations()
        } else {
            builder.fallbackToDestructiveMigration()
        }
        db = builder.build()
    }

    fun checkDbVersion(context: Context) {
        val last = SPHelper[Constance.SP_LAST_DB_VERSION, 0] ?: 0
        if (last != curDbVersion) {
            SPHelper.clear()
        }
        SPHelper.put(Constance.SP_LAST_DB_VERSION, curDbVersion)
    }

    fun getFetchSessionTs(): Long {
        return SPHelper[Constance.SP_FETCH_SESSIONS_TS, 0L] ?: 0L
    }


    fun putFetchSessionTs(newTs: Long) {
        SPHelper.put(Constance.SP_FETCH_SESSIONS_TS, newTs)
    }

    fun getFetchPrivateOwnerSessionTs(): Long {
        return SPHelper[Constance.SP_FETCH_PRIVATE_OWNER_CHAT_SESSIONS_TS, 0L] ?: 0L
    }

    fun putFetchPrivateOwnerSessionTs(newTs: Long) {
        SPHelper.put(Constance.SP_FETCH_PRIVATE_OWNER_CHAT_SESSIONS_TS, newTs)
    }

    companion object {

        private var mInstance: DbHelper? = null
        const val curDbVersion = 5

        @WorkerThread
        fun get(context: Context): DbHelper? {
            if (mInstance == null) {
                synchronized(DbHelper::class.java) {
                    if (mInstance == null) {
                        mInstance = DbHelper(context)
                    }
                }
            }
            return mInstance
        }
    }
}