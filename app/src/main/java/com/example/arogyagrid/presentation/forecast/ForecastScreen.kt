package com.example.arogyagrid.presentation.forecast

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.arogyagrid.core.UserSession
import com.example.arogyagrid.presentation.components.CommandTopBar
import com.example.arogyagrid.presentation.components.RiskBadge
import com.example.arogyagrid.presentation.theme.PrimaryTeal
import com.example.arogyagrid.presentation.theme.SlateBorder
import com.example.arogyagrid.presentation.theme.StatusRed
import com.example.arogyagrid.presentation.theme.TextDark
import com.example.arogyagrid.presentation.theme.TextMuted
import com.example.arogyagrid.presentation.viewmodel.ForecastViewModel

@Composable
fun ForecastScreen(
    viewModel: ForecastViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        CommandTopBar(
            roleLabel = UserSession.userRole ?: "District Officer",
            districtName = UserSession.userDistrict ?: "Jaipur",
            searchQuery = searchQuery,
            onSearchQueryChange = { searchQuery = it },
            onSyncClick = { viewModel.loadForecast() }
        )

        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Demand Forecasting Engine",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )
            Text(
                text = "7-day predicted demand horizon & consumption trends",
                fontSize = 12.sp,
                color = TextMuted
            )

            Spacer(modifier = Modifier.height(14.dp))

            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = PrimaryTeal)
                }
            } else if (uiState.error != null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = uiState.error ?: "Error loading forecast", color = StatusRed, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(onClick = { viewModel.loadForecast() }) {
                            Text("Retry")
                        }
                    }
                }
            } else {
                val filteredItems = uiState.items.filter {
                    searchQuery.isBlank() ||
                            it.medicineName.contains(searchQuery, ignoreCase = true) ||
                            it.facilityName.contains(searchQuery, ignoreCase = true)
                }

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredItems, key = { "${it.facilityId}_${it.medicineId}" }) { item ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(SlateBorder)),
                            elevation = CardDefaults.cardElevation(2.dp),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = item.medicineName,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp,
                                            color = TextDark
                                        )
                                        Text(
                                            text = item.facilityName,
                                            fontSize = 11.sp,
                                            color = TextMuted
                                        )
                                    }
                                    RiskBadge(riskLevel = item.trendDirection)
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text("CURRENT DAILY", fontSize = 9.sp, color = TextMuted, fontWeight = FontWeight.Bold)
                                        Text("${item.currentDailyConsumption}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextDark)
                                    }
                                    Column {
                                        Text("PREDICTED DAILY", fontSize = 9.sp, color = TextMuted, fontWeight = FontWeight.Bold)
                                        Text("${item.predictedDailyConsumption}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = PrimaryTeal)
                                    }
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text("STOCKOUT IN", fontSize = 9.sp, color = TextMuted, fontWeight = FontWeight.Bold)
                                        Text("${item.predictedDaysToStockout} days", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextDark)
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))
                                HorizontalDivider(color = SlateBorder)
                                Spacer(modifier = Modifier.height(8.dp))

                                Text("7-DAY FORECAST (UNITS)", fontSize = 9.sp, color = TextMuted, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    item.weeklyForecast.forEachIndexed { idx, valUnit ->
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text("D${idx + 1}", fontSize = 9.sp, color = TextMuted)
                                            Text("${valUnit.toInt()}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextDark)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
