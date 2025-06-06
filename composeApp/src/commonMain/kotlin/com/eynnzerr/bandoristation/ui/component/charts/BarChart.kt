package com.eynnzerr.bandoristation.ui.component.charts

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.*
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import kotlin.math.*

@Composable
fun BarChart(
    dataSet: ChartDataSet,
    modifier: Modifier = Modifier,
    config: ChartConfig = ChartConfig(),
    height: Dp = 300.dp,
    barStyle: BarStyle = BarStyle.SOLID,
    orientation: BarOrientation = BarOrientation.VERTICAL,
    barSpacing: Float = 0.2f,
    onBarClick: ((ChartData) -> Unit)? = null
) {
    var selectedBar by remember { mutableStateOf<Int?>(null) }
    var hoveredBar by remember { mutableStateOf<Int?>(null) }

    val density = LocalDensity.current
    val textMeasurer = rememberTextMeasurer()

    // 动画进度
    val animationProgress by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(
            durationMillis = config.animationDuration,
            easing = FastOutSlowInEasing
        )
    )

    // 为每个条形创建独立的动画
    val barAnimations = dataSet.data.mapIndexed { index, _ ->
        animateFloatAsState(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = config.animationDuration,
                delayMillis = index * 50, // 错开动画
                easing = FastOutSlowInEasing
            )
        )
    }

    val maxValue = dataSet.data.maxOf { it.value }
    val colors = dataSet.data.mapIndexed { index, data ->
        data.color ?: MaterialTheme.colorScheme.primary
    }

    Box(modifier = modifier.height(height)) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(8.dp))
                .background(config.backgroundColor)
                .pointerInput(Unit) {
                    detectTapGestures { offset ->
                        findClickedBar(
                            offset,
                            dataSet.data,
                            Size(size.width.toFloat(), size.height.toFloat()),
                            orientation
                        )?.let { index ->
                            selectedBar = index
                            onBarClick?.invoke(dataSet.data[index])
                        }
                    }
                }
        ) {
            val chartPadding = 60.dp.toPx()
            val chartWidth = size.width - chartPadding * 2
            val chartHeight = size.height - chartPadding * 2

            // 绘制网格
            if (config.showGrid) {
                drawGrid(
                    chartPadding,
                    chartWidth,
                    chartHeight,
                    config.gridColor,
                    orientation
                )
            }

            // 绘制坐标轴
            drawAxes(
                chartPadding,
                chartWidth,
                chartHeight,
                config.labelTextColor
            )

            // 绘制条形
            when (orientation) {
                BarOrientation.VERTICAL -> {
                    drawVerticalBars(
                        dataSet.data,
                        chartPadding,
                        chartWidth,
                        chartHeight,
                        maxValue,
                        colors,
                        barAnimations.map { it.value },
                        selectedBar,
                        hoveredBar,
                        barStyle,
                        barSpacing
                    )
                }
                BarOrientation.HORIZONTAL -> {
                    drawHorizontalBars(
                        dataSet.data,
                        chartPadding,
                        chartWidth,
                        chartHeight,
                        maxValue,
                        colors,
                        barAnimations.map { it.value },
                        selectedBar,
                        hoveredBar,
                        barStyle,
                        barSpacing
                    )
                }
            }

            // 绘制标签
            if (config.showLabels) {
                when (orientation) {
                    BarOrientation.VERTICAL -> {
                        drawVerticalBarLabels(
                            dataSet.data,
                            chartPadding,
                            chartWidth,
                            chartHeight,
                            textMeasurer,
                            config.labelTextColor
                        )
                    }
                    BarOrientation.HORIZONTAL -> {
                        drawHorizontalBarLabels(
                            dataSet.data,
                            chartPadding,
                            chartHeight,
                            maxValue,
                            textMeasurer,
                            config.labelTextColor
                        )
                    }
                }
            }

            // 绘制数值
            if (config.showValues && animationProgress > 0.5f) {
                drawBarValues(
                    dataSet.data,
                    chartPadding,
                    chartWidth,
                    chartHeight,
                    maxValue,
                    barAnimations.map { it.value },
                    textMeasurer,
                    config.valueTextColor,
                    orientation
                )
            }
        }
    }
}

enum class BarStyle {
    SOLID, GRADIENT, PATTERN, ROUNDED
}

enum class BarOrientation {
    VERTICAL, HORIZONTAL
}

