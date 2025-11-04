package com.eynnzerr.bandoristation.di

import com.eynnzerr.bandoristation.usecase.chat.CheckUnreadChatUseCase
import com.eynnzerr.bandoristation.usecase.websocket.GetWebSocketStateUseCase
import com.eynnzerr.bandoristation.usecase.websocket.DisconnectWebSocketUseCase
import com.eynnzerr.bandoristation.usecase.chat.GetChatUseCase
import com.eynnzerr.bandoristation.usecase.room.GetRoomListUseCase
import com.eynnzerr.bandoristation.usecase.chat.InitializeChatRoomUseCase
import com.eynnzerr.bandoristation.usecase.chat.ReceiveHistoryChatUseCase
import com.eynnzerr.bandoristation.usecase.chat.RequestHistoryChatUseCase
import com.eynnzerr.bandoristation.usecase.chat.SendChatUseCase
import com.eynnzerr.bandoristation.usecase.SetAccessPermissionUseCase
import com.eynnzerr.bandoristation.usecase.SetUpClientUseCase
import com.eynnzerr.bandoristation.usecase.time.UpdateTimestampUseCase
import com.eynnzerr.bandoristation.usecase.room.UploadRoomUseCase
import com.eynnzerr.bandoristation.usecase.account.BindQQUseCase
import com.eynnzerr.bandoristation.usecase.account.GetEditProfileDataUseCase
import com.eynnzerr.bandoristation.usecase.account.GetSelfInfoUseCase
import com.eynnzerr.bandoristation.usecase.account.GetUserInfoUseCase
import com.eynnzerr.bandoristation.usecase.account.LoginUseCase
import com.eynnzerr.bandoristation.usecase.account.LogoutUseCase
import com.eynnzerr.bandoristation.usecase.account.SendVerificationCodeUseCase
import com.eynnzerr.bandoristation.usecase.account.SignupUseCase
import com.eynnzerr.bandoristation.usecase.account.UpdateAccountAggregator
import com.eynnzerr.bandoristation.usecase.account.UpdateAvatarUseCase
import com.eynnzerr.bandoristation.usecase.account.UpdateBannerUseCase
import com.eynnzerr.bandoristation.usecase.account.UpdateEmailSendVCodeUseCase
import com.eynnzerr.bandoristation.usecase.account.UpdateEmailVerifyEmailUseCase
import com.eynnzerr.bandoristation.usecase.account.UpdateIntroductionUseCase
import com.eynnzerr.bandoristation.usecase.account.UpdatePasswordUseCase
import com.eynnzerr.bandoristation.usecase.account.UpdateUsernameUseCase
import com.eynnzerr.bandoristation.usecase.account.VerifyEmailUseCase
import com.eynnzerr.bandoristation.usecase.account.ResetPasswordSendVCodeUseCase
import com.eynnzerr.bandoristation.usecase.account.ResetPasswordVerifyEmailUseCase
import com.eynnzerr.bandoristation.usecase.account.ResetPasswordUseCase
import com.eynnzerr.bandoristation.usecase.room.GetRoomFilterUseCase
import com.eynnzerr.bandoristation.usecase.room.RequestRecentRoomsUseCase
import com.eynnzerr.bandoristation.usecase.room.UpdateRoomFilterUseCase
import com.eynnzerr.bandoristation.usecase.social.FollowUserUseCase
import com.eynnzerr.bandoristation.usecase.social.GetFollowerBriefUseCase
import com.eynnzerr.bandoristation.usecase.social.GetFollowingBriefUseCase
import com.eynnzerr.bandoristation.usecase.social.InformUserUseCase
import com.eynnzerr.bandoristation.usecase.roomhistory.AddRoomHistoryUseCase
import com.eynnzerr.bandoristation.usecase.roomhistory.DeleteRoomHistoriesUseCase
import com.eynnzerr.bandoristation.usecase.roomhistory.DeleteRoomHistoryUseCase
import com.eynnzerr.bandoristation.usecase.roomhistory.EditRoomHistoryUseCase
import com.eynnzerr.bandoristation.usecase.roomhistory.FetchAllHistoryUseCase
import com.eynnzerr.bandoristation.usecase.roomhistory.RoomHistoryAggregator
import com.eynnzerr.bandoristation.usecase.GetLatestReleaseUseCase
import com.eynnzerr.bandoristation.usecase.encryption.AddToBlacklistUseCase
import com.eynnzerr.bandoristation.usecase.encryption.AddToWhitelistUseCase
import com.eynnzerr.bandoristation.usecase.encryption.EncryptionAggregator
import com.eynnzerr.bandoristation.usecase.encryption.GetBlackWhiteListUseCase
import com.eynnzerr.bandoristation.usecase.encryption.GetEncryptionSocketStateUseCase
import com.eynnzerr.bandoristation.usecase.encryption.ListenRoomAccessRequestUseCase
import com.eynnzerr.bandoristation.usecase.encryption.ListenRoomAccessResponseUseCase
import com.eynnzerr.bandoristation.usecase.encryption.RegisterEncryptionUseCase
import com.eynnzerr.bandoristation.usecase.encryption.RemoveFromBlacklistUseCase
import com.eynnzerr.bandoristation.usecase.encryption.RemoveFromWhitelistUseCase
import com.eynnzerr.bandoristation.usecase.encryption.RequestEncryptedRoomAccessUseCase
import com.eynnzerr.bandoristation.usecase.encryption.RespondToRoomAccessUseCase
import com.eynnzerr.bandoristation.usecase.encryption.UpdateInviteCodeUseCase
import com.eynnzerr.bandoristation.usecase.encryption.UploadEncryptedRoomUseCase
import com.eynnzerr.bandoristation.usecase.encryption.VerifyInviteCodeUseCase
import com.eynnzerr.bandoristation.usecase.time.GetServerTimeUseCase
import com.eynnzerr.bandoristation.usecase.websocket.ReceiveNoticeUseCase
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

