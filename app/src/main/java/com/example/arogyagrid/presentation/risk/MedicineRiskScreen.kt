package com.example.arogyagrid.presentation.risk

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.ui.res.painterResource
import com.example.arogyagrid.R
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.arogyagrid.presentation.components.CommandTopBar
import com.example.arogyagrid.presentation.components.RiskBadge
import com.example.arogyagrid.presentation.components.SyntheticDataBanner
import com.example.arogyagrid.presentation.dashboard.DashboardViewModel
import com.example.arogyagrid.presentation.theme.AccentBlue
import com.example.arogyagrid.presentation.theme.AccentBlueLight
import com.example.arogyagrid.presentation.theme.PrimaryTeal
import com.example.arogyagrid.presentation.theme.SlateBorder
import com.example.arogyagrid.presentation.theme.SlateCardVariant
import com.example.arogyagrid.presentation.theme.StatusRed
import com.example.arogyagrid.presentation.theme.TextDark
import com.example.arogyagrid.presentation.theme.TextMuted

@Composable
fun MedicineRiskScreen(
    viewModel: DashboardViewModel
) {
    val summary by viewModel.summaryState.collectAsState()
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
            onSyncClick = {}
        )

        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.ic_risk_shield),
                    contentDescription = null,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Medicine Risk Intelligence",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )
                    Text(
                        text = "Stock coverage, 7-day demand predictions & stock-out probability",
                        fontSize = 12.sp,
                        color = TextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Filter Chips
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(filters) { filter ->
                    FilterChip(
                        selected = selectedFilter == filter,
                        onClick = { selectedFilter = filter },
                        label = { Text(filter, fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            val risks = summary?.topRisks?.filter {
                (selectedFilter == "ALL" || it.riskLevel.equals(selectedFilter, ignoreCase = true)) &&
                (searchQuery.isBlank() || it.medicineName.contains(searchQuery, ignoreCase = true))
            } ?: emptyList()

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(risks, key = { "${it.facilityId}_${it.medicineId}" }) { risk ->
                    val isCritical = risk.riskLevel == "CRITICAL"
                    val isHigh = risk.riskLevel == "HIGH"
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
                        border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(SlateBorder)),
                        elevation = CardDefaults.cardElevation(2.dp),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .animateContentSize()
                            .animateItem()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = risk.medicineName,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextDark
                                    )
                                    Text(
                                        text = risk.facilityName,
                                        fontSize = 11.sp,
                                        color = TextMuted
                                    )
                                }
                                RiskBadge(riskLevel = risk.riskLevel)
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Probability Progress Bar
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Stockout Probability", fontSize = 11.sp, color = TextMuted)
                                Text("${(animatedProb * 100).toInt()}%", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (isCritical) StatusRed else PrimaryTeal)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            LinearProgressIndicator(
                                progress = { animatedProb },
                                color = if (isCritical) StatusRed else PrimaryTeal,
                                trackColor = SlateCardVariant,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            // AI Mini Insight Pill
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(AccentBlueLight)
                                    .padding(10.dp)
                            ) {
                                Row(verticalAlignment = Alignment.Top) {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = "AI Insight",
                                        tint = AccentBlue,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text("Grounded AI Risk Insight", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = AccentBlue)
                                        Text(
                                            text = risk.reason,
                                            fontSize = 11.sp,
                                            color = Color.DarkGray
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
@Preview(showBackground = true)
@Composable
fun MedicineRiskScreen() {
    MaterialTheme {
        CommandTopBar(
            searchQuery = "",
            onSearchQueryChange = {}
        )
    }
}
