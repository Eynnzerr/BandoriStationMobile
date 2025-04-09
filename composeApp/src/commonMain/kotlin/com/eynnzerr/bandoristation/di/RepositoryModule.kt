package com.eynnzerr.bandoristation.di

import com.eynnzerr.bandoristation.data.AppRepository
import com.eynnzerr.bandoristation.data.remote.RemoteDataSource
import com.eynnzerr.bandoristation.data.remote.RemoteDataSourceImpl
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

fun provideRepositoryModule() = module {
    // singleOf(::LocalDataSourceImpl).bind(LocalDataSource::class)
    singleOf(::RemoteDataSourceImpl).bind(RemoteDataSource::class)
    singleOf(::AppRepository)
}