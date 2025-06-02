package com.eynnzerr.bandoristation.di

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.eynnzerr.bandoristation.data.local.AppDatabase
import kotlinx.coroutines.Dispatchers
import org.koin.dsl.module
import java.io.File

actual fun provideDatabaseModule() = module {
    single {
        val dbFile = File(System.getProperty("java.io.tmpdir"), "bandori_station.db")
        Room.databaseBuilder<AppDatabase>(name = dbFile.absolutePath)
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()
    }
}