private fun DrawScope.drawVerticalBars(
    data: List<ChartData>,
    padding: Float,
    width: Float,
    height: Float,
    maxValue: Float,
    colors: List<Color>,
    animationProgress: List<Float>,
    selectedBar: Int?,
    hoveredBar: Int?,
    barStyle: BarStyle,
    barSpacing: Float
) {
    val barWidth = width / data.size * (1 - barSpacing)
    val spacing = width / data.size * barSpacing

    data.forEachIndexed { index, item ->
        val barHeight = (item.value / maxValue) * height * animationProgress[index]
        val x = padding + index * (barWidth + spacing) + spacing / 2
        val y = padding + height - barHeight

        val isSelected = selectedBar == index
        val isHovered = hoveredBar == index

        // 阴影效果
        if (isSelected || isHovered) {
            drawRoundRect(
                color = Color.Black.copy(alpha = 0.1f),
                topLeft = Offset(x + 4, y + 4),
                size = Size(barWidth, barHeight),
                cornerRadius = CornerRadius(4.dp.toPx())
            )
        }

        // 绘制条形
        when (barStyle) {
            BarStyle.GRADIENT -> {
                drawRoundRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            colors[index],
                            colors[index].copy(alpha = 0.7f)
                        )
                    ),
                    topLeft = Offset(x, y),
                    size = Size(barWidth, barHeight),
                    cornerRadius = CornerRadius(4.dp.toPx())
                )
            }
            BarStyle.ROUNDED -> {
                drawRoundRect(
                    color = colors[index],
                    topLeft = Offset(x, y),
                    size = Size(barWidth, barHeight),
                    cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx())
                )
            }
            BarStyle.PATTERN -> {
                // 绘制条纹图案
                drawRect(
                    color = colors[index],
                    topLeft = Offset(x, y),
                    size = Size(barWidth, barHeight)
                )

                // 添加斜条纹
                val stripeWidth = 4.dp.toPx()
                val stripeSpacing = 8.dp.toPx()

                clipRect(
                    left = x,
                    top = y,
                    right = x + barWidth,
                    bottom = y + barHeight
                ) {
                    for (i in 0..20) {
                        val startX = x + i * (stripeWidth + stripeSpacing)
                        drawLine(
                            color = colors[index].copy(alpha = 0.3f),
                            start = Offset(startX, y + barHeight),
                            end = Offset(startX + barHeight, y),
                            strokeWidth = stripeWidth
                        )
                    }
                }
            }
            else -> {
                drawRect(
                    color = colors[index],
                    topLeft = Offset(x, y),
                    size = Size(barWidth, barHeight)
                )
            }
        }

        // 选中效果
        if (isSelected) {
            drawRect(
                color = Color.White.copy(alpha = 0.3f),
                topLeft = Offset(x, y),
                size = Size(barWidth, barHeight)
            )

            // 边框
            drawRect(
                color = colors[index],
                topLeft = Offset(x, y),
                size = Size(barWidth, barHeight),
                style = Stroke(width = 2.dp.toPx())
            )
        }
    }
}

private fun DrawScope.drawHorizontalBars(
    data: List<ChartData>,
    padding: Float,
    width: Float,
    height: Float,
    maxValue: Float,
    colors: List<Color>,
    animationProgress: List<Float>,
    selectedBar: Int?,
    hoveredBar: Int?,
    barStyle: BarStyle,
    barSpacing: Float
) {
    val barHeight = height / data.size * (1 - barSpacing)
    val spacing = height / data.size * barSpacing

    data.forEachIndexed { index, item ->
        val barWidth = (item.value / maxValue) * width * animationProgress[index]
        val x = padding
        val y = padding + index * (barHeight + spacing) + spacing / 2

        val isSelected = selectedBar == index

        // 绘制条形
        when (barStyle) {
            BarStyle.GRADIENT -> {
                drawRoundRect(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            colors[index].copy(alpha = 0.7f),
                            colors[index]
                        )
                    ),
                    topLeft = Offset(x, y),
                    size = Size(barWidth, barHeight),
                    cornerRadius = CornerRadius(4.dp.toPx())
                )
            }
            BarStyle.ROUNDED -> {
                drawRoundRect(
                    color = colors[index],
                    topLeft = Offset(x, y),
                    size = Size(barWidth, barHeight),
                    cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx())
                )
            }
            else -> {
                drawRect(
                    color = colors[index],
                    topLeft = Offset(x, y),
                    size = Size(barWidth, barHeight)
                )
            }
        }

        // 选中效果
        if (isSelected) {
            drawRect(
                color = Color.White.copy(alpha = 0.3f),
                topLeft = Offset(x, y),
                size = Size(barWidth, barHeight)
            )
        }
    }
}

