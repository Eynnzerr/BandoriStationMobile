package com.eynnzerr.bandoristation.ui.common

import androidx.compose.runtime.compositionLocalOf
import com.eynnzerr.bandoristation.ui.theme.BandThemeConfig
import com.eynnzerr.bandoristation.utils.ScreenInfo

data class AppProperty(
    val screenInfo: ScreenInfo,
    val bandTheme: BandThemeConfig,
)

val LocalAppProperty = compositionLocalOf {
    AppProperty(
        screenInfo = ScreenInfo(),
        bandTheme = BandThemeConfig.Default,
    )
}