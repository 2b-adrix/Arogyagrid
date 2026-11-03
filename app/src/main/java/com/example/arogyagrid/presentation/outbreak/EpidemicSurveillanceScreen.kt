package com.example.arogyagrid.presentation.outbreak

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Coronavirus
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.arogyagrid.presentation.theme.*

data class OutbreakCluster(
    val id: String,
    val diseaseCategory: String,
    val focalBlock: String,
    val affectedFacilityId: String,
    val affectedFacilityName: String,
    val triggerMedicines: List<String>,
    val baselineDailyDemand: Int,
    val current24hDemand: Int,
    val spikePercentage: Int,
    val hoursToTotalStockout: Double,
    val confidenceScore: Double,
    val donorHubRecommendation: String,
    val severity: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EpidemicSurveillanceScreen(
    onNavigateBack: () -> Unit,
    onNavigateToMap: () -> Unit,
    onNavigateToTransfer: () -> Unit
) {
    val clusters = remember {
        listOf(
            OutbreakCluster(
                id = "OUTBREAK-JPR-01",
                diseaseCategory = "Acute Diarrheal Outbreak (Cholera/Gastroenteritis)",
                focalBlock = "Amer Block (Jaipur Rural)",
                affectedFacilityId = "FAC-JPR-011",
                affectedFacilityName = "PHC Amer, Jaipur",
                triggerMedicines = listOf("ORS Sachets 20.5g", "Zinc Sulfate 20mg"),
                baselineDailyDemand = 85,
                current24hDemand = 374,
                spikePercentage = 340,
                hoursToTotalStockout = 38.0,
                confidenceScore = 96.4,
                donorHubRecommendation = "SMS Hospital Jaipur has 3,731 units surplus (10.9 km distance).",
                severity = "CRITICAL"
            ),
            OutbreakCluster(
                id = "OUTBREAK-JPR-02",
                diseaseCategory = "Seasonal Viral Bronchitis & Respiratory Influx",
                focalBlock = "Chomu & Kotputli Highway Corridor",
                affectedFacilityId = "FAC-JPR-004",
                affectedFacilityName = "CHC Chomu, Jaipur",
                triggerMedicines = listOf("Amoxicillin 500mg", "Paracetamol Syrup 125mg"),
                baselineDailyDemand = 120,
                current24hDemand = 342,
                spikePercentage = 185,
                hoursToTotalStockout = 64.0,
                confidenceScore = 88.2,
                donorHubRecommendation = "SMS Hospital Jaipur has 1,840 units surplus (32.4 km distance).",
                severity = "HIGH"
            ),
            OutbreakCluster(
                id = "OUTBREAK-JPR-03",
                diseaseCategory = "Post-Monsoon Dengue & Febrile Cluster",
                focalBlock = "Sanganer Urban Sub-district",
                affectedFacilityId = "FAC-JPR-006",
                affectedFacilityName = "CHC Sanganer, Jaipur",
                triggerMedicines = listOf("IV Fluids Normal Saline 500ml", "Paracetamol Tablets"),
                baselineDailyDemand = 95,
                current24hDemand = 228,
                spikePercentage = 140,
                hoursToTotalStockout = 86.0,
                confidenceScore = 82.5,
                donorHubRecommendation = "RUHS College of Medical Sciences has 2,031 units surplus (8.1 km distance).",
                severity = "MODERATE"
            )
        )
    }

    var bulletinDialogMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Surface(
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextDark)
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Epidemic Outbreak Surveillance",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextDark
                        )
                        Text(
                            text = "Syndromic Consumption Spike Early-Warning System",
                            fontSize = 11.sp,
                            color = PrimaryTeal
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = StatusRed.copy(alpha = 0.15f)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(StatusRed, CircleShape)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "3 Spikes Detected",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = StatusRed
                            )
                        }
                    }
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = PrimaryTeal.copy(alpha = 0.08f)),
                    border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(PrimaryTeal)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.AutoMirrored.Filled.TrendingUp, contentDescription = null, tint = PrimaryTeal, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "7-Day Rolling vs 24h Demand Anomaly Model",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryTeal
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "ArogyaGrid monitors unexpected demand acceleration in essential rehydration and antibiotic supplies to flag regional outbreaks 48-72 hours before traditional clinical reporting.",
                            fontSize = 11.sp,
                            color = TextDark
                        )
                    }
                }
            }

            items(clusters) { cluster ->
                val isCritical = cluster.severity == "CRITICAL"
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = SolidColor(if (isCritical) StatusRed else SlateBorder)
                    ),
                    shape = RoundedCornerShape(14.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .background(
                                        if (isCritical) StatusRed.copy(alpha = 0.15f) else StatusOrange.copy(alpha = 0.15f),
                                        RoundedCornerShape(10.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Coronavirus,
                                    contentDescription = null,
                                    tint = if (isCritical) StatusRed else StatusOrange,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = cluster.diseaseCategory,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextDark
                                )
                                Text(
                                    text = "${cluster.affectedFacilityName} • ${cluster.focalBlock}",
                                    fontSize = 11.sp,
                                    color = TextMuted
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (isCritical) StatusRed.copy(alpha = 0.15f) else StatusOrange.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "+${cluster.spikePercentage}% SURGE",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (isCritical) StatusRed else StatusOrange,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Demand acceleration numbers
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f), RoundedCornerShape(8.dp))
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Baseline Demand", fontSize = 10.sp, color = TextMuted)
                                Text("${cluster.baselineDailyDemand} units/day", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextDark)
                            }
                            Column {
                                Text("Current 24h Influx", fontSize = 10.sp, color = TextMuted)
                                Text("${cluster.current24hDemand} units/day", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if (isCritical) StatusRed else StatusOrange)
                            }
                            Column {
                                Text("Stockout Window", fontSize = 10.sp, color = TextMuted)
                                Text("${cluster.hoursToTotalStockout.toInt()} hours", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = StatusRed)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Triggers: ${cluster.triggerMedicines.joinToString(", ")}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextDark
                        )

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Action Strategy: ${cluster.donorHubRecommendation}",
                            fontSize = 11.sp,
                            color = PrimaryTeal,
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = onNavigateToMap,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.Map, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Route Map", fontSize = 11.sp)
                            }
                            Button(
                                onClick = onNavigateToTransfer,
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryTeal),
                                modifier = Modifier.weight(1.3f)
                            ) {
                                Icon(Icons.Default.LocalShipping, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Auto-Requisition", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    if (bulletinDialogMessage != null) {
        AlertDialog(
            onDismissRequest = { bulletinDialogMessage = null },
            title = { Text("Epidemic Surveillance Action") },
            text = { Text(bulletinDialogMessage!!) },
            confirmButton = {
                Button(onClick = { bulletinDialogMessage = null }) {
                    Text("OK")
                }
            }
        )
    }
}
