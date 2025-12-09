package com.eynnzerr.bandoristation.di

import com.eynnzerr.bandoristation.ConnectionHandler
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

fun provideHandlerModule() = module {
    single {
        ConnectionHandler(
            getWebSocketStateUseCase = get(),
            setAccessPermissionUseCase = get(),
            setUpClientUseCase = get(),
            externalScope = get(named(DispatcherQualifiers.APPLICATION_SCOPE)),
        )
    }
}