fun provideUseCaseModule() = module {
    singleOf(::GetServerTimeUseCase)

    single {
        SetUpClientUseCase(
            repository = get(),
            dispatcher = get(named(DispatcherQualifiers.IO_DISPATCHER)),
        )
    }

    single {
        GetWebSocketStateUseCase(
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

    single {
        GetLatestReleaseUseCase(
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
    singleOf(::UpdatePasswordUseCase)
    singleOf(::UpdateEmailSendVCodeUseCase)
    singleOf(::UpdateEmailVerifyEmailUseCase)
    singleOf(::ResetPasswordSendVCodeUseCase)
    singleOf(::ResetPasswordVerifyEmailUseCase)
    singleOf(::ResetPasswordUseCase)
    singleOf(::UpdateAccountAggregator)

    // Room History UseCases
    singleOf(::FetchAllHistoryUseCase)
    singleOf(::AddRoomHistoryUseCase)
    singleOf(::EditRoomHistoryUseCase)
    singleOf(::DeleteRoomHistoryUseCase)
    singleOf(::DeleteRoomHistoriesUseCase)
    singleOf(::RoomHistoryAggregator)

    // Encryption UseCases
    single {
        RegisterEncryptionUseCase(
            repository = get(),
            encryptionSocketClient = get(named("BandoriscriptionWS")),
            dataStore = get(),
        )
    }
    singleOf(::UpdateInviteCodeUseCase)
    singleOf(::VerifyInviteCodeUseCase)
    singleOf(::UploadEncryptedRoomUseCase)
    singleOf(::RequestEncryptedRoomAccessUseCase)
    singleOf(::RespondToRoomAccessUseCase)
    singleOf(::ListenRoomAccessRequestUseCase)
    singleOf(::ListenRoomAccessResponseUseCase)
    singleOf(::AddToWhitelistUseCase)
    singleOf(::AddToBlacklistUseCase)
    singleOf(::RemoveFromWhitelistUseCase)
    singleOf(::RemoveFromBlacklistUseCase)
    singleOf(::GetBlackWhiteListUseCase)
    singleOf(::GetEncryptionSocketStateUseCase)
    singleOf(::EncryptionAggregator)
}
