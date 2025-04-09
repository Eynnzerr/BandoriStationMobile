package com.eynnzerr.bandoristation.di

import com.eynnzerr.bandoristation.business.CheckUnreadChatUseCase
import com.eynnzerr.bandoristation.business.DisconnectWebSocketUseCase
import com.eynnzerr.bandoristation.business.GetRoomListUseCase
import com.eynnzerr.bandoristation.business.InitializeChatRoomUseCase
import com.eynnzerr.bandoristation.business.SetUpClientUseCase
import com.eynnzerr.bandoristation.business.UpdateTimestampUseCase
import com.eynnzerr.bandoristation.business.UploadRoomUseCase
import org.koin.core.qualifier.named
import org.koin.dsl.module

fun provideUseCaseModule() = module {
    single {
        SetUpClientUseCase(
            repository = get(),
            dispatcher = get(named(DispatcherQualifiers.IO_DISPATCHER)),
            dataStore = get(),
        )
    }

    single {
        DisconnectWebSocketUseCase(
            repository = get(),
            dispatcher = get(named(DispatcherQualifiers.MAIN_DISPATCHER))
        )
    }

    single {
        GetRoomListUseCase(
            repository = get(),
            dispatcher = get(named(DispatcherQualifiers.IO_DISPATCHER)),
        )
    }

    single {
        UpdateTimestampUseCase(
            dispatcher = get(named(DispatcherQualifiers.IO_DISPATCHER)),
        )
    }

    single {
        InitializeChatRoomUseCase(
            repository = get(),
            dispatcher = get(named(DispatcherQualifiers.IO_DISPATCHER)),
        )
    }

    single {
        CheckUnreadChatUseCase(
            repository = get(),
            dispatcher = get(named(DispatcherQualifiers.IO_DISPATCHER)),
        )
    }

    single {
        UploadRoomUseCase(
            repository = get(),
            dispatcher = get(named(DispatcherQualifiers.IO_DISPATCHER)),
        )
    }
}