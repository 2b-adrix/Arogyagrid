package com.example.arogyagrid.presentation.dashboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Coronavirus
import androidx.compose.material.icons.filled.Healing
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PendingActions
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.arogyagrid.domain.model.AlertItem
import com.example.arogyagrid.presentation.components.CommandTopBar
import com.example.arogyagrid.presentation.components.GovtCampaignCarousel
import com.example.arogyagrid.presentation.components.GovtEmergencyHelplineBanner
import com.example.arogyagrid.presentation.components.GovtGazetteCirculars
import com.example.arogyagrid.presentation.components.GovtPortalFooter
import com.example.arogyagrid.presentation.components.GovtServicesGrid
import com.example.arogyagrid.presentation.components.MetricCard
import com.example.arogyagrid.presentation.components.RiskBadge
import com.example.arogyagrid.presentation.components.SyntheticDataBanner
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
import com.example.arogyagrid.presentation.theme.TirangaSaffron
import com.example.arogyagrid.presentation.theme.TirangaSaffronLight
import com.example.arogyagrid.presentation.viewmodel.DashboardViewModel

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = viewModel(),
    onNavigateToRedistribution: () -> Unit = {},
    onNavigateToScreen: (String) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var selectedAlert by remember { mutableStateOf<AlertItem?>(null) }
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
            roleLabel = com.example.arogyagrid.core.UserSession.getFormattedRole(),
            districtName = com.example.arogyagrid.core.UserSession.userDistrict ?: "Jaipur",
            searchQuery = searchQuery,
            onSearchQueryChange = { searchQuery = it },
            onSyncClick = { viewModel.loadDashboard() },
            onCopilotClick = { onNavigateToScreen("ai_assistant") },
            onProfileClick = { onNavigateToScreen("profile") },
            onLogoutClick = { onNavigateToScreen("login") },
            onLoginClick = { onNavigateToScreen("login") }
        )

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = PrimaryTeal)
                }
            } else if (uiState.error != null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Text(
                            text = uiState.error ?: "Error loading dashboard",
                            color = StatusRed,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(onClick = { viewModel.loadDashboard() }) {
                                Text("Retry")
                            }
                            OutlinedButton(onClick = { onNavigateToScreen("inventory") }) {
                                Text("Inventory")
                            }
                            OutlinedButton(onClick = { onNavigateToScreen("profile") }) {
                                Text("Profile")
                            }
                        }
                    }
                }
            } else if (uiState.data != null) {
                val sum = uiState.data!!
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // ── 1. Official Government Scheme Awareness Carousel (SWAYAM / UMANG Style) ──
                    item {
                        GovtCampaignCarousel(onNavigateToScreen = onNavigateToScreen)
                    }

                    // ── 2. Official 8-Service Portals Grid (e-Aushadhi, eVIN, IDSP, HMIS, CDSS, etc.) ──
                    item {
                        GovtServicesGrid(onNavigateToScreen = onNavigateToScreen)
                    }

                    // ── 3. Epidemic Alert Banner (Formal Disease Surveillance Bulletin) ──
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, StatusRed),
                            shape = RoundedCornerShape(4.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onNavigateToScreen("outbreak_surveillance") }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(IntrinsicSize.Min),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .width(4.dp)
                                        .fillMaxHeight()
                                        .background(StatusRed)
                                )
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    val isDark = MaterialTheme.colorScheme.background.red < 0.15f
                                    val alertRed = if (isDark) Color(0xFFF87171) else StatusRed
                                    val alertBg = if (isDark) Color(0xFF450A0A) else Color(0xFFFEE2E2)

                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .background(alertBg, RoundedCornerShape(6.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.Coronavirus, contentDescription = null, tint = alertRed, modifier = Modifier.size(20.dp))
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("राज्य महामारी निगरानी बुलेटिन • आमेर ब्लॉक (Amer Block)", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = alertRed)
                                        Spacer(modifier = Modifier.height(1.dp))
                                        Text("तीव्र अतिसार प्रकोप (+340% मांग वृद्धि) • ओआरएस स्टॉक 38 घंटे में समाप्त होने का पूर्वानुमान। विवरण देखें →", fontSize = 10.5.sp, color = MaterialTheme.colorScheme.onSurface, lineHeight = 14.sp)
                                    }
                                }
                            }
                        }
                    }

                    // ── 4. 2x2 Metric Grid (Structured Government Informatics) ──
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                MetricCard(
                                    label = "पंजीकृत संस्थान (Facilities)",
                                    value = "${sum.totalFacilities}",
                                    sub = "लाइव मानचित्र देखें →",
                                    icon = Icons.Default.Business,
                                    iconBgColor = GovtNavyLight,
                                    iconTintColor = GovtNavy,
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { onNavigateToScreen("hospital_map") }
                                )
                                MetricCard(
                                    label = "शय्या उपयोग दर (Bed Occupancy)",
                                    value = "${sum.bedOccupancyPercentage}%",
                                    sub = "${sum.occupiedBeds}/${sum.totalBeds} शय्याएं उपयोग में",
                                    icon = Icons.Default.LocalHospital,
                                    iconBgColor = TirangaSaffronLight,
                                    iconTintColor = TirangaSaffron,
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { onNavigateToScreen("beds_staff") }
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                MetricCard(
                                    label = "स्टॉक संकट (Stockout Deficit)",
                                    value = "${sum.criticalRisks}",
                                    sub = "${sum.highRisks} उच्च जोखिम मदें",
                                    icon = Icons.Default.Warning,
                                    iconBgColor = StatusRedBg,
                                    iconTintColor = StatusRed,
                                    modifier = Modifier.weight(1f)
                                )
                                MetricCard(
                                    label = "लंबित अनुमोदन (Pending Orders)",
                                    value = "${sum.pendingRedistributions}",
                                    sub = "CMHO स्तर पर लंबित",
                                    icon = Icons.Default.PendingActions,
                                    iconBgColor = StatusOrangeBg,
                                    iconTintColor = StatusOrange,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }

                    // ── 5. Pending Transfer Prompt (Official RMSCL Drug Redistribution) ──
                    if (sum.pendingRedistributions > 0) {
                        item {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(SlateBorder)),
                                elevation = CardDefaults.cardElevation(1.dp),
                                shape = RoundedCornerShape(4.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "औषधि पुनर्वितरण प्रस्ताव • Requisition Orders",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.5.sp,
                                            color = GovtNavy
                                        )
                                        Spacer(modifier = Modifier.weight(1f))
                                        Surface(
                                            shape = RoundedCornerShape(3.dp),
                                            color = StatusOrangeBg
                                        ) {
                                            Text(
                                                text = "${sum.pendingRedistributions} लंबित",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = StatusOrange,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "जिले के प्राथमिक स्वास्थ्य केंद्रों में आपूर्ति संतुलन हेतु अंतर-संस्थान औषधि स्थानांतरण प्रस्ताव तैयार हैं। मुख्य चिकित्सा अधिकारी (CMHO) के डिजिटल अनुमोदन उपरांत ग्रीन कॉरिडोर गेट पास जारी होगा।",
                                        fontSize = 11.5.sp,
                                        color = TextMuted
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Button(
                                        onClick = onNavigateToRedistribution,
                                        shape = RoundedCornerShape(4.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = GovtNavy),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("पुनर्वितरण प्रस्ताव समीक्षा व डिजिटल अनुमोदन (Review & Approve)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }

                    // ── 6. Recent Alerts (Official Gazette/Notices style) ──
                    if (sum.recentAlerts.isNotEmpty()) {
                        item {
                            Text(
                                text = "सक्रिय जिला चेतावनियाँ • District Health Advisories",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = GovtNavy
                            )
                        }
                        items(sum.recentAlerts, key = { it.id }) { alert ->
                            Card(
                                onClick = { selectedAlert = alert },
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(SlateBorder)),
                                elevation = CardDefaults.cardElevation(0.dp),
                                shape = RoundedCornerShape(4.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(IntrinsicSize.Min),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .width(4.dp)
                                            .fillMaxHeight()
                                            .background(if (alert.severity == "CRITICAL") StatusRed else StatusOrange)
                                    )
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .clip(CircleShape)
                                                .background(if (alert.severity == "CRITICAL") StatusRed else StatusOrange)
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(alert.title, fontSize = 12.5.sp, fontWeight = FontWeight.Bold, color = TextDark)
                                            Text(alert.message, fontSize = 11.sp, color = TextMuted)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // ── 7. Top Stockout Risks (RMSCL Essential Drug List Risk Matrix) ──
                    item {
                        Text(
                            text = "प्राथमिकता स्टॉकआउट जोखिम • Critical Drug Deficit Matrix",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = GovtNavy
                        )
                    }

                    items(sum.topRisks, key = { it.id }) { risk ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(SlateBorder)),
                            elevation = CardDefaults.cardElevation(0.dp),
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
                                        Text(
                                            text = risk.medicineName,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = GovtNavy
                                        )
                                        Text(
                                            text = risk.facilityName,
                                            fontSize = 11.sp,
                                            color = TextMuted
                                        )
                                    }
                                    RiskBadge(riskLevel = risk.severity)
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "स्टॉक शेष: ${risk.daysToStockout} दिन • जोखिम सूचकांक: ${risk.riskScore}",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextDark
                                )
                                if (!risk.recommendation.isNullOrEmpty()) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "सुझाव: ${risk.recommendation}",
                                        fontSize = 10.5.sp,
                                        color = TextMuted
                                    )
                                }
                            }
                        }
                    }

                    // ── 8. Official Rajasthan Health Gazette & Circulars Notice Board ──
                    item {
                        GovtGazetteCirculars()
                    }

                    // ── 9. Official Emergency Helplines 104 / 108 / 1070 ──
                    item {
                        GovtEmergencyHelplineBanner()
                    }

                    // ── 10. Official National Informatics Centre (NIC) Seal & Footer ──
                    item {
                        GovtPortalFooter()
                    }
                }
            }
        }
    }

    selectedAlert?.let { alert ->
        AlertDialog(
            onDismissRequest = { selectedAlert = null },
            title = { Text(alert.title, fontWeight = FontWeight.Bold, color = GovtNavy) },
            text = {
                Column {
                    Text(alert.message, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Severity: ${alert.severity} • Status: ${alert.status}", fontSize = 11.sp, color = TextMuted)
                }
            },
            confirmButton = {
                Button(
                    onClick = { selectedAlert = null },
                    colors = ButtonDefaults.buttonColors(containerColor = GovtNavy),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text("स्वीकार करें (Acknowledge)")
                }
            }
        )
    }

    if (showBroadcastDialog) {
        AlertDialog(
            onDismissRequest = { showBroadcastDialog = false },
            title = { Text("Emergency District Broadcast", fontWeight = FontWeight.Bold, color = GovtNavy) },
            text = {
                Column {
                    Text("Broadcast an emergency stock advisory to all facilities.", fontSize = 12.sp, color = TextMuted)
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
                        successToast = "Emergency broadcast dispatched successfully."
                        showBroadcastDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GovtNavy),
                    shape = RoundedCornerShape(4.dp)
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
            title = { Text("District Operations Report", fontWeight = FontWeight.Bold, color = GovtNavy) },
            text = {
                Column {
                    Text("Official State Health Operations Summary", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = GovtNavy)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("• Total Facilities: ${uiState.data?.totalFacilities ?: 0}\n• Bed Occupancy: ${uiState.data?.bedOccupancyPercentage ?: 0}%\n• Critical Risks: ${uiState.data?.criticalRisks ?: 0}", fontSize = 12.sp)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        successToast = "Official report generated."
                        showReportDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GovtNavy),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text("Close")
                }
            }
        )
    }

    successToast?.let { msg ->
        AlertDialog(
            onDismissRequest = { successToast = null },
            title = { Text("Notification", fontWeight = FontWeight.Bold, color = GovtNavy) },
            text = { Text(msg) },
            confirmButton = {
                Button(
                    onClick = { successToast = null },
                    colors = ButtonDefaults.buttonColors(containerColor = GovtNavy),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text("OK")
                }
            }
        )
    }
}
