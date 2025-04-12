package com.eynnzerr.bandoristation.di

import com.eynnzerr.bandoristation.feature.chat.ChatViewModel
import com.eynnzerr.bandoristation.feature.home.HomeViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

fun provideViewModelModule() = module {
    factory {
        HomeViewModel(
            connectWebSocketUseCase = get(),
            setUpClientUseCase = get(),
            disconnectWebSocketUseCase = get(),
            getRoomListUseCase = get(),
            updateTimestampUseCase = get(),
            checkUnreadChatUseCase = get(),
            uploadRoomUseCase = get(),
            setPreferenceUseCase = get(),
            stringSetPreferenceUseCase = get(named("stringSetPreferenceUseCase"))
        )
    }
    // viewModelOf(::HomeViewModel)
    viewModelOf(::ChatViewModel)
}