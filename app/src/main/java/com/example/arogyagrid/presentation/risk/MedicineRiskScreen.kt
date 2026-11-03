package com.example.arogyagrid.presentation.risk

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.arogyagrid.core.LanguageManager
import com.example.arogyagrid.presentation.components.CommandTopBar
import com.example.arogyagrid.presentation.components.RiskBadge
import com.example.arogyagrid.presentation.components.SyntheticDataBanner
import com.example.arogyagrid.presentation.theme.*
import com.example.arogyagrid.presentation.viewmodel.RiskViewModel

@Composable
fun MedicineRiskScreen(
    viewModel: RiskViewModel = viewModel(),
    onNavigateToProfile: () -> Unit = {},
    onLogout: () -> Unit = {},
    onNavigateToLogin: () -> Unit = {}
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
            onSyncClick = { viewModel.loadRisks() },
            onProfileClick = onNavigateToProfile,
            onLogoutClick = onLogout,
            onLoginClick = onNavigateToLogin
        )

        Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
            // Official Section Header (Clean & Streamlined - No duplicate emblem)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Risk Matrix",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = LanguageManager.s("औषधि स्टॉकआउट जोखिम मैट्रिक्स • DEFICIT MATRIX", "Medicine Stockout Risk Matrix • DEFICIT MATRIX"),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = LanguageManager.s("आरएमएससीएल आवश्यक औषधि सूची (EDL) स्टॉकआउट प्रायिकता एवं अग्रिम आपूर्ति चेतावनी", "RMSCL Essential Drug List (EDL) Stockout Probability & Early Warning"),
                        fontSize = 10.5.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Filter Chips (Adaptive Material 3 Colors)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                items(filters) { filter ->
                    val filterLabel = when (filter) {
                        "CRITICAL" -> LanguageManager.s("गंभीर संकट (Critical)", "Critical Deficit")
                        "HIGH" -> LanguageManager.s("उच्च जोखिम (High)", "High Risk")
                        "MEDIUM" -> LanguageManager.s("मध्यम (Medium)", "Medium Risk")
                        "LOW" -> LanguageManager.s("सामान्य (Low)", "Low Risk")
                        else -> LanguageManager.s("समस्त (All)", "All (All)")
                    }
                    FilterChip(
                        selected = selectedFilter == filter,
                        onClick = { selectedFilter = filter },
                        shape = RoundedCornerShape(4.dp),
                        label = { Text(filterLabel, fontSize = 10.5.sp, fontWeight = FontWeight.Bold) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = if (filter == "CRITICAL") StatusRed else MaterialTheme.colorScheme.primary,
                            selectedLabelColor = if (filter == "CRITICAL") Color.White else MaterialTheme.colorScheme.onPrimary,
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
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
                        Text(text = uiState.error ?: "Error loading risk data", color = StatusRed, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = { viewModel.loadRisks() },
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
                            border = CardDefaults.outlinedCardBorder().copy(
                                brush = SolidColor(if (isCritical) StatusRed else MaterialTheme.colorScheme.outline)
                            ),
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
                                                    text = "EDL-${(risk.medicineName.hashCode() % 899 + 100).let { if (it < 0) -it else it }}",
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = risk.medicineName,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = risk.facilityName,
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    RiskBadge(riskLevel = risk.severity)
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
                                            Text(LanguageManager.s("स्टॉक समाप्ति शेष", "Days to Stockout"), fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            Text("${risk.daysToStockout} ${LanguageManager.s("दिन", "days")}", fontSize = 13.5.sp, fontWeight = FontWeight.Bold, color = if (isCritical) StatusRed else MaterialTheme.colorScheme.onSurface)
                                        }
                                        Column {
                                            Text(LanguageManager.s("जोखिम सूचकांक", "Risk Index"), fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            Text("${risk.riskScore}", fontSize = 13.5.sp, fontWeight = FontWeight.Bold, color = if (isCritical) StatusRed else MaterialTheme.colorScheme.primary)
                                        }
                                        Column(horizontalAlignment = Alignment.End) {
                                            Text(LanguageManager.s("स्टॉक घाटा (Deficit)", "Stock Deficit"), fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            Text("${risk.deficit} ${LanguageManager.s("यूनिट", "Units")}", fontSize = 13.5.sp, fontWeight = FontWeight.Bold, color = if (isCritical) StatusRed else StatusOrange)
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "${LanguageManager.s("वर्तमान स्टॉक", "Stock")}: ${risk.currentStock} | ${LanguageManager.s("आवश्यक", "Req")}: ${risk.requiredStock}",
                                        fontSize = 10.5.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "${LanguageManager.s("स्टॉकआउट प्रायिकता", "Stockout Risk")}: ${(animatedProb * 100).toInt()}%",
                                        fontSize = 10.5.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (isCritical) StatusRed else MaterialTheme.colorScheme.primary
                                    )
                                }

                                Spacer(modifier = Modifier.height(6.dp))
                                LinearProgressIndicator(
                                    progress = { animatedProb },
                                    color = if (isCritical) StatusRed else if (isHigh) StatusOrange else TirangaGreen,
                                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(4.dp)
                                        .clip(RoundedCornerShape(2.dp))
                                )

                                if (!risk.recommendation.isNullOrEmpty()) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f),
                                        border = androidx.compose.foundation.BorderStroke(0.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(8.dp),
                                            verticalAlignment = Alignment.Top
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.AutoAwesome,
                                                contentDescription = "Advisory",
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Column {
                                                Text(
                                                    text = LanguageManager.s("आरएमएससीएल क्लिनिकल निर्देश / CMHO परामर्श:", "RMSCL Clinical Advisory / CMHO Guidance:"),
                                                    fontSize = 9.5.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.primary
                                                )
                                                Text(
                                                    text = risk.recommendation ?: "",
                                                    fontSize = 10.5.sp,
                                                    color = MaterialTheme.colorScheme.onSurface
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
