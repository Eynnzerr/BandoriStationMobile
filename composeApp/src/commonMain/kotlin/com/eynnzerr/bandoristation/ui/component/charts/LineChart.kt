package com.eynnzerr.bandoristation.ui.component.charts

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
fun LineChart(
    dataSets: List<ChartDataSet>,
    modifier: Modifier = Modifier,
    config: ChartConfig = ChartConfig(),
    height: Dp = 300.dp,
    lineType: LineType = LineType.CURVED,
    pointStyle: PointStyle = PointStyle.CIRCLE,
    onPointClick: ((ChartDataSet, ChartData) -> Unit)? = null
) {
    var selectedPoint by remember { mutableStateOf<Pair<Int, Int>?>(null) }
    var hoveredPoint by remember { mutableStateOf<Pair<Int, Int>?>(null) }
    var showTooltip by remember { mutableStateOf(false) }
    var tooltipPosition by remember { mutableStateOf(Offset.Zero) }

    val density = LocalDensity.current
    val textMeasurer = rememberTextMeasurer()

    // 动画进度
    val animationProgress by animateFloatAsState(
        targetValue = if (config.animationDuration > 0) 1f else 1f,
        animationSpec = tween(
            durationMillis = config.animationDuration,
            easing = FastOutSlowInEasing
        )
    )

    // 找出数据范围
    val allValues = dataSets.flatMap { it.data.map { data -> data.value } }
    val minValue = allValues.minOrNull() ?: 0f
    val maxValue = allValues.maxOrNull() ?: 1f
    val valueRange = maxValue - minValue

    Box(modifier = modifier.height(height)) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(8.dp))
                .background(config.backgroundColor)
                .pointerInput(Unit) {
                    detectTapGestures { offset ->
                        // 修复：将IntSize转换为Size
                        val canvasSize = Size(size.width.toFloat(), size.height.toFloat())
                        findNearestPoint(offset, dataSets, canvasSize)?.let { (setIndex, dataIndex) ->
                            selectedPoint = setIndex to dataIndex
                            onPointClick?.invoke(dataSets[setIndex], dataSets[setIndex].data[dataIndex])
                        }
                    }
                }
                .pointerInput(Unit) {
                    detectDragGestures { _, _ ->
                        // 可以添加拖动交互
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
                    config.gridColor
                )
            }

            // 绘制坐标轴
            drawAxes(
                chartPadding,
                chartWidth,
                chartHeight,
                config.labelTextColor
            )

            // 绘制Y轴标签
            if (config.showLabels) {
                drawYAxisLabels(
                    chartPadding,
                    chartHeight,
                    minValue,
                    maxValue,
                    textMeasurer,
                    config.labelTextColor
                )
            }

            // 绘制数据线
            dataSets.forEachIndexed { setIndex, dataSet ->
                val points = dataSet.data.mapIndexed { index, data ->
                    val x = chartPadding + (chartWidth / (dataSet.data.size - 1)) * index
                    val y = chartPadding + chartHeight - ((data.value - minValue) / valueRange * chartHeight)
                    Offset(x, y)
                }

                val animatedPoints = points.map { point ->
                    Offset(
                        point.x,
                        chartPadding + chartHeight - (chartPadding + chartHeight - point.y) * animationProgress
                    )
                }

                // 绘制填充区域
                if (lineType == LineType.AREA) {
                    drawAreaFill(
                        animatedPoints,
                        chartPadding + chartHeight,
                        dataSet.color ?: generateChartColors(1)[0],
                        animationProgress
                    )
                }

                // 绘制线条
                drawDataLine(
                    animatedPoints,
                    dataSet.color ?: generateChartColors(dataSets.size)[setIndex],
                    lineType,
                    animationProgress
                )

                // 绘制数据点
                animatedPoints.forEachIndexed { index, point ->
                    val isSelected = selectedPoint == setIndex to index
                    val isHovered = hoveredPoint == setIndex to index

                    drawDataPoint(
                        point,
                        dataSet.color ?: generateChartColors(dataSets.size)[setIndex],
                        pointStyle,
                        isSelected,
                        isHovered,
                        animationProgress
                    )

                    // 绘制数值标签
                    if (config.showValues && animationProgress > 0.5f) {
                        drawValueLabel(
                            point,
                            dataSet.data[index].value,
                            textMeasurer,
                            config.valueTextColor
                        )
                    }
                }
            }

            // 绘制X轴标签
            if (config.showLabels && dataSets.isNotEmpty()) {
                drawXAxisLabels(
                    dataSets.first().data,
                    chartPadding,
                    chartWidth,
                    chartHeight,
                    textMeasurer,
                    config.labelTextColor
                )
            }
        }

        // 图例
        if (config.showLegend && dataSets.size > 1) {
            ChartLegend(
                dataSets = dataSets,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
            )
        }

        // 工具提示
        AnimatedVisibility(
            visible = showTooltip && hoveredPoint != null,
            modifier = Modifier.offset {
                IntOffset(
                    tooltipPosition.x.toInt(),
                    tooltipPosition.y.toInt()
                )
            }
        ) {
            hoveredPoint?.let { (setIndex, dataIndex) ->
                ChartTooltip(
                    label = dataSets[setIndex].data[dataIndex].label,
                    value = dataSets[setIndex].data[dataIndex].value,
                    color = dataSets[setIndex].color ?: generateChartColors(dataSets.size)[setIndex]
                )
            }
        }
    }
}

