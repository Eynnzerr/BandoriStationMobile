package com.eynnzerr.bandoristation.di

import com.eynnzerr.bandoristation.usecase.datastore.GetPreferenceUseCase
import com.eynnzerr.bandoristation.usecase.datastore.SetPreferenceUseCase
import com.eynnzerr.bandoristation.preferences.createDataStore
import org.koin.core.qualifier.named
import org.koin.dsl.module

fun provideDataStoreModule() = module {
    single {
        createDataStore()
    }

    single {
        SetPreferenceUseCase(
            dataStore = get(),
            dispatcher = get(named(DispatcherQualifiers.IO_DISPATCHER)),
        )
    }

    single(named("stringPreferenceUseCase")) {
        GetPreferenceUseCase<String>(
            dataStore = get(),
            dispatcher = get(named(DispatcherQualifiers.IO_DISPATCHER))
        )
    }

    single(named("intPreferenceUseCase")) {
        GetPreferenceUseCase<Int>(
            dataStore = get(),
            dispatcher = get(named(DispatcherQualifiers.IO_DISPATCHER))
        )
    }

    single(named("booleanPreferenceUseCase")) {
        GetPreferenceUseCase<Boolean>(
            dataStore = get(),
            dispatcher = get(named(DispatcherQualifiers.IO_DISPATCHER))
        )
    }

    single(named("stringSetPreferenceUseCase")) {
        GetPreferenceUseCase<Set<String>>(
            dataStore = get(),
            dispatcher = get(named(DispatcherQualifiers.IO_DISPATCHER))
        )
    }
}