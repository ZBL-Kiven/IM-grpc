package com.zj.database

import android.content.Context
import androidx.annotation.WorkerThread
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import com.zj.database.sp.SPHelper
import com.zj.database.ut.Constance

@Suppress("unused")
class DbHelper private constructor(context: Context, dbId: Int) {

    private var db: IMDb
    private var dbName = "clipClaps-im:$dbId"

    private val migrations = arrayOf<Migration>()

    init {
        SPHelper.init("im_sp_main:$dbId", context)
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

    fun getDb(): IMDb {
        return db
    }

    fun checkDbVersion() {
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

    fun clearSessionFetchingTs() {
        SPHelper.put(Constance.SP_FETCH_SESSIONS_TS, 0)
    }

    fun clearPrivateOwnerSessionFetchingTs() {
        SPHelper.put(Constance.SP_FETCH_PRIVATE_OWNER_CHAT_SESSIONS_TS, 0)
    }

    fun clearAll() {
        SPHelper.clear()
        db.clearAllTables()
    }

    companion object {

        private var mInstance: DbHelper? = null
        const val curDbVersion = 5

        @WorkerThread
        fun get(context: Context, dbId: Int): DbHelper? {
            if (mInstance == null) {
                synchronized(DbHelper::class.java) {
                    if (mInstance == null) {
                        mInstance = DbHelper(context, dbId)
                    }
                }
            }
            return mInstance
        }
    }
}