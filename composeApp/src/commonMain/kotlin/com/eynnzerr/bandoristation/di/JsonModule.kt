package com.eynnzerr.bandoristation.di

import kotlinx.serialization.json.Json
import org.koin.dsl.module

fun provideJsonModule() = module {
    single {
        Json {
            isLenient = true
            ignoreUnknownKeys = true
            coerceInputValues = true
        }
    }
}