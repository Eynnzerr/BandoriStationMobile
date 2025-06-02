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
import com.eynnzerr.bandoristation.business.SetAccessPermissionUseCase
import com.eynnzerr.bandoristation.business.SetUpClientUseCase
import com.eynnzerr.bandoristation.business.UpdateTimestampUseCase
import com.eynnzerr.bandoristation.business.UploadRoomUseCase
import com.eynnzerr.bandoristation.business.account.BindQQUseCase
import com.eynnzerr.bandoristation.business.account.GetEditProfileDataUseCase
import com.eynnzerr.bandoristation.business.account.GetSelfInfoUseCase
import com.eynnzerr.bandoristation.business.account.GetUserInfoUseCase
import com.eynnzerr.bandoristation.business.account.LoginUseCase
import com.eynnzerr.bandoristation.business.account.LogoutUseCase
import com.eynnzerr.bandoristation.business.account.SendVerificationCodeUseCase
import com.eynnzerr.bandoristation.business.account.SignupUseCase
import com.eynnzerr.bandoristation.business.account.UpdateAccountAggregator
import com.eynnzerr.bandoristation.business.account.UpdateAvatarUseCase
import com.eynnzerr.bandoristation.business.account.UpdateBannerUseCase
import com.eynnzerr.bandoristation.business.account.UpdateIntroductionUseCase
import com.eynnzerr.bandoristation.business.account.UpdateUsernameUseCase
import com.eynnzerr.bandoristation.business.account.VerifyEmailUseCase
import com.eynnzerr.bandoristation.business.room.GetRoomFilterUseCase
import com.eynnzerr.bandoristation.business.room.RequestRecentRoomsUseCase
import com.eynnzerr.bandoristation.business.room.UpdateRoomFilterUseCase
import com.eynnzerr.bandoristation.business.social.FollowUserUseCase
import com.eynnzerr.bandoristation.business.social.GetFollowerBriefUseCase
import com.eynnzerr.bandoristation.business.social.GetFollowingBriefUseCase
import com.eynnzerr.bandoristation.business.social.InformUserUseCase
import com.eynnzerr.bandoristation.business.roomhistory.AddRoomHistoryUseCase
import com.eynnzerr.bandoristation.business.roomhistory.DeleteRoomHistoriesUseCase
import com.eynnzerr.bandoristation.business.roomhistory.DeleteRoomHistoryUseCase
import com.eynnzerr.bandoristation.business.roomhistory.EditRoomHistoryUseCase
import com.eynnzerr.bandoristation.business.roomhistory.FetchAllHistoryUseCase
import com.eynnzerr.bandoristation.business.roomhistory.RoomHistoryAggregator
import com.eynnzerr.bandoristation.business.websocket.ReceiveNoticeUseCase
import org.koin.core.module.dsl.singleOf
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
        )
    }

    single {
        ReceiveNoticeUseCase(
            repository = get(),
            dispatcher = get(named(DispatcherQualifiers.IO_DISPATCHER)),
        )
    }

    single {
        SetAccessPermissionUseCase(
            repository = get(),
            dispatcher = get(named(DispatcherQualifiers.IO_DISPATCHER)),
            dataStore = get()
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
            repository = get(),
            dataStore = get(),
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
        GetSelfInfoUseCase(
            repository = get(),
            dispatcher = get(named(DispatcherQualifiers.IO_DISPATCHER)),
        )
    }

    single {
        GetUserInfoUseCase(
            repository = get(),
            dispatcher = get(named(DispatcherQualifiers.IO_DISPATCHER)),
            dataStore = get(),
        )
    }

    single {
        LoginUseCase(
            repository = get(),
            dispatcher = get(named(DispatcherQualifiers.IO_DISPATCHER)),
            dataStore = get(),
        )
    }

    single {
        LogoutUseCase(
            repository = get(),
            dispatcher = get(named(DispatcherQualifiers.IO_DISPATCHER)),
            dataStore = get(),
        )
    }

    single {
        SignupUseCase(
            repository = get(),
            dispatcher = get(named(DispatcherQualifiers.IO_DISPATCHER)),
            dataStore = get(),
        )
    }

    single {
        SendVerificationCodeUseCase(
            repository = get(),
            dispatcher = get(named(DispatcherQualifiers.IO_DISPATCHER)),
            dataStore = get(),
        )
    }

    single {
        VerifyEmailUseCase(
            repository = get(),
            dispatcher = get(named(DispatcherQualifiers.IO_DISPATCHER)),
            dataStore = get(),
        )
    }

    single {
        FollowUserUseCase(
            repository = get(),
            dispatcher = get(named(DispatcherQualifiers.IO_DISPATCHER)),
            dataStore = get(),
        )
    }

    single {
        GetFollowerBriefUseCase(
            repository = get(),
            dispatcher = get(named(DispatcherQualifiers.IO_DISPATCHER)),
            dataStore = get(),
        )
    }

    single {
        GetFollowingBriefUseCase(
            repository = get(),
            dispatcher = get(named(DispatcherQualifiers.IO_DISPATCHER)),
            dataStore = get(),
        )
    }

    single {
        InformUserUseCase(
            repository = get(),
            dispatcher = get(named(DispatcherQualifiers.IO_DISPATCHER)),
            dataStore = get(),
        )
    }

    single {
        GetRoomFilterUseCase(
            repository = get(),
            dispatcher = get(named(DispatcherQualifiers.IO_DISPATCHER)),
            dataStore = get(),
        )
    }

    single {
        UpdateRoomFilterUseCase(
            repository = get(),
            dispatcher = get(named(DispatcherQualifiers.IO_DISPATCHER)),
            dataStore = get(),
        )
    }

    single {
        GetEditProfileDataUseCase(
            repository = get(),
            dispatcher = get(named(DispatcherQualifiers.IO_DISPATCHER)),
            dataStore = get(),
        )
    }

    single {
        RequestRecentRoomsUseCase(
            repository = get(),
            dispatcher = get(named(DispatcherQualifiers.IO_DISPATCHER)),
        )
    }

    // Account UseCases
    singleOf(::BindQQUseCase)
    singleOf(::UpdateAvatarUseCase)
    singleOf(::UpdateBannerUseCase)
    singleOf(::UpdateUsernameUseCase)
    singleOf(::UpdateIntroductionUseCase)
    singleOf(::UpdateAccountAggregator)

    // Room History UseCases
    singleOf(::FetchAllHistoryUseCase)
    singleOf(::AddRoomHistoryUseCase)
    singleOf(::EditRoomHistoryUseCase)
    singleOf(::DeleteRoomHistoryUseCase)
    singleOf(::DeleteRoomHistoriesUseCase)
    singleOf(::RoomHistoryAggregator)
}
