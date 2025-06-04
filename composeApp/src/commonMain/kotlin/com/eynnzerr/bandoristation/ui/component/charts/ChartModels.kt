package com.eynnzerr.bandoristation.ui.component.charts

import androidx.compose.ui.graphics.Color

data class ChartData(
    val label: String,
    val value: Float,
    val color: Color? = null,
    val metadata: Any? = null
)

data class ChartDataSet(
    val data: List<ChartData>,
    val label: String = "",
    val color: Color? = null
)

data class ChartConfig(
    val showGrid: Boolean = true,
    val showLabels: Boolean = true,
    val showValues: Boolean = true,
    val showLegend: Boolean = true,
    val animationDuration: Int = 800,
    val enableInteraction: Boolean = true,
    val backgroundColor: Color = Color.Transparent,
    val gridColor: Color = Color.Gray.copy(alpha = 0.2f),
    val labelTextColor: Color = Color.Black,
    val valueTextColor: Color = Color.Black
)
