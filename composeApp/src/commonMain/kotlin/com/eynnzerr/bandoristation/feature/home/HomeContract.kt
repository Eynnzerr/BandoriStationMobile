package com.eynnzerr.bandoristation.feature.home

import com.eynnzerr.bandoristation.base.UIEffect
import com.eynnzerr.bandoristation.base.UIEvent
import com.eynnzerr.bandoristation.base.UIState
import com.eynnzerr.bandoristation.model.ApiRequest
import com.eynnzerr.bandoristation.model.RoomFilter
import com.eynnzerr.bandoristation.model.RoomInfo
import com.eynnzerr.bandoristation.model.RoomUploadInfo
import com.eynnzerr.bandoristation.model.UserInfo
import com.eynnzerr.bandoristation.model.GithubRelease
import com.eynnzerr.bandoristation.navigation.Screen
import kotlinx.datetime.Clock.System
import org.jetbrains.compose.resources.StringResource
import bandoristationm.composeapp.generated.resources.Res
import bandoristationm.composeapp.generated.resources.home_screen_title

data class HomeState(
    val rooms: List<RoomInfo> = emptyList(),
    val roomFilter: RoomFilter = RoomFilter(),
    val hasUnReadMessages: Boolean = false,
    val selectedRoom: RoomInfo? = null,
    val serverTimestampMillis: Long = System.now().toEpochMilliseconds(),
    val joinedTimestampMillis: Long = System.now().toEpochMilliseconds(),
    val presetWords: Set<String> = emptySet(),
    val title: StringResource = Res.string.home_screen_title,
    val isShowingPlayerBrief: Boolean = false,
    val isFirstRun: Boolean = false,
) : UIState {
    companion object {
        fun initial() = HomeState(
        )
    }
}

sealed class HomeIntent: UIEvent {
    class SetNoReminder(): HomeIntent()
    data class UpdateRoomList(val rooms: List<RoomInfo>): HomeIntent()
    data class AppendRoomList(val rooms: List<RoomInfo>): HomeIntent()
    data class UpdateTimestamp(val timestampMillis: Long): HomeIntent()
    data class UpdateMessageBadge(val hasUnReadMessages: Boolean): HomeIntent()
    data class JoinRoom(val room: RoomInfo?): HomeIntent()
    data class UploadRoom(val room: RoomUploadInfo, val continuous: Boolean = false): HomeIntent()
    data class UpdatePresetWords(val words: Set<String>): HomeIntent()
    data class AddPresetWord(val word: String): HomeIntent()
    data class RemovePresetWord(val word: String): HomeIntent()
    class ClearRooms(): HomeIntent()
    class RefreshRooms(): HomeIntent()
    data class InformUser(val params: ApiRequest.InformUser): HomeIntent()
    class GetRoomFilter(): HomeIntent()
    data class UpdateRoomFilter(val filter: RoomFilter): HomeIntent()
}

sealed class HomeEffect: UIEffect {
    data class NavigateToScreen(val destination: Screen): HomeEffect()
    data class CopyRoomNumber(val roomNumber: String): HomeEffect()
    data class ShowResourceSnackbar(val textRes: StringResource): HomeEffect()
    data class ShowSnackbar(val text: String): HomeEffect()
    class ScrollToFirst: HomeEffect()
    data class OpenSendRoomDialog(
        val prefillRoomNumber: String = "",
        val prefillDescription: String = "",
    ): HomeEffect()
    class CloseSendRoomDialog(): HomeEffect()
    data class OpenInformUserDialog(
        val roomToInform: RoomInfo,
    ): HomeEffect()
    class CloseInformUserDialog(): HomeEffect()
    data class OpenFilterDialog(
        val prefillWords: List<String> = emptyList(),
        val prefillUsers: List<UserInfo> = emptyList(),
    ): HomeEffect()
    class CloseFilterDialog(): HomeEffect()
    data class OpenBlockUserDialog(
        val userToBlock: UserInfo,
    ): HomeEffect()
    class CloseBlockUserDialog(): HomeEffect()
    class OpenHelpDialog(): HomeEffect()
    class CloseHelpDialog(): HomeEffect()
    data class OpenUpdateDialog(val release: GithubRelease): HomeEffect()
    class CloseUpdateDialog(): HomeEffect()
}