package com.example.arogyagrid.presentation.forecast

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.arogyagrid.R
import com.example.arogyagrid.core.UserSession
import com.example.arogyagrid.presentation.components.CommandTopBar
import com.example.arogyagrid.presentation.components.RiskBadge
import com.example.arogyagrid.presentation.theme.GovtNavy
import com.example.arogyagrid.presentation.theme.GovtNavyLight
import com.example.arogyagrid.presentation.theme.PrimaryTeal
import com.example.arogyagrid.presentation.theme.SlateBorder
import com.example.arogyagrid.presentation.theme.StatusOrange
import com.example.arogyagrid.presentation.theme.StatusOrangeBg
import com.example.arogyagrid.presentation.theme.StatusRed
import com.example.arogyagrid.presentation.theme.StatusRedBg
import com.example.arogyagrid.presentation.theme.TextDark
import com.example.arogyagrid.presentation.theme.TextMuted
import com.example.arogyagrid.presentation.theme.TirangaGreen
import com.example.arogyagrid.presentation.theme.TirangaGreenLight
import com.example.arogyagrid.presentation.theme.TirangaSaffron
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

        Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
            // Official Section Masthead
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color.White)
                        .border(1.dp, SlateBorder, RoundedCornerShape(4.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_emblem_lion),
                        contentDescription = "Emblem",
                        modifier = Modifier.size(26.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "आईडीएसपी मौसमी मांग व प्रकोप प्रक्षेपण • DEMAND FORECAST",
                        fontSize = 14.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = GovtNavy
                    )
                    Text(
                        text = "राष्ट्रीय स्वास्थ्य मिशन (NHM) • 7-दिवसीय उपभोग प्रवृत्ति, मांग वृद्धि एवं स्टॉकआउट विश्लेषण",
                        fontSize = 10.5.sp,
                        color = TextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = GovtNavy)
                }
            } else if (uiState.error != null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = uiState.error ?: "Error loading forecast", color = StatusRed, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = { viewModel.loadForecast() },
                            shape = RoundedCornerShape(4.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = GovtNavy)
                        ) {
                            Text("पुनः प्रयास करें (Retry)")
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
                            border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(SlateBorder)),
                            elevation = CardDefaults.cardElevation(1.dp),
                            shape = RoundedCornerShape(4.dp),
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
                                                shape = RoundedCornerShape(2.dp),
                                                color = GovtNavyLight
                                            ) {
                                                Text(
                                                    text = "EDL-${(item.medicineName.hashCode() % 899 + 100).let { if (it < 0) -it else it }}",
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = GovtNavy,
                                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = item.medicineName,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.5.sp,
                                                color = GovtNavy
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = item.facilityName,
                                            fontSize = 11.sp,
                                            color = TextMuted
                                        )
                                    }
                                    RiskBadge(riskLevel = item.trendDirection)
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Surface(
                                    shape = RoundedCornerShape(3.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                    border = androidx.compose.foundation.BorderStroke(0.5.dp, SlateBorder),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 10.dp, vertical = 6.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column {
                                            Text("वर्तमान दैनिक खपत", fontSize = 9.sp, color = TextMuted)
                                            Text("${item.currentDailyConsumption}/दिन", fontSize = 13.5.sp, fontWeight = FontWeight.Bold, color = TextDark)
                                        }
                                        Column {
                                            Text("अनुमानित दैनिक मांग", fontSize = 9.sp, color = TextMuted)
                                            Text("${item.predictedDailyConsumption}/दिन", fontSize = 13.5.sp, fontWeight = FontWeight.Bold, color = if (item.predictedDailyConsumption > item.currentDailyConsumption * 1.5) StatusRed else GovtNavy)
                                        }
                                        Column(horizontalAlignment = Alignment.End) {
                                            Text("स्टॉकआउट संभावित", fontSize = 9.sp, color = TextMuted)
                                            Text(
                                                "${item.predictedDaysToStockout} दिन",
                                                fontSize = 13.5.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (item.predictedDaysToStockout <= 5) StatusRed else TirangaGreen
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))
                                HorizontalDivider(color = SlateBorder, thickness = 0.5.dp)
                                Spacer(modifier = Modifier.height(6.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("7-दिवसीय उपभोग प्रक्षेपण (दैनिक यूनिट):", fontSize = 9.5.sp, color = TextMuted, fontWeight = FontWeight.SemiBold)
                                    Text("IDSP-ARIMA टाइम सीरीज v2.4", fontSize = 9.sp, color = GovtNavy, fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    item.weeklyForecast.forEachIndexed { idx, valUnit ->
                                        Surface(
                                            shape = RoundedCornerShape(2.dp),
                                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                                            border = androidx.compose.foundation.BorderStroke(0.5.dp, SlateBorder)
                                        ) {
                                            Column(
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                            ) {
                                                Text("दिन ${idx + 1}", fontSize = 8.5.sp, color = TextMuted)
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
}
