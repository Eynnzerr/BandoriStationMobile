package com.eynnzerr.bandoristation.di

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import org.koin.core.qualifier.named
import org.koin.dsl.module

fun provideDispatcherModule() = module {
    single<CoroutineDispatcher>(named(DispatcherQualifiers.DEFAULT_DISPATCHER)) {
        Dispatchers.Default
    }

    single<CoroutineDispatcher>(named(DispatcherQualifiers.IO_DISPATCHER)) {
        Dispatchers.IO
    }

    single<CoroutineDispatcher>(named(DispatcherQualifiers.MAIN_DISPATCHER)) {
        Dispatchers.Main
    }
}

object DispatcherQualifiers {
    const val DEFAULT_DISPATCHER = "defaultDispatcher"
    const val IO_DISPATCHER = "ioDispatcher"
    const val MAIN_DISPATCHER = "mainDispatcher"
}