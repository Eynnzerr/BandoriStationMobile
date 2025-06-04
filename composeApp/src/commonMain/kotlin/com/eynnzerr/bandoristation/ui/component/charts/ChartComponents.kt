package com.eynnzerr.bandoristation.ui.component.charts

// ChartComponents.kt
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ChartLegend(
    dataSets: List<ChartDataSet>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(12.dp)
    ) {
        dataSets.forEachIndexed { index, dataSet ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(
                            dataSet.color ?: MaterialTheme.colorScheme.primary
                        )
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = dataSet.label,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
fun ChartTooltip(
    label: String,
    value: Float,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .shadow(8.dp, RoundedCornerShape(8.dp))
            .clip(RoundedCornerShape(8.dp)),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 4.dp
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(color)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = value.toString(),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

// ChartShowcase.kt
@Composable
fun ChartShowcase() {
    val sampleData = listOf(
        ChartData("一月", 120f),
        ChartData("二月", 150f),
        ChartData("三月", 180f),
        ChartData("四月", 130f),
        ChartData("五月", 200f),
        ChartData("六月", 170f)
    )

    val multiDataSets = listOf(
        ChartDataSet(
            data = sampleData,
            label = "销售额",
            color = MaterialTheme.colorScheme.primary
        ),
        ChartDataSet(
            data = listOf(
                ChartData("一月", 100f),
                ChartData("二月", 130f),
                ChartData("三月", 160f),
                ChartData("四月", 110f),
                ChartData("五月", 180f),
                ChartData("六月", 150f)
            ),
            label = "成本",
            color = MaterialTheme.colorScheme.primary
        )
    )

    val pieData = listOf(
        ChartData("产品A", 35f),
        ChartData("产品B", 25f),
        ChartData("产品C", 20f),
        ChartData("产品D", 15f),
        ChartData("其他", 5f)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 折线图
        Card {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "折线图示例",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                LineChart(
                    dataSets = multiDataSets,
                    height = 250.dp,
                    lineType = LineType.CURVED,
                    config = ChartConfig(
                        showGrid = false,
                        showLabels = true,
                        showValues = false,
                        showLegend = false
                    ),
                    onPointClick = { dataSet, data ->
                        println("点击了 ${dataSet.label}: ${data.label} - ${data.value}")
                    }
                )
            }
        }

        // 柱状图
        Card {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "柱状图示例",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                BarChart(
                    dataSet = ChartDataSet(data = sampleData),
                    height = 250.dp,
                    barStyle = BarStyle.GRADIENT,
                    orientation = BarOrientation.VERTICAL,
                    config = ChartConfig(
                        showGrid = false,
                        showLabels = true,
                        showValues = true
                    ),
                    onBarClick = { data ->
                        println("点击了柱子: ${data.label} - ${data.value}")
                    }
                )
            }
        }

        // 饼图
        Card {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "饼图示例",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                PieChart(
                    dataSet = ChartDataSet(data = pieData),
                    height = 500.dp,
                    donutMode = false,
                    donutWidth = 0.4f,
                    config = ChartConfig(
                        showValues = true,
                        showLegend = true,
                        animationDuration = 1000,
                    ),
                    onSliceClick = { data ->
                        println("点击了切片: ${data.label} - ${data.value}")
                    }
                )
            }
        }
    }
}

@Composable
@Preview
private fun ChartsPreview() {
    MaterialTheme {
        ChartShowcase()
    }
}