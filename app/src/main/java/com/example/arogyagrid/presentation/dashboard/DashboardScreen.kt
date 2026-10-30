package com.example.arogyagrid.presentation.dashboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.arogyagrid.domain.models.AlertItem
import com.example.arogyagrid.domain.models.Facility
import com.example.arogyagrid.presentation.auth.LoginScreen
import com.example.arogyagrid.presentation.components.CommandTopBar
import com.example.arogyagrid.presentation.components.MetricCard
import com.example.arogyagrid.presentation.components.RiskBadge
import com.example.arogyagrid.presentation.components.RiskMapCard
import com.example.arogyagrid.presentation.components.SyntheticDataBanner
import com.example.arogyagrid.presentation.theme.AccentBlue
import com.example.arogyagrid.presentation.theme.AccentBlueLight
import com.example.arogyagrid.presentation.theme.PrimaryTeal
import com.example.arogyagrid.presentation.theme.PrimaryTealLight
import com.example.arogyagrid.presentation.theme.SlateBorder
import com.example.arogyagrid.presentation.theme.SlateCardVariant
import com.example.arogyagrid.presentation.theme.StatusGreen
import com.example.arogyagrid.presentation.theme.StatusOrange
import com.example.arogyagrid.presentation.theme.StatusOrangeBg
import com.example.arogyagrid.presentation.theme.StatusRed
import com.example.arogyagrid.presentation.theme.StatusRedBg
import com.example.arogyagrid.presentation.theme.TextDark
import com.example.arogyagrid.presentation.theme.TextMuted

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    onNavigateToRedistribution: () -> Unit = {},
    onNavigateToScreen: (String) -> Unit = {}
) {
    val summary by viewModel.summaryState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var selectedAlert by remember { mutableStateOf<AlertItem?>(null) }
    var selectedFacility by remember { mutableStateOf<Facility?>(null) }
    var showBroadcastDialog by remember { mutableStateOf(false) }
    var broadcastMsg by remember { mutableStateOf("Urgent: Restock critical Amoxicillin reserves across Jaipur PHC network.") }
    var showReportDialog by remember { mutableStateOf(false) }
    var successToast by remember { mutableStateOf<String?>(null) }

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
            onSyncClick = { successToast = "Synced 24 facilities successfully with State Health Server." },
            onCopilotClick = { onNavigateToScreen("ai_assistant") },
            onProfileClick = { onNavigateToScreen("profile") }
        )

        if (summary == null) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator(color = PrimaryTeal)
            }
        } else {
            val sum = summary!!
            AnimatedVisibility(
                visible = true,
                enter = fadeIn(animationSpec = tween(600)) + slideInVertically(initialOffsetY = { 40 })
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Header Title
                    item {
                        Column {
                            Text(
                                text = "HEALTHCARE COMMAND CENTER",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = PrimaryTeal,
                                letterSpacing = 1.2.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Jaipur District Operations",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = TextDark
                            )
                            Text(
                                text = "24 facilities monitored • Live resource intelligence & demand forecasting",
                                fontSize = 12.sp,
                                color = TextMuted
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                AssistChip(
                                    onClick = { onNavigateToScreen("ai_assistant") },
                                    label = { Text("AI Copilot", fontSize = 10.sp) },
                                    leadingIcon = { Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = PrimaryTeal) }
                                )
                                AssistChip(
                                    onClick = { showBroadcastDialog = true },
                                    label = { Text("Broadcast", fontSize = 10.sp) },
                                    leadingIcon = { Icon(Icons.Default.Campaign, contentDescription = null, tint = StatusRed) }
                                )
                                AssistChip(
                                    onClick = { showReportDialog = true },
                                    label = { Text("Report", fontSize = 10.sp) },
                                    leadingIcon = { Icon(Icons.Default.Description, contentDescription = null, tint = AccentBlue) }
                                )
                                AssistChip(
                                    onClick = { onNavigateToScreen("audit_logs") },
                                    label = { Text("Audit", fontSize = 10.sp) },
                                    leadingIcon = { Icon(Icons.AutoMirrored.Filled.ListAlt, contentDescription = null, tint = TextMuted) }
                                )
                            }
                        }
                    }

                    // 2x2 Metric Grid
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                MetricCard(
                                    label = "Total Facilities",
                                    value = "${sum.totalFacilities}",
                                    sub = "8 DH + 16 PHC",
                                    icon = Icons.Default.Business,
                                    iconBgColor = PrimaryTealLight,
                                    iconTintColor = PrimaryTeal,
                                    modifier = Modifier.weight(1f)
                                )
                                MetricCard(
                                    label = "PHC Network",
                                    value = "16",
                                    sub = "Fully active",
                                    icon = Icons.Default.Favorite,
                                    iconBgColor = AccentBlueLight,
                                    iconTintColor = AccentBlue,
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                MetricCard(
                                    label = "Stockout Risks",
                                    value = "${sum.criticalRisksCount}",
                                    sub = "Action needed",
                                    icon = Icons.Default.Warning,
                                    iconBgColor = StatusRedBg,
                                    iconTintColor = StatusRed,
                                    modifier = Modifier.weight(1f)
                                )
                                MetricCard(
                                    label = "Active Alerts",
                                    value = "${sum.stockAlertsCount}",
                                    sub = "3 unresolved",
                                    icon = Icons.Default.Notifications,
                                    iconBgColor = StatusOrangeBg,
                                    iconTintColor = StatusOrange,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }

                    // Interactive Risk Map Card
                    item {
                        RiskMapCard(facilities = sum.facilities)
                    }

                    // Active District Alerts Section
                    if (sum.recentAlerts.isNotEmpty()) {
                        item {
                            Text(
                                text = "Active District Alerts (Tap to Review)",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextDark
                            )
                        }
                        items(sum.recentAlerts, key = { it.id }) { alert ->
                            Card(
                                onClick = { selectedAlert = alert },
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(SlateBorder)),
                                elevation = CardDefaults.cardElevation(1.dp),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .animateItem()
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(10.dp)
                                            .clip(CircleShape)
                                            .background(if (alert.severity == "CRITICAL") StatusRed else StatusOrange)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(alert.title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextDark)
                                        Text(alert.message, fontSize = 11.sp, color = TextMuted)
                                    }
                                }
                            }
                        }
                    }

                    // Pending Transfer Review Prompt
                    if (sum.pendingRecommendations.isNotEmpty()) {
                        item {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(SlateBorder)),
                                elevation = CardDefaults.cardElevation(2.dp),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .animateContentSize()
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = "Pending Redistribution Recommendation",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = PrimaryTeal
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Jaipur DH 1 has 400 surplus units of Amoxicillin. Jaipur PHC 1 faces a 330 unit gap. Review and approve the transfer.",
                                        fontSize = 12.sp,
                                        color = TextMuted
                                    )
                                    Spacer(modifier = Modifier.height(14.dp))
                                    Button(
                                        onClick = onNavigateToRedistribution,
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("Review & Approve Transfer", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }

                    // Top Stockout Risks
                    item {
                        Text(
                            text = "Priority Stockout Risks",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextDark
                        )
                    }

                    items(sum.topRisks.take(5), key = { "${it.facilityId}_${it.medicineId}" }) { risk ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(SlateBorder)),
                            elevation = CardDefaults.cardElevation(1.dp),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .animateContentSize()
                                .animateItem()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = risk.medicineName,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
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
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = risk.reason,
                                    fontSize = 11.sp,
                                    color = TextDark
                                )
                            }
                        }
                    }

                    // Bed Capacity & Pressure
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(SlateBorder)),
                            elevation = CardDefaults.cardElevation(2.dp),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Bed Capacity & Pressure (Tap Facility)",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextDark
                                )
                                Spacer(modifier = Modifier.height(12.dp))

                                sum.facilities.take(4).forEach { f ->
                                    val animatedProgress by animateFloatAsState(
                                        targetValue = f.occupancyPercentage / 100f,
                                        animationSpec = spring(
                                            dampingRatio = Spring.DampingRatioMediumBouncy,
                                            stiffness = Spring.StiffnessLow
                                        ),
                                        label = "BedOccupancyProgress"
                                    )

                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { selectedFacility = f }
                                            .padding(vertical = 6.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(text = f.name, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextDark)
                                            Text(text = "${f.occupancyPercentage}% occupied", fontSize = 11.sp, color = TextMuted)
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        LinearProgressIndicator(
                                            progress = { animatedProgress },
                                            color = if (f.occupancyPercentage > 85) StatusRed else StatusGreen,
                                            trackColor = SlateCardVariant,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(6.dp)
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(text = "${f.occupiedBeds}/${f.totalBeds} beds occupied", fontSize = 10.sp, color = TextMuted)
                                        }
                                        HorizontalDivider(modifier = Modifier.padding(top = 8.dp), color = SlateBorder)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    selectedAlert?.let { alert ->
        AlertDialog(
            onDismissRequest = { selectedAlert = null },
            title = { Text(alert.title) },
            text = {
                Column {
                    Text("Facility: ${alert.facilityName}", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(alert.message, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Time: ${alert.createdAtFormatted}", fontSize = 11.sp, color = TextMuted)
                }
            },
            confirmButton = {
                Button(onClick = {
                    successToast = "Alert acknowledged & archived."
                    selectedAlert = null
                }) {
                    Text("Acknowledge & Archive")
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedAlert = null }) {
                    Text("Close")
                }
            }
        )
    }

    selectedFacility?.let { fac ->
        AlertDialog(
            onDismissRequest = { selectedFacility = null },
            title = { Text(fac.name) },
            text = {
                Column {
                    Text("Facility Type: ${fac.facilityType}", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Text("District: ${fac.district}", fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Bed Occupancy: ${fac.occupiedBeds} / ${fac.totalBeds} (${fac.occupancyPercentage}%)", fontSize = 12.sp)
                    Text("Location Coordinates: Lat ${fac.latitude}, Lng ${fac.longitude}", fontSize = 11.sp, color = TextMuted)
                }
            },
            confirmButton = {
                Button(onClick = { selectedFacility = null }) {
                    Text("Close Details")
                }
            }
        )
    }

    if (showBroadcastDialog) {
        AlertDialog(
            onDismissRequest = { showBroadcastDialog = false },
            title = { Text("Emergency District Broadcast") },
            text = {
                Column {
                    Text("Broadcast an emergency stock advisory to all 24 facilities in Jaipur district.", fontSize = 12.sp, color = TextMuted)
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = broadcastMsg,
                        onValueChange = { broadcastMsg = it },
                        label = { Text("Advisory Message") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        successToast = "Emergency broadcast dispatched to all 24 facilities successfully."
                        showBroadcastDialog = false
                    }
                ) {
                    Text("Broadcast Now")
                }
            },
            dismissButton = {
                TextButton(onClick = { showBroadcastDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showReportDialog) {
        AlertDialog(
            onDismissRequest = { showReportDialog = false },
            title = { Text("District Operations Report") },
            text = {
                Column {
                    Text("Official State Health Operations Summary", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = PrimaryTeal)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("• District: Jaipur (24 Facilities Monitored)\n• Bed Occupancy: 84% Average\n• Critical Stockout Risks: 1 (Amoxicillin)\n• Pending Redistributions: 1 Transfer", fontSize = 12.sp)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        successToast = "Official PDF operations report downloaded & saved."
                        showReportDialog = false
                    }
                ) {
                    Text("Download PDF Report")
                }
            },
            dismissButton = {
                TextButton(onClick = { showReportDialog = false }) {
                    Text("Close")
                }
            }
        )
    }

    successToast?.let { msg ->
        AlertDialog(
            onDismissRequest = { successToast = null },
            title = { Text("System Notification") },
            text = { Text(msg) },
            confirmButton = {
                Button(onClick = { successToast = null }) {
                    Text("OK")
                }
            }
        )
    }
}
@Preview (showSystemUi = true)
@Composable
fun DashboardScreen() {
    MaterialTheme {
        DashboardScreen()
    }
}