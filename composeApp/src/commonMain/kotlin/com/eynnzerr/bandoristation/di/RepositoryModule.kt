package com.eynnzerr.bandoristation.di

import com.eynnzerr.bandoristation.data.AppRepository
import com.eynnzerr.bandoristation.data.local.LocalDataSource
import com.eynnzerr.bandoristation.data.local.LocalDataSourceImpl
import com.eynnzerr.bandoristation.data.remote.RemoteDataSource
import com.eynnzerr.bandoristation.data.remote.RemoteDataSourceImpl
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module

fun provideRepositoryModule() = module {
    singleOf(::LocalDataSourceImpl).bind(LocalDataSource::class)
    single {
        RemoteDataSourceImpl(
            webSocketClient = get(named("BandoriStationWS")),
            encryptionSocketClient = get(named("BandoriscriptionWS")),
            httpsClient = get()
        )
    }.bind(RemoteDataSource::class)
    singleOf(::AppRepository)
}