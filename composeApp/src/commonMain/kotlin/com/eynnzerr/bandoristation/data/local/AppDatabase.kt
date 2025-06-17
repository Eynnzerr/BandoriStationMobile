package com.eynnzerr.bandoristation.data.local

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import com.eynnzerr.bandoristation.data.local.dao.RoomHistoryDao
import com.eynnzerr.bandoristation.model.room.RoomHistory

@Database(
    version = 1,
    entities = [RoomHistory::class],
    exportSchema = true
)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase: RoomDatabase() {
    abstract fun getRoomHistoryDao(): RoomHistoryDao
}

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase> {
    override fun initialize(): AppDatabase
}