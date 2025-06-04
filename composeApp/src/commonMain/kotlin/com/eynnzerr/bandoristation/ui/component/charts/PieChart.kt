package com.eynnzerr.bandoristation.ui.component.charts

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import kotlin.math.*

@Composable
fun PieChart(
    dataSet: ChartDataSet,
    modifier: Modifier = Modifier,
    config: ChartConfig = ChartConfig(),
    height: Dp = 300.dp,
    donutMode: Boolean = false,
    donutWidth: Float = 0.3f,
    startAngle: Float = -90f,
    sliceSpacing: Float = 2f,
    onSliceClick: ((ChartData) -> Unit)? = null
) {
    var selectedSlice by remember { mutableStateOf<Int?>(null) }
    var hoveredSlice by remember { mutableStateOf<Int?>(null) }

    val density = LocalDensity.current
    val textMeasurer = rememberTextMeasurer()

    // 主动画进度
    val animationProgress by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(
            durationMillis = config.animationDuration,
            easing = FastOutSlowInEasing
        )
    )

    // 旋转动画
    val rotationAnimation by animateFloatAsState(
        targetValue = 360f,
        animationSpec = tween(
            durationMillis = config.animationDuration * 2,
            easing = LinearEasing
        )
    )

    // 为每个切片创建独立的动画
    val sliceAnimations = dataSet.data.mapIndexed { index, _ ->
        animateFloatAsState(
            targetValue = if (selectedSlice == index) 1.1f else 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
    }

    val total = dataSet.data.sumOf { it.value.toDouble() }.toFloat()
    val colors = dataSet.data.mapIndexed { index, data ->
        data.color ?: generateChartColors(dataSet.data.size)[index]
    }

    Box (
        modifier = modifier
            .height(height)
            .clip(RoundedCornerShape(16.dp))
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .background(config.backgroundColor)
                .pointerInput(Unit) {
                    detectTapGestures { offset ->
                        val canvasSize = Size(this.size.width.toFloat(), this.size.height.toFloat())
                        findClickedSlice(
                            offset,
                            canvasSize,
                            dataSet.data,
                            total,
                            startAngle,
                            donutMode,
                            donutWidth
                        )?.let { index ->
                            selectedSlice = if (selectedSlice == index) null else index
                            onSliceClick?.invoke(dataSet.data[index])
                        }
                    }
                }
        ) {
            val centerX = this.size.width / 2
            val centerY = this.size.height / 2
            val radius = min(centerX, centerY) * 0.8f

            // 绘制切片
            var currentAngle = startAngle

            dataSet.data.forEachIndexed { index, item ->
                val sweepAngle = (item.value / total) * 360f * animationProgress
                val isSelected = selectedSlice == index
                val scale = sliceAnimations[index].value

                // 计算偏移
                val angleRad = (currentAngle + sweepAngle / 2) * PI / 180f
                val offsetX = if (isSelected) cos(angleRad).toFloat() * 10.dp.toPx() else 0f
                val offsetY = if (isSelected) sin(angleRad).toFloat() * 10.dp.toPx() else 0f

                // 绘制切片
                drawSlice(
                    center = Offset(centerX + offsetX, centerY + offsetY),
                    radius = radius * scale,
                    startAngle = currentAngle,
                    sweepAngle = sweepAngle - sliceSpacing,
                    color = colors[index],
                    donutMode = donutMode,
                    donutWidth = donutWidth,
                    isSelected = isSelected
                )

                // 绘制标签
                if (config.showValues && sweepAngle > 10f) {
                    val labelAngleRad = (currentAngle + sweepAngle / 2) * PI / 180f
                    val labelRadius = if (donutMode) {
                        radius * (1 - donutWidth / 2)
                    } else {
                        radius * 0.7f
                    }

                    val labelX = centerX + cos(labelAngleRad).toFloat() * labelRadius + offsetX
                    val labelY = centerY + sin(labelAngleRad).toFloat() * labelRadius + offsetY

                    drawSliceLabel(
                        center = Offset(labelX, labelY),
                        value = item.value,
                        percentage = (item.value / total * 100),
                        textMeasurer = textMeasurer,
                        color = if (isLightColor(colors[index])) Color.Black else Color.White
                    )
                }

                currentAngle += sweepAngle
            }

            // 中心装饰（仅在甜甜圈模式）
            if (donutMode) {
                drawCircle(
                    color = config.backgroundColor,
                    radius = radius * donutWidth,
                    center = Offset(centerX, centerY),
                    style = Fill
                )

                // 中心文字
                if (config.showValues) {
                    val centerText = textMeasurer.measure(
                        text = "总计\n${total.toInt()}",
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = config.labelTextColor,
                            textAlign = TextAlign.Center
                        )
                    )

                    drawText(
                        textLayoutResult = centerText,
                        topLeft = Offset(
                            centerX - centerText.size.width / 2,
                            centerY - centerText.size.height / 2
                        )
                    )
                }
            }
        }

        // 图例
        if (config.showLegend) {
            PieChartLegend(
                dataSet = dataSet,
                colors = colors,
                total = total,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
            )
        }
    }
}

