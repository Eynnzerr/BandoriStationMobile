package com.eynnzerr.bandoristation.utils

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class ScreenInfo(
    val widthType: ScreenType = ScreenType.Compact,
    val heightType: ScreenType = ScreenType.Compact,
    val widthDp: Dp = 0.dp,
    val heightDp: Dp = 0.dp,
) {
    constructor(sizeDp: Pair<Dp, Dp>): this(
        widthType = when {
            sizeDp.first < 600.dp -> ScreenType.Compact
            sizeDp.first < 840.dp -> ScreenType.Medium
            else -> ScreenType.Expanded
        },
        heightType = when {
            // unused
            sizeDp.second < 480.dp -> ScreenType.Compact
            sizeDp.second < 900.dp -> ScreenType.Medium
            else -> ScreenType.Expanded
        },
        widthDp = sizeDp.first,
        heightDp = sizeDp.second
    )

    fun isExpanded() = widthType == ScreenType.Expanded
}

sealed class ScreenType {
    data object Compact: ScreenType()
    data object Medium: ScreenType()
    data object Expanded: ScreenType()
}