enum class LineType {
    STRAIGHT, CURVED, STEPPED, AREA
}

enum class PointStyle {
    NONE, CIRCLE, SQUARE, DIAMOND
}

private fun DrawScope.drawDataLine(
    points: List<Offset>,
    color: Color,
    lineType: LineType,
    progress: Float
) {
    if (points.size < 2) return

    val path = when (lineType) {
        LineType.CURVED -> createSmoothPath(points.take((points.size * progress).toInt()))
        LineType.STEPPED -> createSteppedPath(points.take((points.size * progress).toInt()))
        else -> Path().apply {
            moveTo(points.first().x, points.first().y)
            points.take((points.size * progress).toInt()).forEach { point ->
                lineTo(point.x, point.y)
            }
        }
    }

    drawPath(
        path = path,
        color = color,
        style = Stroke(
            width = 3.dp.toPx(),
            cap = StrokeCap.Round,
            join = StrokeJoin.Round
        )
    )
}

private fun DrawScope.drawDataPoint(
    point: Offset,
    color: Color,
    style: PointStyle,
    isSelected: Boolean,
    isHovered: Boolean,
    progress: Float
) {
    if (style == PointStyle.NONE) return

    val radius = when {
        isSelected -> 8.dp.toPx()
        isHovered -> 6.dp.toPx()
        else -> 4.dp.toPx()
    } * progress

    // 外圈（选中/悬停效果）
    if (isSelected || isHovered) {
        drawCircle(
            color = color.copy(alpha = 0.3f),
            radius = radius * 1.5f,
            center = point
        )
    }

    // 内圈
    when (style) {
        PointStyle.CIRCLE -> {
            drawCircle(
                color = Color.White,
                radius = radius,
                center = point
            )
            drawCircle(
                color = color,
                radius = radius * 0.7f,
                center = point
            )
        }
        PointStyle.SQUARE -> {
            drawRect(
                color = Color.White,
                topLeft = Offset(point.x - radius, point.y - radius),
                size = Size(radius * 2, radius * 2)
            )
            drawRect(
                color = color,
                topLeft = Offset(point.x - radius * 0.7f, point.y - radius * 0.7f),
                size = Size(radius * 1.4f, radius * 1.4f)
            )
        }
        PointStyle.DIAMOND -> {
            val path = Path().apply {
                moveTo(point.x, point.y - radius)
                lineTo(point.x + radius, point.y)
                lineTo(point.x, point.y + radius)
                lineTo(point.x - radius, point.y)
                close()
            }
            drawPath(path, Color.White)
            drawPath(
                path,
                color,
                style = Stroke(width = 2.dp.toPx())
            )
        }
        else -> {}
    }
}

