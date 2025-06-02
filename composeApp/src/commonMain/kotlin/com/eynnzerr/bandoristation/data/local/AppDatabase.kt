package com.eynnzerr.bandoristation.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.eynnzerr.bandoristation.data.local.dao.RoomHistoryDao
import com.eynnzerr.bandoristation.model.RoomHistory

@Database(
    version = 1,
    entities = [RoomHistory::class],
    exportSchema = true
)
abstract class AppDatabase: RoomDatabase() {
    abstract fun getRoomHistoryDao(): RoomHistoryDao
}