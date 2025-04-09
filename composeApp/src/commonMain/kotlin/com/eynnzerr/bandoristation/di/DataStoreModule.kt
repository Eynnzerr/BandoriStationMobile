package com.eynnzerr.bandoristation.di

import com.eynnzerr.bandoristation.preferences.createDataStore
import org.koin.dsl.module

fun provideDataStoreModule() = module {
    single {
        createDataStore()
    }
}