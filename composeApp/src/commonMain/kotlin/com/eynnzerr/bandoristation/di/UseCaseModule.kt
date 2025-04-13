package com.eynnzerr.bandoristation.di

import com.eynnzerr.bandoristation.business.CheckUnreadChatUseCase
import com.eynnzerr.bandoristation.business.ConnectWebSocketUseCase
import com.eynnzerr.bandoristation.business.DisconnectWebSocketUseCase
import com.eynnzerr.bandoristation.business.GetChatUseCase
import com.eynnzerr.bandoristation.business.GetRoomListUseCase
import com.eynnzerr.bandoristation.business.InitializeChatRoomUseCase
import com.eynnzerr.bandoristation.business.ReceiveHistoryChatUseCase
import com.eynnzerr.bandoristation.business.RequestHistoryChatUseCase
import com.eynnzerr.bandoristation.business.SendChatUseCase
import com.eynnzerr.bandoristation.business.SetUpClientUseCase
import com.eynnzerr.bandoristation.business.UpdateTimestampUseCase
import com.eynnzerr.bandoristation.business.UploadRoomUseCase
import com.eynnzerr.bandoristation.business.account.GetUserInfoUseCase
import org.koin.core.qualifier.named
import org.koin.dsl.module

fun provideUseCaseModule() = module {
    single {
        SetUpClientUseCase(
            repository = get(),
            dispatcher = get(named(DispatcherQualifiers.IO_DISPATCHER)),
        )
    }

    single {
        ConnectWebSocketUseCase(
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

    single {
        GetChatUseCase(
            repository = get(),
            dispatcher = get(named(DispatcherQualifiers.IO_DISPATCHER)),
        )
    }

    single {
        SendChatUseCase(
            repository = get(),
            dispatcher = get(named(DispatcherQualifiers.IO_DISPATCHER)),
        )
    }

    single {
        RequestHistoryChatUseCase(
            repository = get(),
            dispatcher = get(named(DispatcherQualifiers.IO_DISPATCHER)),
        )
    }

    single {
        ReceiveHistoryChatUseCase(
            repository = get(),
            dispatcher = get(named(DispatcherQualifiers.IO_DISPATCHER)),
        )
    }

    single {
        GetUserInfoUseCase(
            repository = get(),
            dispatcher = get(named(DispatcherQualifiers.IO_DISPATCHER)),
        )
    }
}