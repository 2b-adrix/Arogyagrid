package com.example.arogyagrid.presentation.forecast

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.arogyagrid.domain.models.ForecastItem
import com.example.arogyagrid.presentation.components.CommandTopBar
import com.example.arogyagrid.presentation.theme.PrimaryTeal
import com.example.arogyagrid.presentation.theme.AccentBlue
import com.example.arogyagrid.presentation.theme.SlateBorder
import com.example.arogyagrid.presentation.theme.TextDark
import com.example.arogyagrid.presentation.theme.TextMuted

@Composable
fun ForecastScreen() {
    var searchQuery by remember { mutableStateOf("") }

    val forecastList = listOf(
        ForecastItem("Day 1 (Sep 10)", actualDemand = 90, predictedDemand = 92, lowerBound = 82, upperBound = 102),
        ForecastItem("Day 2 (Sep 11)", actualDemand = 90, predictedDemand = 95, lowerBound = 85, upperBound = 105),
        ForecastItem("Day 3 (Sep 12)", actualDemand = 90, predictedDemand = 88, lowerBound = 78, upperBound = 98),
        ForecastItem("Day 4 (Sep 13)", actualDemand = 90, predictedDemand = 94, lowerBound = 84, upperBound = 104),
        ForecastItem("Day 5 (Sep 14)", actualDemand = 90, predictedDemand = 90, lowerBound = 80, upperBound = 100),
        ForecastItem("Day 6 (Sep 15)", actualDemand = 90, predictedDemand = 86, lowerBound = 76, upperBound = 96),
        ForecastItem("Day 7 (Sep 16)", actualDemand = 90, predictedDemand = 85, lowerBound = 75, upperBound = 95)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        CommandTopBar(
            roleLabel = "District Officer",
            districtName = "Jaipur",
            searchQuery = searchQuery,
            onSearchQueryChange = { searchQuery = it },
            onSyncClick = {}
        )

        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Demand Forecasting Engine",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "7-day predicted demand horizon • Model: forecast-v1.0",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Beautiful Forecast Chart
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(MaterialTheme.colorScheme.outline)),
                elevation = CardDefaults.cardElevation(2.dp),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Predicted Consumption Curve (7 Days)", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = PrimaryTeal)
                    Spacer(modifier = Modifier.height(16.dp))

                    Canvas(modifier = Modifier.fillMaxWidth().height(120.dp)) {
                        val maxDemand = 110f
                        val minDemand = 70f
                        val range = maxDemand - minDemand
                        
                        val stepX = size.width / (forecastList.size - 1)
                        val points = forecastList.mapIndexed { index, item ->
                            val normalizedY = 1f - ((item.predictedDemand - minDemand) / range)
                            Offset(x = index * stepX, y = normalizedY * size.height)
                        }

                        val path = Path().apply {
                            moveTo(points.first().x, points.first().y)
                            for (i in 1 until points.size) {
                                val current = points[i]
                                val previous = points[i - 1]
                                val controlX1 = previous.x + (current.x - previous.x) / 2
                                val controlX2 = current.x - (current.x - previous.x) / 2
                                cubicTo(controlX1, previous.y, controlX2, current.y, current.x, current.y)
                            }
                        }

                        drawPath(
                            path = path,
                            color = AccentBlue,
                            style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
                        )

                        points.forEach { point ->
                            drawCircle(
                                color = PrimaryTeal,
                                radius = 6.dp.toPx(),
                                center = point
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Day 1", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                        Text("Day 4", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                        Text("Day 7", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text("Forecast Details (Amoxicillin 500mg)", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(forecastList) { item ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(MaterialTheme.colorScheme.outline)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(item.day, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                                Text("Bounds: ${item.lowerBound} - ${item.upperBound} units", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                            }
                            Text(
                                text = "${item.predictedDemand} units",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryTeal
                            )
                        }
                    }
                }
            }
        }
    }
}
