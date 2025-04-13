package com.eynnzerr.bandoristation.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.ui.tooling.preview.Preview

enum class ArrowHorizontalPosition {
    START, CENTER, END
}

@Composable
fun UnreadBubble(
    text: String,
    isArrowOnTop: Boolean = false,
    arrowPosition: ArrowHorizontalPosition = ArrowHorizontalPosition.CENTER,
    modifier: Modifier = Modifier,
    bubbleColor: Color = MaterialTheme.colorScheme.primary,
    textColor: Color = Color.White
) {
    val triangleHeight = 8.dp

    Box(
        modifier = modifier
            .wrapContentSize()
            .drawBehind {
                val cornerRadius = 16.dp.toPx()
                val triangleWidthPx = 16.dp.toPx()
                val triangleHeightPx = triangleHeight.toPx()
                val arrowMargin = 24.dp.toPx() // 当箭头在开始或结束位置时的边距

                // 计算矩形的位置和大小
                val rectStartY = if (isArrowOnTop) triangleHeightPx else 0f
                val rectHeight = if (isArrowOnTop)
                    size.height - triangleHeightPx
                else
                    size.height - triangleHeightPx

                // 绘制圆角矩形
                drawRoundRect(
                    color = bubbleColor,
                    topLeft = Offset(0f, rectStartY),
                    size = Size(size.width, rectHeight),
                    cornerRadius = CornerRadius(cornerRadius, cornerRadius),
                )

                // 根据箭头位置计算三角形的水平位置
                val triangleX = when (arrowPosition) {
                    ArrowHorizontalPosition.START -> arrowMargin - triangleWidthPx / 2
                    ArrowHorizontalPosition.CENTER -> size.width / 2 - triangleWidthPx / 2
                    ArrowHorizontalPosition.END -> size.width - arrowMargin - triangleWidthPx / 2
                }

                // 根据箭头位置绘制三角形
                if (isArrowOnTop) {
                    // 箭头在上方
                    drawPath(
                        path = Path().apply {
                            moveTo(triangleX, triangleHeightPx)
                            lineTo(triangleX + triangleWidthPx, triangleHeightPx)
                            lineTo(triangleX + triangleWidthPx / 2, 0f)
                            close()
                        },
                        color = bubbleColor
                    )
                } else {
                    // 箭头在下方
                    drawPath(
                        path = Path().apply {
                            moveTo(triangleX, rectHeight)
                            lineTo(triangleX + triangleWidthPx, rectHeight)
                            lineTo(triangleX + triangleWidthPx / 2, size.height)
                            close()
                        },
                        color = bubbleColor
                    )
                }
            }
    ) {
        // 使用Box包装Text，并调整垂直对齐方式
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .padding(
                    start = 16.dp,
                    end = 16.dp,
                    // 调整上下内边距，确保文本垂直居中
                    top = if (isArrowOnTop) triangleHeight + 4.dp else 8.dp,
                    bottom = if (isArrowOnTop) 8.dp else triangleHeight + 4.dp
                )
        ) {
            Text(
                text = text,
                color = textColor,
                fontSize = 16.sp,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }
    }
}

@Preview()
@Composable
fun UnreadBubblePreview() {
    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // 箭头在下方中间的气泡
            UnreadBubble(
                text = "99",
                isArrowOnTop = false,
                arrowPosition = ArrowHorizontalPosition.CENTER,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(vertical = 16.dp)
            )

            // 箭头在上方左侧的气泡
            UnreadBubble(
                text = "15",
                isArrowOnTop = true,
                arrowPosition = ArrowHorizontalPosition.START,
                bubbleColor = MaterialTheme.colorScheme.secondary,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(vertical = 16.dp)
            )

            // 箭头在下方右侧的气泡
            UnreadBubble(
                text = "42",
                isArrowOnTop = false,
                arrowPosition = ArrowHorizontalPosition.END,
                bubbleColor = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(vertical = 16.dp)
            )
        }
    }
}