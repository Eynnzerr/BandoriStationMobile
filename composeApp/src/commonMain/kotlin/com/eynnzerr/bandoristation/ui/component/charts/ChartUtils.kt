package com.eynnzerr.bandoristation.ui.component.charts

import androidx.compose.animation.core.*
import androidx.compose.runtime.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.dp
import kotlin.math.*

object ChartAnimations {
    @Composable
    fun animateFloatAsState(
        targetValue: Float,
        animationSpec: AnimationSpec<Float> = tween(
            durationMillis = 800,
            easing = FastOutSlowInEasing
        )
    ): State<Float> = androidx.compose.animation.core.animateFloatAsState(
        targetValue = targetValue,
        animationSpec = animationSpec
    )
}

fun generateChartColors(count: Int): List<Color> {
    val baseColors = listOf(
        Color(0xFF6200EE), // Primary
        Color(0xFF03DAC6), // Secondary
        Color(0xFFFF6B6B), // Red
        Color(0xFF4ECDC4), // Teal
        Color(0xFF45B7D1), // Blue
        Color(0xFFF7DC6F), // Yellow
        Color(0xFFBB8FCE), // Purple
        Color(0xFF85C1AE), // Green
        Color(0xFFF8B195), // Peach
        Color(0xFF95A5A6)  // Gray
    )

    return List(count) { index ->
        baseColors[index % baseColors.size]
    }
}

// 贝塞尔曲线平滑算法
fun createSmoothPath(points: List<Offset>): Path {
    val path = Path()
    if (points.isEmpty()) return path

    path.moveTo(points.first().x, points.first().y)

    if (points.size == 1) return path

    for (i in 1 until points.size) {
        val p0 = points[max(0, i - 1)]
        val p1 = points[i]
        val p2 = points[min(points.size - 1, i + 1)]
        val p3 = points[min(points.size - 1, i + 2)]

        val cp1x = p1.x + (p2.x - p0.x) / 6f
        val cp1y = p1.y + (p2.y - p0.y) / 6f
        val cp2x = p2.x - (p3.x - p1.x) / 6f
        val cp2y = p2.y - (p3.y - p1.y) / 6f

        path.cubicTo(cp1x, cp1y, cp2x, cp2y, p2.x, p2.y)
    }

    return path
}

// 创建阶梯路径
fun createSteppedPath(points: List<Offset>): Path {
    val path = Path()
    if (points.isEmpty()) return path

    path.moveTo(points.first().x, points.first().y)

    for (i in 1 until points.size) {
        val prev = points[i - 1]
        val curr = points[i]
        path.lineTo(curr.x, prev.y)
        path.lineTo(curr.x, curr.y)
    }

    return path
}

fun DrawScope.drawGrid(
    padding: Float,
    width: Float,
    height: Float,
    color: Color,
    orientation: BarOrientation? = null
) {
    val horizontalLines = 5
    val verticalLines = 7

    // 水平网格线
    for (i in 0..horizontalLines) {
        val y = padding + (height / horizontalLines) * i
        drawLine(
            color = color,
            start = Offset(padding, y),
            end = Offset(padding + width, y),
            strokeWidth = 1.dp.toPx(),
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 5f))
        )
    }

    // 垂直网格线 - 对于水平柱状图可能需要不同的间隔
    val lines = if (orientation == BarOrientation.HORIZONTAL) horizontalLines else verticalLines
    for (i in 0..lines) {
        val x = padding + (width / lines) * i
        drawLine(
            color = color,
            start = Offset(x, padding),
            end = Offset(x, padding + height),
            strokeWidth = 1.dp.toPx(),
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 5f))
        )
    }
}

fun DrawScope.drawAxes(
    padding: Float,
    width: Float,
    height: Float,
    color: Color
) {
    // X轴
    drawLine(
        color = color,
        start = Offset(padding, padding + height),
        end = Offset(padding + width, padding + height),
        strokeWidth = 2.dp.toPx()
    )

    // Y轴
    drawLine(
        color = color,
        start = Offset(padding, padding),
        end = Offset(padding, padding + height),
        strokeWidth = 2.dp.toPx()
    )
}