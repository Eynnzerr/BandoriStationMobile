package com.eynnzerr.bandoristation.utils

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

sealed class ScreenAspectRatio {
    data object Portrait: ScreenAspectRatio() // 竖屏/窄屏比例
    data object Landscape: ScreenAspectRatio() // 横屏/宽屏比例
}

data class ScreenInfo(
    val aspectRatio: ScreenAspectRatio = ScreenAspectRatio.Portrait,
    val widthDp: Dp = 0.dp,
    val heightDp: Dp = 0.dp,
    val aspectRatioValue: Float = 1f // 宽高比值 (width/height)
) {
    constructor(sizeDp: Pair<Dp, Dp>): this(
        aspectRatio = calculateAspectRatio(sizeDp.first, sizeDp.second),
        widthDp = sizeDp.first,
        heightDp = sizeDp.second,
        aspectRatioValue = if (sizeDp.second != 0.dp) sizeDp.first / sizeDp.second else 1f
    )

    fun isLandscape() = aspectRatio == ScreenAspectRatio.Landscape
    fun isPortrait() = aspectRatio == ScreenAspectRatio.Portrait

    companion object {
        private fun calculateAspectRatio(width: Dp, height: Dp): ScreenAspectRatio {
            if (height == 0.dp) return ScreenAspectRatio.Landscape

            val ratio = width / height

            // 比例大于1表示宽度大于高度，为横屏
            return if (ratio >= 1f) ScreenAspectRatio.Landscape else ScreenAspectRatio.Portrait
        }
    }
}
