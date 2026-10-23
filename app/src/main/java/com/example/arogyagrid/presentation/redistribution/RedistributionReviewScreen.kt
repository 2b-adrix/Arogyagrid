package com.example.arogyagrid.presentation.redistribution

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.arogyagrid.presentation.components.CommandTopBar
import com.example.arogyagrid.presentation.components.SyntheticDataBanner
import com.example.arogyagrid.presentation.theme.PrimaryTeal
import com.example.arogyagrid.presentation.theme.SlateBorder
import com.example.arogyagrid.presentation.theme.StatusGreen
import com.example.arogyagrid.presentation.theme.StatusGreenBg
import com.example.arogyagrid.presentation.theme.StatusRed
import com.example.arogyagrid.presentation.theme.StatusRedBg
import com.example.arogyagrid.presentation.theme.TextDark
import com.example.arogyagrid.presentation.theme.TextMuted

@Composable
fun RedistributionReviewScreen(
    viewModel: RedistributionViewModel
) {
    val recs by viewModel.recommendations.collectAsState()
    val message by viewModel.userMessage.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Redistribution Approval",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )
                    Text(
                        text = "Human-reviewed resource movement based on risk, surplus & proximity",
                        fontSize = 12.sp,
                        color = TextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (recs.isEmpty()) {
                Text("No active redistribution recommendations.", fontSize = 14.sp, color = TextMuted)
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(recs, key = { it.id }) { item ->
                        val isApproved = item.status == "APPROVED"

                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(SlateBorder)),
                            elevation = CardDefaults.cardElevation(3.dp),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .animateContentSize()
                                .animateItem()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                // Source -> Destination Route Header
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(10.dp))
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("SOURCE (SURPLUS)", fontSize = 9.sp, color = TextMuted, fontWeight = FontWeight.Bold)
                                        Text(item.sourceFacilityName, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextDark)
                                        Text("Surplus: 400 units", fontSize = 10.sp, color = TextMuted)
                                    }

                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                        contentDescription = "Transfer",
                                        tint = PrimaryTeal,
                                        modifier = Modifier.padding(horizontal = 8.dp)
                                    )

                                    Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                                        Text("DESTINATION (SHORTAGE)", fontSize = 9.sp, color = TextMuted, fontWeight = FontWeight.Bold)
                                        Text(item.destinationFacilityName, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextDark)
                                        Text("Shortage: 330 units", fontSize = 10.sp, color = TextMuted)
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(item.medicineName, fontSize = 17.sp, fontWeight = FontWeight.Bold, color = PrimaryTeal)
                                        Text("${item.recommendedQuantity} units recommended", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextDark)
                                    }

                                    Column(horizontalAlignment = Alignment.End) {
                                        Text("ESTIMATED DISTANCE", fontSize = 9.sp, color = TextMuted, fontWeight = FontWeight.Bold)
                                        Text("~${item.distanceKm} km", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    }
                                }

                                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = SlateBorder)

                                Text("RATIONALE & REASON", fontSize = 10.sp, color = TextMuted, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(item.reason, fontSize = 12.sp, color = Color.DarkGray)

                                Spacer(modifier = Modifier.height(14.dp))

                                AnimatedVisibility(
                                    visible = item.status == "PENDING_APPROVAL",
                                    enter = fadeIn(),
                                    exit = fadeOut()
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        Button(
                                            onClick = { viewModel.approve(item.id) },
                                            shape = RoundedCornerShape(10.dp),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text("Approve Transfer", fontSize = 13.sp)
                                        }
                                        OutlinedButton(
                                            onClick = { viewModel.reject(item.id) },
                                            shape = RoundedCornerShape(10.dp),
                                            colors = ButtonDefaults.outlinedButtonColors(contentColor = StatusRed),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text("Reject", fontSize = 13.sp)
                                        }
                                    }
                                }

                                AnimatedVisibility(
                                    visible = item.status != "PENDING_APPROVAL",
                                    enter = fadeIn(),
                                    exit = fadeOut()
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(if (isApproved) StatusGreenBg else StatusRedBg, shape = RoundedCornerShape(8.dp))
                                            .padding(10.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            tint = if (isApproved) StatusGreen else StatusRed,
                                            modifier = Modifier.height(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = if (isApproved) "APPROVED • Human officer decision recorded & inventory transfer queued"
                                                   else "REJECTED • Human decision logged in audit history",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isApproved) StatusGreen else StatusRed
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

    message?.let { msg ->
        AlertDialog(
            onDismissRequest = { viewModel.clearMessage() },
            title = { Text("Decision Logged") },
            text = { Text(msg) },
            confirmButton = {
                Button(onClick = { viewModel.clearMessage() }) {
                    Text("OK")
                }
            }
        )
    }
}
