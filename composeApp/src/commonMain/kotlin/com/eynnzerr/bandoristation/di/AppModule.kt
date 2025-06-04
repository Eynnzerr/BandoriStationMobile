package com.eynnzerr.bandoristation.di

val appModule = listOf(
    provideDispatcherModule(),
    provideDataStoreModule(),
    provideJsonModule(),
    provideKtorClientModule(),
    provideWebSocketClientModule(),
    provideHttpsClientModule(),
    provideDatabaseModule(),
    provideRepositoryModule(),
    provideUseCaseModule(),
    provideViewModelModule(),
)