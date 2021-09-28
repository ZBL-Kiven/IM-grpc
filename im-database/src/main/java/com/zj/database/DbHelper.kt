package com.zj.database

import android.content.Context
import androidx.annotation.WorkerThread
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration

class DbHelper private constructor(context: Context) {

    var db: IMDb
    private var dbName = "clipClaps-im"

    private val migrations = arrayOf<Migration>()

    init {
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

    companion object {

        private var mInstance: DbHelper? = null

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