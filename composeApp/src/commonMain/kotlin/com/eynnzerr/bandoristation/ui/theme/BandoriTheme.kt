package com.eynnzerr.bandoristation.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.materialkolor.DynamicMaterialExpressiveTheme

@Composable
fun BandoriTheme(
    seedColor: Color = Color(0xFFFFDDEE),
    content: @Composable () -> Unit
) {
    DynamicMaterialExpressiveTheme(
        seedColor = seedColor,
        animate = true,
        content = content
    )
}