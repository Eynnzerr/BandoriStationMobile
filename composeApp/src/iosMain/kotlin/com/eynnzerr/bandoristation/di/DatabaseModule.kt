package com.eynnzerr.bandoristation.di

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.eynnzerr.bandoristation.data.local.AppDatabase
import com.eynnzerr.bandoristation.utils.PathUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import org.koin.dsl.module

actual fun provideDatabaseModule() = module {
    single {
        val dbFilePath = PathUtils.getDocumentDirectory() + "/bandori_station.db"
        Room.databaseBuilder<AppDatabase>(name = dbFilePath)
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()
    }
}