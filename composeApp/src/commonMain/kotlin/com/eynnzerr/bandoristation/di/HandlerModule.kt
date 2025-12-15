package com.eynnzerr.bandoristation.di

import com.eynnzerr.bandoristation.handler.CheckUnreadChatHandler
import com.eynnzerr.bandoristation.handler.ConnectionHandler
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

    single {
        CheckUnreadChatHandler(
            getWebSocketStateUseCase = get(),
            checkUnreadChatUseCase = get(),
            scope = get(named(DispatcherQualifiers.APPLICATION_SCOPE))
        )
    }
}
