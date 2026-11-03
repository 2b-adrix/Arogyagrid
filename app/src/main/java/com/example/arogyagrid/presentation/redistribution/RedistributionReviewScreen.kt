package com.example.arogyagrid.presentation.redistribution

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.SwapHoriz
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
import com.example.arogyagrid.domain.model.RedistributionItem
import com.example.arogyagrid.presentation.components.CommandTopBar
import com.example.arogyagrid.presentation.components.RiskBadge
import com.example.arogyagrid.presentation.components.SyntheticDataBanner
import com.example.arogyagrid.presentation.dispatch.DispatchManifest
import com.example.arogyagrid.presentation.dispatch.EmergencyDispatchDialog
import com.example.arogyagrid.presentation.theme.*
import com.example.arogyagrid.presentation.viewmodel.RedistributionViewModel

@Composable
fun RedistributionReviewScreen(
    viewModel: RedistributionViewModel = viewModel(),
    onNavigateToProfile: () -> Unit = {},
    onLogout: () -> Unit = {},
    onNavigateToLogin: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var activeDispatchManifest by remember { mutableStateOf<DispatchManifest?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        SyntheticDataBanner()

        CommandTopBar(
            roleLabel = UserSession.userRole ?: "District Officer",
            districtName = UserSession.userDistrict ?: "Jaipur",
            searchQuery = searchQuery,
            onSearchQueryChange = { searchQuery = it },
            onSyncClick = { viewModel.loadRedistributions() },
            onProfileClick = onNavigateToProfile,
            onLogoutClick = onLogout,
            onLoginClick = onNavigateToLogin
        )

        Column(modifier = Modifier.padding(14.dp)) {
            // Official Section Header
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.SwapHoriz,
                        contentDescription = "Approval",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = LanguageManager.s("औषधि पुनर्वितरण अनुमोदन • REDISTRIBUTION ORDERS", "Medicine Redistribution Approvals • REDISTRIBUTION ORDERS"),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = LanguageManager.s("मुख्य चिकित्सा अधिकारी (CMHO) स्तर पर डिजिटल सत्यापन एवं ग्रीन कॉरिडोर पास", "CMHO Digital Verification & Green Corridor Transit Gate Pass"),
                        fontSize = 10.5.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            } else if (uiState.error != null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = uiState.error ?: "Error loading redistributions", color = StatusRed, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = { viewModel.loadRedistributions() },
                            shape = RoundedCornerShape(4.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            )
                        ) {
                            Text(LanguageManager.s("पुनः प्रयास करें (Retry)", "Retry"))
                        }
                    }
                }
            } else if (uiState.items.isEmpty()) {
                Text(
                    LanguageManager.s("कोई सक्रिय पुनर्वितरण प्रस्ताव लंबित नहीं है।", "No active redistribution recommendations pending."),
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                val filteredList = uiState.items.filter {
                    searchQuery.isBlank() ||
                            it.medicineName.contains(searchQuery, ignoreCase = true) ||
                            it.sourceFacilityName.contains(searchQuery, ignoreCase = true) ||
                            it.targetFacilityName.contains(searchQuery, ignoreCase = true)
                }

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredList, key = { it.recommendationId }) { item ->
                        val isApproved = item.status.equals("APPROVED", ignoreCase = true)
                        val isPending = item.status.equals("PENDING_APPROVAL", ignoreCase = true) || item.status.equals("PENDING", ignoreCase = true)

                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(MaterialTheme.colorScheme.outline)),
                            elevation = CardDefaults.cardElevation(2.dp),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .animateContentSize()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                // Source -> Destination Route Header
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(6.dp))
                                        .border(0.5.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(6.dp))
                                        .padding(10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(LanguageManager.s("स्रोतः अधिशेष केंद्र", "SOURCE (SURPLUS)"), fontSize = 8.5.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold)
                                        Text(item.sourceFacilityName, fontSize = 12.5.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                                        Text("${LanguageManager.s("अधिशेष", "Surplus")}: ${item.sourceSurplus} ${LanguageManager.s("यूनिट", "units")}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }

                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                        contentDescription = "Transfer",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.padding(horizontal = 8.dp)
                                    )

                                    Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                                        Text(LanguageManager.s("गंतव्यः घाटा केंद्र", "DESTINATION (DEFICIT)"), fontSize = 8.5.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold)
                                        Text(item.targetFacilityName, fontSize = 12.5.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                                        Text("${LanguageManager.s("मांग घाटा", "Deficit")}: ${item.targetDeficit} ${LanguageManager.s("यूनिट", "units")}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(item.medicineName, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                        Text("${item.transferQuantity} ${LanguageManager.s("यूनिट स्थानांतरण अनुशंसित", "units recommended")}", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                                    }

                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(LanguageManager.s("प्राथमिकता स्कोर", "PRIORITY SCORE"), fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold)
                                        Text("${item.priorityScore}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        RiskBadge(riskLevel = if (isPending) "PENDING" else if (isApproved) "APPROVED" else "REJECTED")
                                    }
                                }

                                if (!item.justification.isNullOrEmpty()) {
                                    HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = MaterialTheme.colorScheme.outline)
                                    Text(LanguageManager.s("औचित्य विवरण (JUSTIFICATION)", "JUSTIFICATION"), fontSize = 9.5.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(item.justification ?: "", fontSize = 11.5.sp, color = MaterialTheme.colorScheme.onSurface)
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                AnimatedVisibility(
                                    visible = isPending,
                                    enter = fadeIn(),
                                    exit = fadeOut()
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Button(
                                            onClick = { viewModel.approve(item.recommendationId, true) },
                                            shape = RoundedCornerShape(6.dp),
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = MaterialTheme.colorScheme.primary,
                                                contentColor = MaterialTheme.colorScheme.onPrimary
                                            ),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text(LanguageManager.s("स्थानांतरण स्वीकृत करें", "Approve Transfer"), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        }
                                        OutlinedButton(
                                            onClick = { viewModel.approve(item.recommendationId, false) },
                                            shape = RoundedCornerShape(6.dp),
                                            border = androidx.compose.foundation.BorderStroke(1.dp, StatusRed),
                                            colors = ButtonDefaults.outlinedButtonColors(contentColor = StatusRed),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text(LanguageManager.s("अस्वीकार करें", "Reject"), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }

                                AnimatedVisibility(
                                    visible = !isPending,
                                    enter = fadeIn(),
                                    exit = fadeOut()
                                ) {
                                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .background(if (isApproved) StatusGreenBg else StatusRedBg, shape = RoundedCornerShape(6.dp))
                                                .padding(10.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.CheckCircle,
                                                contentDescription = null,
                                                tint = if (isApproved) StatusGreen else StatusRed,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = if (isApproved)
                                                    LanguageManager.s("स्वीकृत • निर्णय दर्ज एवं ग्रीन कॉरिडोर रूट सक्रिय", "APPROVED • Decision recorded & corridor route active")
                                                else
                                                    LanguageManager.s("अस्वीकृत • ऑडिट लॉग में दर्ज", "REJECTED • Decision logged in audit history"),
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isApproved) StatusGreen else StatusRed
                                            )
                                        }

                                        if (isApproved) {
                                            OutlinedButton(
                                                onClick = {
                                                    activeDispatchManifest = DispatchManifest(
                                                        passNumber = "RJ-MED-TR-${item.recommendationId.takeLast(6).uppercase()}",
                                                        sourceHospital = item.sourceFacilityName,
                                                        destinationHospital = item.targetFacilityName,
                                                        items = listOf("${item.transferQuantity}x ${item.medicineName}")
                                                    )
                                                },
                                                shape = RoundedCornerShape(6.dp),
                                                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                                                colors = ButtonDefaults.outlinedButtonColors(
                                                    contentColor = MaterialTheme.colorScheme.primary
                                                ),
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Icon(Icons.Default.LocalShipping, contentDescription = null, modifier = Modifier.size(15.dp))
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(LanguageManager.s("आपातकालीन गेट पास जनरेट करें (Pass)", "Generate Emergency Gate Pass"), fontSize = 11.sp, fontWeight = FontWeight.Bold)
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

        // Emergency Gate Pass Dialog
        activeDispatchManifest?.let { manifest ->
            EmergencyDispatchDialog(
                manifest = manifest,
                onDismiss = { activeDispatchManifest = null },
                onConfirmDispatch = {
                    activeDispatchManifest = null
                }
            )
        }
    }
}
