package com.eynnzerr.bandoristation.di

val appModule = listOf(
    provideDispatcherModule(),
    provideDataStoreModule(),
    provideJsonModule(),
    provideKtorClientModule(),
    provideWebSocketClientModule(),
    provideRepositoryModule(),
    provideUseCaseModule(),
    provideViewModelModule(),
)