private fun DrawScope.drawSlice(
    center: Offset,
    radius: Float,
    startAngle: Float,
    sweepAngle: Float,
    color: Color,
    donutMode: Boolean,
    donutWidth: Float,
    isSelected: Boolean
) {
    if (donutMode) {
        // 甜甜圈模式
        val innerRadius = radius * donutWidth

        // 外圈
        drawArc(
            color = color,
            startAngle = startAngle,
            sweepAngle = sweepAngle,
            useCenter = false,
            topLeft = Offset(center.x - radius, center.y - radius),
            size = Size(radius * 2, radius * 2),
            style = Stroke(
                width = radius - innerRadius,
                cap = StrokeCap.Butt
            )
        )

        // 选中效果
        if (isSelected) {
            drawArc(
                color = Color.White.copy(alpha = 0.3f),
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = false,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = Size(radius * 2, radius * 2),
                style = Stroke(
                    width = radius - innerRadius,
                    cap = StrokeCap.Butt
                )
            )
        }
    } else {
        // 实心饼图
        drawArc(
            color = color,
            startAngle = startAngle,
            sweepAngle = sweepAngle,
            useCenter = true,
            topLeft = Offset(center.x - radius, center.y - radius),
            size = Size(radius * 2, radius * 2)
        )

        // 选中效果
        if (isSelected) {
            drawArc(
                color = Color.White.copy(alpha = 0.3f),
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = true,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = Size(radius * 2, radius * 2)
            )

            // 边框
            drawArc(
                color = color.copy(alpha = 0.8f),
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = true,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = Size(radius * 2, radius * 2),
                style = Stroke(width = 2.dp.toPx())
            )
        }
    }
}

private fun DrawScope.drawSliceLabel(
    center: Offset,
    value: Float,
    percentage: Float,
    textMeasurer: TextMeasurer,
    color: Color
) {
    val text = textMeasurer.measure(
        text = "${percentage.toInt()}%",
        style = TextStyle(
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
    )

    drawText(
        textLayoutResult = text,
        topLeft = Offset(
            center.x - text.size.width / 2,
            center.y - text.size.height / 2
        )
    )
}

@Composable
private fun PieChartLegend(
    dataSet: ChartDataSet,
    colors: List<Color>,
    total: Float,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        dataSet.data.take(4).forEachIndexed { index, item ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(colors[index])
                )

                Spacer(modifier = Modifier.width(4.dp))

                Column {
                    Text(
                        text = item.label,
                        style = MaterialTheme.typography.labelSmall,
                        maxLines = 1
                    )
                    Text(
                        text = "${(item.value / total * 100).toInt()}%",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }
    }
}

private fun findClickedSlice(
    offset: Offset,
    canvasSize: Size,
    data: List<ChartData>,
    total: Float,
    startAngle: Float,
    donutMode: Boolean,
    donutWidth: Float
): Int? {
    val centerX = canvasSize.width / 2
    val centerY = canvasSize.height / 2
    val radius = min(centerX, centerY) * 0.8f

    // 计算点击位置相对于中心的角度和距离
    val dx = offset.x - centerX
    val dy = offset.y - centerY
    val distance = sqrt(dx * dx + dy * dy)

    // 检查是否在有效范围内
    if (donutMode) {
        val innerRadius = radius * donutWidth
        if (distance < innerRadius || distance > radius) return null
    } else {
        if (distance > radius) return null
    }

    // 计算角度 - 修复：使用Kotlin的数学函数
    var angle = atan2(dy, dx) * 180 / PI.toFloat()
    angle = (angle - startAngle + 360) % 360

    // 找出对应的切片
    var currentAngle = 0f
    data.forEachIndexed { index, item ->
        val sweepAngle = (item.value / total) * 360f
        if (angle >= currentAngle && angle < currentAngle + sweepAngle) {
            return index
        }
        currentAngle += sweepAngle
    }

    return null
}

private fun isLightColor(color: Color): Boolean {
    val r = color.red
    val g = color.green
    val b = color.blue
    val luminance = 0.299 * r + 0.587 * g + 0.114 * b
    return luminance > 0.5
}