private fun DrawScope.drawBarValues(
    data: List<ChartData>,
    padding: Float,
    width: Float,
    height: Float,
    maxValue: Float,
    animationProgress: List<Float>,
    textMeasurer: TextMeasurer,
    textColor: Color,
    orientation: BarOrientation
) {
    val textStyle = TextStyle(
        fontSize = 12.sp,
        color = textColor,
        fontWeight = FontWeight.Medium
    )

    when (orientation) {
        BarOrientation.VERTICAL -> {
            val barWidth = width / data.size * 0.8f
            val spacing = width / data.size * 0.2f

            data.forEachIndexed { index, item ->
                val barHeight = (item.value / maxValue) * height * animationProgress[index]
                val x = padding + index * (barWidth + spacing) + spacing / 2 + barWidth / 2
                val y = padding + height - barHeight - 8.dp.toPx()

                val text = textMeasurer.measure(
                    text = item.value.toInt().toString(),
                    style = textStyle
                )

                drawText(
                    textLayoutResult = text,
                    topLeft = Offset(x - text.size.width / 2, y - text.size.height)
                )
            }
        }
        BarOrientation.HORIZONTAL -> {
            val barHeight = height / data.size * 0.8f
            val spacing = height / data.size * 0.2f

            data.forEachIndexed { index, item ->
                val barWidth = (item.value / maxValue) * width * animationProgress[index]
                val x = padding + barWidth + 8.dp.toPx()
                val y = padding + index * (barHeight + spacing) + spacing / 2 + barHeight / 2

                val text = textMeasurer.measure(
                    text = item.value.toInt().toString(),
                    style = textStyle
                )

                drawText(
                    textLayoutResult = text,
                    topLeft = Offset(x, y - text.size.height / 2)
                )
            }
        }
    }
}

private fun DrawScope.drawVerticalBarLabels(
    data: List<ChartData>,
    padding: Float,
    width: Float,
    height: Float,
    textMeasurer: TextMeasurer,
    textColor: Color
) {
    val textStyle = TextStyle(
        fontSize = 11.sp,
        color = textColor
    )

    val barWidth = width / data.size * 0.8f
    val spacing = width / data.size * 0.2f

    val sample = data.maxByOrNull { it.label.length }
    val sampleText = sample?.let {
        textMeasurer.measure(
            text = it.label,
            style = textStyle,
            constraints = Constraints(maxWidth = barWidth.toInt())
        )
    }
    val step = if (sampleText != null) {
        max(1, ceil(sampleText.size.width / (barWidth + spacing)).toInt())
    } else 1

    data.forEachIndexed { index, item ->
        if (index % step != 0) return@forEachIndexed

        val x = padding + index * (barWidth + spacing) + spacing / 2 + barWidth / 2
        val y = padding + height + 16.dp.toPx()

        val text = textMeasurer.measure(
            text = item.label,
            style = textStyle,
            constraints = Constraints(maxWidth = barWidth.toInt())
        )

        var xPos = x - text.size.width / 2
        xPos = xPos.coerceIn(padding, padding + width - text.size.width)

        drawText(
            textLayoutResult = text,
            topLeft = Offset(xPos, y)
        )
    }
}

private fun DrawScope.drawHorizontalBarLabels(
    data: List<ChartData>,
    padding: Float,
    height: Float,
    maxValue: Float,
    textMeasurer: TextMeasurer,
    textColor: Color
) {
    val textStyle = TextStyle(
        fontSize = 11.sp,
        color = textColor
    )

    val barHeight = height / data.size * 0.8f
    val spacing = height / data.size * 0.2f

    data.forEachIndexed { index, item ->
        val x = padding - 8.dp.toPx()
        val y = padding + index * (barHeight + spacing) + spacing / 2 + barHeight / 2

        val text = textMeasurer.measure(
            text = item.label,
            style = textStyle
        )

        drawText(
            textLayoutResult = text,
            topLeft = Offset(x - text.size.width, y - text.size.height / 2)
        )
    }
}

private fun findClickedBar(
    offset: Offset,
    data: List<ChartData>,
    canvasSize: Size,
    orientation: BarOrientation
): Int? {
    val padding = 60f
    val width = canvasSize.width - padding * 2
    val height = canvasSize.height - padding * 2

    return when (orientation) {
        BarOrientation.VERTICAL -> {
            val barWidth = width / data.size * 0.8f
            val spacing = width / data.size * 0.2f

            data.indices.firstOrNull { index ->
                val x = padding + index * (barWidth + spacing) + spacing / 2
                offset.x >= x && offset.x <= x + barWidth
            }
        }
        BarOrientation.HORIZONTAL -> {
            val barHeight = height / data.size * 0.8f
            val spacing = height / data.size * 0.2f

            data.indices.firstOrNull { index ->
                val y = padding + index * (barHeight + spacing) + spacing / 2
                offset.y >= y && offset.y <= y + barHeight
            }
        }
    }
}
