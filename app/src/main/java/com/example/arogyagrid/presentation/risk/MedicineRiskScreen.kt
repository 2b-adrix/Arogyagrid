package com.example.arogyagrid.presentation.risk

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
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
import com.example.arogyagrid.presentation.components.CommandTopBar
import com.example.arogyagrid.presentation.components.RiskBadge
import com.example.arogyagrid.presentation.components.SyntheticDataBanner
import com.example.arogyagrid.presentation.theme.AccentBlue
import com.example.arogyagrid.presentation.theme.AccentBlueLight
import com.example.arogyagrid.presentation.theme.GovtNavy
import com.example.arogyagrid.presentation.theme.GovtNavyLight
import com.example.arogyagrid.presentation.theme.PrimaryTeal
import com.example.arogyagrid.presentation.theme.SlateBorder
import com.example.arogyagrid.presentation.theme.SlateCardVariant
import com.example.arogyagrid.presentation.theme.StatusOrange
import com.example.arogyagrid.presentation.theme.StatusOrangeBg
import com.example.arogyagrid.presentation.theme.StatusRed
import com.example.arogyagrid.presentation.theme.StatusRedBg
import com.example.arogyagrid.presentation.theme.TextDark
import com.example.arogyagrid.presentation.theme.TextMuted
import com.example.arogyagrid.presentation.theme.TirangaGreen
import com.example.arogyagrid.presentation.theme.TirangaGreenLight
import com.example.arogyagrid.presentation.theme.TirangaSaffron
import com.example.arogyagrid.presentation.viewmodel.RiskViewModel

@Composable
fun MedicineRiskScreen(
    viewModel: RiskViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedFilter by remember { mutableStateOf("ALL") }
    var searchQuery by remember { mutableStateOf("") }

    val filters = listOf("ALL", "CRITICAL", "HIGH", "MEDIUM", "LOW")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        SyntheticDataBanner()

        CommandTopBar(
            roleLabel = "District Officer",
            districtName = "Jaipur",
            searchQuery = searchQuery,
            onSearchQueryChange = { searchQuery = it },
            onSyncClick = { viewModel.loadRisks() }
        )

        Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
            // Official Section Header
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
                        text = "औषधि स्टॉकआउट जोखिम मैट्रिक्स • DEFICIT MATRIX",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = GovtNavy
                    )
                    Text(
                        text = "आरएमएससीएल आवश्यक औषधि सूची (EDL) स्टॉकआउट प्रायिकता एवं अग्रिम आपूर्ति चेतावनी",
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Filter Chips (Government Portal Style)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                items(filters) { filter ->
                    val filterLabel = when (filter) {
                        "CRITICAL" -> "गंभीर संकट (Critical)"
                        "HIGH" -> "उच्च जोखिम (High)"
                        "MEDIUM" -> "मध्यम (Medium)"
                        "LOW" -> "सामान्य (Low)"
                        else -> "समस्त (All)"
                    }
                    FilterChip(
                        selected = selectedFilter == filter,
                        onClick = { selectedFilter = filter },
                        shape = RoundedCornerShape(4.dp),
                        label = { Text(filterLabel, fontSize = 10.5.sp, fontWeight = FontWeight.Bold) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = if (filter == "CRITICAL") StatusRed else GovtNavy,
                            selectedLabelColor = Color.White
                        )
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
                        Text(text = uiState.error ?: "Error loading risk data", color = StatusRed, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = { viewModel.loadRisks() },
                            shape = RoundedCornerShape(4.dp),
                            colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = GovtNavy)
                        ) {
                            Text("पुनः प्रयास करें (Retry)")
                        }
                    }
                }
            } else {
                val risks = uiState.items.filter {
                    (selectedFilter == "ALL" || it.severity.equals(selectedFilter, ignoreCase = true)) &&
                            (searchQuery.isBlank() || it.medicineName.contains(searchQuery, ignoreCase = true) || it.facilityName.contains(searchQuery, ignoreCase = true))
                }

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(risks, key = { it.id }) { risk ->
                        val isCritical = risk.severity.equals("CRITICAL", ignoreCase = true)
                        val isHigh = risk.severity.equals("HIGH", ignoreCase = true)
                        val targetProb = if (isCritical) 0.88f else if (isHigh) 0.65f else 0.25f

                        val animatedProb by animateFloatAsState(
                            targetValue = targetProb,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessLow
                            ),
                            label = "RiskProbability"
                        )

                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(if (isCritical) StatusRed else SlateBorder)),
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
                                                    text = "EDL-${(risk.medicineName.hashCode() % 899 + 100).let { if (it < 0) -it else it }}",
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = GovtNavy,
                                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = risk.medicineName,
                                                fontSize = 14.5.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = GovtNavy
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = risk.facilityName,
                                            fontSize = 11.sp,
                                            color = TextMuted
                                        )
                                    }
                                    RiskBadge(riskLevel = risk.severity)
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
                                            Text("स्टॉक समाप्ति शेष", fontSize = 9.sp, color = TextMuted)
                                            Text("${risk.daysToStockout} दिन", fontSize = 13.5.sp, fontWeight = FontWeight.Bold, color = if (isCritical) StatusRed else TextDark)
                                        }
                                        Column {
                                            Text("जोखिम सूचकांक", fontSize = 9.sp, color = TextMuted)
                                            Text("${risk.riskScore}", fontSize = 13.5.sp, fontWeight = FontWeight.Bold, color = if (isCritical) StatusRed else GovtNavy)
                                        }
                                        Column(horizontalAlignment = Alignment.End) {
                                            Text("स्टॉक घाटा (Deficit)", fontSize = 9.sp, color = TextMuted)
                                            Text("${risk.deficit} यूनिट", fontSize = 13.5.sp, fontWeight = FontWeight.Bold, color = if (isCritical) StatusRed else StatusOrange)
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "वर्तमान स्टॉक: ${risk.currentStock} | आवश्यक मानक: ${risk.requiredStock}",
                                        fontSize = 10.5.sp,
                                        color = TextMuted
                                    )
                                    Text(
                                        text = "स्टॉकआउट प्रायिकता: ${(animatedProb * 100).toInt()}%",
                                        fontSize = 10.5.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (isCritical) StatusRed else GovtNavy
                                    )
                                }

                                Spacer(modifier = Modifier.height(6.dp))
                                LinearProgressIndicator(
                                    progress = { animatedProb },
                                    color = if (isCritical) StatusRed else if (isHigh) StatusOrange else TirangaGreen,
                                    trackColor = SlateCardVariant,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(4.dp)
                                        .clip(RoundedCornerShape(2.dp))
                                )

                                if (!risk.recommendation.isNullOrEmpty()) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Surface(
                                        shape = RoundedCornerShape(3.dp),
                                        color = GovtNavyLight.copy(alpha = 0.6f),
                                        border = androidx.compose.foundation.BorderStroke(0.5.dp, GovtNavy.copy(alpha = 0.2f)),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(8.dp),
                                            verticalAlignment = Alignment.Top
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.AutoAwesome,
                                                contentDescription = "Advisory",
                                                tint = GovtNavy,
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Column {
                                                Text("आरएमएससीएल क्लिनिकल निर्देश / CMHO परामर्श:", fontSize = 9.5.sp, fontWeight = FontWeight.Bold, color = GovtNavy)
                                                Text(
                                                    text = risk.recommendation ?: "",
                                                    fontSize = 10.5.sp,
                                                    color = TextDark
                                                )
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
