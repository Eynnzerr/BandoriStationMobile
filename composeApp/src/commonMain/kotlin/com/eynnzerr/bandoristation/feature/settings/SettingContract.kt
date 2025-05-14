package com.eynnzerr.bandoristation.feature.settings

import com.eynnzerr.bandoristation.base.UIEffect
import com.eynnzerr.bandoristation.base.UIEvent
import com.eynnzerr.bandoristation.base.UIState

data class SettingState(
    val themeName: String = "",
    val isFilteringPJSK: Boolean = true,
) : UIState {
    companion object {
        fun initial() = SettingState()
    }
}

sealed class SettingEvent : UIEvent {
    data class UpdateBandTheme(val name: String): SettingEvent()
    data class UpdateFilterPJSK(val isFiltering: Boolean): SettingEvent()
}

sealed class SettingEffect(

) : UIEffect