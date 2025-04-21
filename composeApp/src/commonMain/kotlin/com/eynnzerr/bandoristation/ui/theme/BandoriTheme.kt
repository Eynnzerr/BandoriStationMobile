package com.eynnzerr.bandoristation.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.materialkolor.DynamicMaterialTheme

@Composable
fun BandoriTheme(
    seedColor: Color = Color(0xFFFFDDEE),
    content: @Composable () -> Unit
) {
    DynamicMaterialTheme(
        seedColor = seedColor,
        animate = true,
        content = content
    )
}