private fun DrawScope.drawAreaFill(
    points: List<Offset>,
    bottomY: Float,
    color: Color,
    progress: Float
) {
    if (points.isEmpty()) return

    val path = Path().apply {
        moveTo(points.first().x, bottomY)
        lineTo(points.first().x, points.first().y)

        for (i in 1 until points.size) {
            lineTo(points[i].x, points[i].y)
        }

        lineTo(points.last().x, bottomY)
        close()
    }

    drawPath(
        path = path,
        brush = Brush.verticalGradient(
            colors = listOf(
                color.copy(alpha = 0.3f * progress),
                color.copy(alpha = 0.1f * progress)
            )
        )
    )
}

// 查找最近的数据点
private fun findNearestPoint(
    offset: Offset,
    dataSets: List<ChartDataSet>,
    canvasSize: Size
): Pair<Int, Int>? {
    val threshold = 20.dp.value
    var nearestPoint: Pair<Int, Int>? = null
    var minDistance = Float.MAX_VALUE

    dataSets.forEachIndexed { setIndex, dataSet ->
        dataSet.data.forEachIndexed { dataIndex, _ ->
            // 计算点的位置
            val x = 60f + ((canvasSize.width - 120f) / (dataSet.data.size - 1)) * dataIndex
            val y = 60f + (canvasSize.height - 120f) * (1 - dataSet.data[dataIndex].value)

            val distance = sqrt((offset.x - x).pow(2) + (offset.y - y).pow(2))

            if (distance < threshold && distance < minDistance) {
                minDistance = distance
                nearestPoint = setIndex to dataIndex
            }
        }
    }

    return nearestPoint
}

// 添加缺失的绘制函数
private fun DrawScope.drawYAxisLabels(
    padding: Float,
    height: Float,
    minValue: Float,
    maxValue: Float,
    textMeasurer: TextMeasurer,
    textColor: Color
) {
    val labelCount = 5
    val valueRange = maxValue - minValue

    for (i in 0..labelCount) {
        val value = minValue + (valueRange / labelCount) * i
        val y = padding + height - (height / labelCount) * i

        val text = textMeasurer.measure(
            text = ((value * 10).toInt() / 10.0).toString(),
            style = TextStyle(
                fontSize = 10.sp,
                color = textColor
            )
        )

        drawText(
            textLayoutResult = text,
            topLeft = Offset(
                padding - text.size.width - 8.dp.toPx(),
                y - text.size.height / 2
            )
        )
    }
}

private fun DrawScope.drawXAxisLabels(
    data: List<ChartData>,
    padding: Float,
    width: Float,
    height: Float,
    textMeasurer: TextMeasurer,
    textColor: Color
) {
    data.forEachIndexed { index, item ->
        val x = padding + (width / (data.size - 1)) * index
        val y = padding + height + 8.dp.toPx()

        val text = textMeasurer.measure(
            text = item.label,
            style = TextStyle(
                fontSize = 10.sp,
                color = textColor
            )
        )

        drawText(
            textLayoutResult = text,
            topLeft = Offset(
                x - text.size.width / 2,
                y
            )
        )
    }
}

private fun DrawScope.drawValueLabel(
    point: Offset,
    value: Float,
    textMeasurer: TextMeasurer,
    textColor: Color
) {
    val text = textMeasurer.measure(
        text = ((value * 10).toInt() / 10.0).toString(),
        style = TextStyle(
            fontSize = 9.sp,
            color = textColor,
            fontWeight = FontWeight.Medium
        )
    )

    // 背景
    drawRoundRect(
        color = Color.White.copy(alpha = 0.9f),
        topLeft = Offset(
            point.x - text.size.width / 2 - 4.dp.toPx(),
            point.y - text.size.height - 12.dp.toPx()
        ),
        size = Size(
            text.size.width + 8.dp.toPx(),
            text.size.height + 4.dp.toPx()
        ),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(4.dp.toPx())
    )

    // 文字
    drawText(
        textLayoutResult = text,
        topLeft = Offset(
            point.x - text.size.width / 2,
            point.y - text.size.height - 10.dp.toPx()
        )
    )
}