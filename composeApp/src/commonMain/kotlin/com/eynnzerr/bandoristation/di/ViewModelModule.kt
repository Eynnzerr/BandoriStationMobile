package com.eynnzerr.bandoristation.di

import com.eynnzerr.bandoristation.feature.account.AccountViewModel
import com.eynnzerr.bandoristation.feature.chat.ChatViewModel
import com.eynnzerr.bandoristation.feature.home.HomeViewModel
import com.eynnzerr.bandoristation.feature.settings.SettingViewModel
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
            stringSetPreferenceUseCase = get(named("stringSetPreferenceUseCase")),
            receiveNoticeUseCase = get(),
            informUserUseCase = get(),
            updateRoomFilterUseCase = get(),
            getRoomFilterUseCase = get(),
            boolPreferenceUseCase = get(named("booleanPreferenceUseCase")),
            setAccessPermissionUseCase = get(),
            requestRecentRoomsUseCase = get(),
        )
    }

    viewModelOf(::ChatViewModel)

    factory {
        AccountViewModel(
            loginUseCase = get(),
            getSelfInfoUseCase = get(),
            stringPreferenceUseCase = get(named("stringPreferenceUseCase")),
            setPreferenceUseCase = get(),
            logoutUseCase = get(),
            signupUseCase = get(),
            sendVerificationCodeUseCase = get(),
            verifyEmailUseCase = get(),
            setAccessPermissionUseCase = get(),
            getFollowingBriefUseCase = get(),
            getFollowerBriefUseCase = get(),
            getEditProfileDataUseCase = get(),
            followUserUseCase = get(),
            updateAccountAggregator = get(),
        )
    }

    viewModelOf(::SettingViewModel)
}