package com.eynnzerr.bandoristation.feature.settings

import com.eynnzerr.bandoristation.base.UIEffect
import com.eynnzerr.bandoristation.base.UIEvent
import com.eynnzerr.bandoristation.base.UIState

data class SettingState(
    val themeName: String = "",
    val isFilteringPJSK: Boolean = true,
    val isClearingOutdatedRoom: Boolean = false,
    val isShowingPlayerInfo: Boolean = false,
    val isRecordingRoomHistory: Boolean = true,
    val autoUploadInterval: Long = 30,
) : UIState {
    companion object {
        fun initial() = SettingState()
    }
}

sealed class SettingEvent : UIEvent {
    data class UpdateBandTheme(val name: String): SettingEvent()
    data class UpdateFilterPJSK(val isFiltering: Boolean): SettingEvent()
    data class UpdateClearOutdatedRoom(val isClearing: Boolean): SettingEvent()
    data class UpdateShowPlayerInfo(val isShowing: Boolean): SettingEvent()
    data class UpdateRecordRoomHistory(val isRecording: Boolean): SettingEvent()
    data class UpdateAutoUploadInterval(val interval: Long): SettingEvent()
}

sealed class SettingEffect : UIEffect {
    data class ControlTutorialDialog(val isShowing: Boolean): SettingEffect()
    data class ControlRegexDialog(val isShowing: Boolean): SettingEffect()
}