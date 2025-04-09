package com.eynnzerr.bandoristation.feature.home

import com.eynnzerr.bandoristation.base.UIEffect
import com.eynnzerr.bandoristation.base.UIEvent
import com.eynnzerr.bandoristation.base.UIState
import com.eynnzerr.bandoristation.model.RoomInfo
import com.eynnzerr.bandoristation.navigation.Screen
import kotlinx.datetime.Clock.System

data class HomeState(
    val rooms: List<RoomInfo> = emptyList(),
    val hasUnReadMessages: Boolean = false,
    val timestampMillis: Long = System.now().toEpochMilliseconds()
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
}

sealed class HomeEffect: UIEffect {
    data class NavigateToScreen(val destination: Screen): HomeEffect()
    data class CopyRoomNumber(val roomNumber: String): HomeEffect()
}