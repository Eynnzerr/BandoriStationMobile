package com.eynnzerr.bandoristation.feature.home

import com.eynnzerr.bandoristation.base.UIEffect
import com.eynnzerr.bandoristation.base.UIEvent
import com.eynnzerr.bandoristation.base.UIState
import com.eynnzerr.bandoristation.model.RoomInfo
import com.eynnzerr.bandoristation.model.RoomUploadInfo
import com.eynnzerr.bandoristation.navigation.Screen
import kotlinx.datetime.Clock.System
import org.jetbrains.compose.resources.StringResource

data class HomeState(
    val rooms: List<RoomInfo> = emptyList(),
    val hasUnReadMessages: Boolean = false,
    val selectedRoom: RoomInfo? = null,
    val localTimestampMillis: Long = System.now().toEpochMilliseconds(),
    val joinedTimestampMillis: Long = System.now().toEpochMilliseconds(),
    val presetWords: Set<String> = emptySet()
) : UIState {
    companion object {
        fun initial() = HomeState()
    }
}

sealed class HomeIntent: UIEvent {
    data class UpdateRoomList(val rooms: List<RoomInfo>): HomeIntent()
    data class AppendRoomList(val rooms: List<RoomInfo>): HomeIntent()
    data class UpdateTimestamp(val timestampMillis: Long): HomeIntent()
    data class UpdateMessageBadge(val hasUnReadMessages: Boolean): HomeIntent()
    data class JoinRoom(val room: RoomInfo?): HomeIntent()
    data class UploadRoom(val room: RoomUploadInfo): HomeIntent()
    data class UpdatePresetWords(val words: Set<String>): HomeIntent()
    data class AddPresetWord(val word: String): HomeIntent()
    data class RemovePresetWord(val word: String): HomeIntent()
}

sealed class HomeEffect: UIEffect {
    data class NavigateToScreen(val destination: Screen): HomeEffect()
    data class CopyRoomNumber(val roomNumber: String): HomeEffect()
    data class ShowSnackbar(val textRes: StringResource): HomeEffect()
    class ScrollToFirst: HomeEffect()
    data class OpenSendRoomDialog(
        val prefillRoomNumber: String = "",
        val prefillDescription: String = "",
    ): HomeEffect()
    class CloseSendRoomDialog(): HomeEffect()
}