package com.eynnzerr.bandoristation.di

import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.eynnzerr.AppApplication
import com.eynnzerr.bandoristation.data.local.AppDatabase
import kotlinx.coroutines.Dispatchers
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

actual fun provideDatabaseModule() = module {
    single {
        Room.databaseBuilder<AppDatabase>(AppApplication.context, "bandori_station.db")
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()
    }
}
