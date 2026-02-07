package com.eynnzerr.bandoristation.feature.settings

import com.eynnzerr.bandoristation.base.UIEffect
import com.eynnzerr.bandoristation.base.UIEvent
import com.eynnzerr.bandoristation.base.UIState
import com.eynnzerr.bandoristation.feature.home.HomeIntent
import com.eynnzerr.bandoristation.getPlatform
import com.eynnzerr.bandoristation.model.account.AccountInfo
import com.eynnzerr.bandoristation.model.account.AccountSummary
import org.jetbrains.compose.resources.StringResource

data class SettingState(
    val themeName: String = "",
    val isFilteringPJSK: Boolean = true,
    val isClearingOutdatedRoom: Boolean = false,
    val isShowingPlayerInfo: Boolean = false,
    val isRecordingRoomHistory: Boolean = true,
    val isAutoPullingNewRooms: Boolean = false,
    val autoUploadInterval: Long = 10,
    val onlineNumber: Int? = null,
    val isOnlineNumberLoading: Boolean = false,
    val versionName: String = "",
    val isEncryptionEnabled: Boolean = false,
    val encryptionValidDays: Long = 0,
    val inviteCode: String = "",
    val blacklist: List<String> = emptyList(),
    val whitelist: List<String> = emptyList(),
    val selectedUser: AccountInfo = AccountInfo(),
    val followingUsers: List<Long> = emptyList(),
) : UIState {
    companion object {
        fun initial() = SettingState(
            versionName = getPlatform().name + ": " + getPlatform().versionName
        )
    }
}

sealed class SettingEvent : UIEvent {
    data class UpdateBandTheme(val name: String): SettingEvent()
    data class UpdateFilterPJSK(val isFiltering: Boolean): SettingEvent()
    data class UpdateClearOutdatedRoom(val isClearing: Boolean): SettingEvent()
    data class UpdateShowPlayerInfo(val isShowing: Boolean): SettingEvent()
    data class UpdateRecordRoomHistory(val isRecording: Boolean): SettingEvent()
    data class UpdateAutoPullNewRooms(val enabled: Boolean): SettingEvent()
    data class UpdateAutoUploadInterval(val interval: Long): SettingEvent()
    class FetchOnlineNumber: SettingEvent()
    data class UpdateEnableEncryption(val enabled: Boolean): SettingEvent()
    class RegisterEncryption: SettingEvent()
    data class UpdateInviteCode(val code: String): SettingEvent()
    data class RemoveFromWhiteList(val id: String): SettingEvent()
    data class RemoveFromBlackList(val id: String): SettingEvent()
    class FetchWhiteBlackList: SettingEvent()
    data class BrowseUser(val id: Long): SettingEvent()
    data class FollowUser(val id: Long): SettingEvent()
}

sealed class SettingEffect : UIEffect {
    data class ControlTutorialDialog(val isShowing: Boolean): SettingEffect()
    data class ControlListDialog(val isShowing: Boolean): SettingEffect()
    data class ControlProfileDialog(val isShowing: Boolean): SettingEffect()
    data class ShowSnackbar(val text: String): SettingEffect()
    data class ShowResourceSnackbar(val textRes: StringResource): SettingEffect()
}
