package com.example.arogyagrid.presentation.forecast

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.arogyagrid.core.LanguageManager
import com.example.arogyagrid.core.UserSession
import com.example.arogyagrid.presentation.components.CommandTopBar
import com.example.arogyagrid.presentation.components.RiskBadge
import com.example.arogyagrid.presentation.theme.*
import com.example.arogyagrid.presentation.viewmodel.ForecastViewModel

@Composable
fun ForecastScreen(
    viewModel: ForecastViewModel = viewModel(),
    onNavigateToProfile: () -> Unit = {},
    onLogout: () -> Unit = {},
    onNavigateToLogin: () -> Unit = {}
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
            onSyncClick = { viewModel.loadForecast() },
            onProfileClick = onNavigateToProfile,
            onLogoutClick = onLogout,
            onLoginClick = onNavigateToLogin
        )

        Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
            // Official Section Masthead (Clean & Organized)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                        contentDescription = "Forecast",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = LanguageManager.s("आईडीएसपी मौसमी मांग व प्रकोप प्रक्षेपण • DEMAND FORECAST", "IDSP Epidemic Demand Projections • DEMAND FORECAST"),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = LanguageManager.s("राष्ट्रीय स्वास्थ्य मिशन (NHM) • 7-दिवसीय उपभोग प्रवृत्ति, मांग वृद्धि एवं स्टॉकआउट विश्लेषण", "National Health Mission • 7-Day ARIMA Trend, Surge & Stockout Analysis"),
                        fontSize = 10.5.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            } else if (uiState.error != null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = uiState.error ?: "Error loading forecast", color = StatusRed, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = { viewModel.loadForecast() },
                            shape = RoundedCornerShape(4.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            )
                        ) {
                            Text(LanguageManager.s("पुनः प्रयास करें (Retry)", "Retry Connection"))
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
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredItems, key = { "${it.facilityId}_${it.medicineId}" }) { item ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(MaterialTheme.colorScheme.outline)),
                            elevation = CardDefaults.cardElevation(1.dp),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Surface(
                                                shape = RoundedCornerShape(3.dp),
                                                color = MaterialTheme.colorScheme.primaryContainer
                                            ) {
                                                Text(
                                                    text = "EDL-${(item.medicineName.hashCode() % 899 + 100).let { if (it < 0) -it else it }}",
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = item.medicineName,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = item.facilityName,
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    RiskBadge(riskLevel = item.trendDirection)
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                    border = androidx.compose.foundation.BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 10.dp, vertical = 6.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column {
                                            Text(LanguageManager.s("वर्तमान दैनिक खपत", "Daily Inflow"), fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            Text("${item.currentDailyConsumption}/${LanguageManager.s("दिन", "day")}", fontSize = 13.5.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                                        }
                                        Column {
                                            Text(LanguageManager.s("अनुमानित दैनिक मांग", "Projected Demand"), fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            Text(
                                                "${item.predictedDailyConsumption}/${LanguageManager.s("दिन", "day")}",
                                                fontSize = 13.5.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (item.predictedDailyConsumption > item.currentDailyConsumption * 1.5) StatusRed else MaterialTheme.colorScheme.primary
                                            )
                                        }
                                        Column(horizontalAlignment = Alignment.End) {
                                            Text(LanguageManager.s("स्टॉकआउट संभावित", "Stockout Horizon"), fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            Text(
                                                "${item.predictedDaysToStockout} ${LanguageManager.s("दिन", "days")}",
                                                fontSize = 13.5.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (item.predictedDaysToStockout <= 5) StatusRed else TirangaGreen
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))
                                HorizontalDivider(color = MaterialTheme.colorScheme.outline, thickness = 0.5.dp)
                                Spacer(modifier = Modifier.height(6.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = LanguageManager.s("7-दिवसीय उपभोग प्रक्षेपण (दैनिक यूनिट):", "7-Day Consumption Projections (Units):"),
                                        fontSize = 9.5.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = "IDSP-ARIMA v2.4",
                                        fontSize = 9.sp,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    item.weeklyForecast.forEachIndexed { idx, valUnit ->
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                            border = androidx.compose.foundation.BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline)
                                        ) {
                                            Column(
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                            ) {
                                                Text("${LanguageManager.s("दिन", "D")}${idx + 1}", fontSize = 8.5.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                                Text("${valUnit.toInt()}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
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
}
