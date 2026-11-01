package com.example.arogyagrid.presentation.resources

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.arogyagrid.core.UserSession
import com.example.arogyagrid.presentation.components.CommandTopBar
import com.example.arogyagrid.presentation.theme.PrimaryTeal
import com.example.arogyagrid.presentation.theme.SlateBorder
import com.example.arogyagrid.presentation.theme.SlateCardVariant
import com.example.arogyagrid.presentation.theme.StatusGreen
import com.example.arogyagrid.presentation.theme.StatusRed
import com.example.arogyagrid.presentation.theme.TextDark
import com.example.arogyagrid.presentation.theme.TextMuted
import com.example.arogyagrid.presentation.viewmodel.BedsStaffViewModel

@Composable
fun BedsStaffScreen(
    viewModel: BedsStaffViewModel = viewModel()
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
            onSyncClick = { viewModel.loadBedsAndStaff() }
        )

        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Bed & Staff Management",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )
            Text(
                text = "Capacity, occupancy rates & staffing availability across facilities",
                fontSize = 12.sp,
                color = TextMuted
            )

            Spacer(modifier = Modifier.height(14.dp))

            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = PrimaryTeal)
                }
            } else if (uiState.error != null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = uiState.error ?: "Error loading beds & staff data", color = StatusRed, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(onClick = { viewModel.loadBedsAndStaff() }) {
                            Text("Retry")
                        }
                    }
                }
            } else if (uiState.data != null) {
                val sum = uiState.data!!

                // Summary Card
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(SlateBorder)),
                    elevation = CardDefaults.cardElevation(2.dp),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("DISTRICT SUMMARY", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = PrimaryTeal)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("TOTAL BEDS", fontSize = 9.sp, color = TextMuted, fontWeight = FontWeight.Bold)
                                Text("${sum.totalBeds}", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextDark)
                                Text("Occupied: ${sum.occupiedBeds}", fontSize = 11.sp, color = TextMuted)
                                Text("Available: ${sum.availableBeds}", fontSize = 11.sp, color = TextMuted)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("STAFFING", fontSize = 9.sp, color = TextMuted, fontWeight = FontWeight.Bold)
                                Text("${sum.activeStaff}/${sum.totalStaff}", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = PrimaryTeal)
                                Text("Ratio: ${sum.staffingRatio}", fontSize = 11.sp, color = TextMuted)
                                Text("Occupancy: ${sum.occupancyPercentage}%", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (sum.occupancyPercentage > 85) StatusRed else StatusGreen)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                val facilities = sum.facilities.filter {
                    searchQuery.isBlank() || it.name.contains(searchQuery, ignoreCase = true)
                }

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(facilities, key = { it.facilityId }) { fac ->
                        val totalBeds = fac.totalBeds
                        val occupied = fac.occupiedBeds
                        val pct = fac.bedOccupancyPercentage.toFloat()

                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(SlateBorder)),
                            elevation = CardDefaults.cardElevation(2.dp),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = fac.name,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextDark
                                        )
                                        Text(
                                            text = "${fac.type} • ${fac.district}",
                                            fontSize = 11.sp,
                                            color = TextMuted
                                        )
                                    }
                                    Text(
                                        text = "${pct.toInt()}% occupied",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (pct > 85) StatusRed else PrimaryTeal
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                LinearProgressIndicator(
                                    progress = { pct / 100f },
                                    color = if (pct > 85) StatusRed else StatusGreen,
                                    trackColor = SlateCardVariant,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(6.dp)
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Beds: $occupied / $totalBeds (Avail: ${fac.availableBeds})", fontSize = 12.sp, color = TextMuted)
                                    Text("Staff: ${fac.activeStaff} / ${fac.totalStaff} active", fontSize = 12.sp, color = TextMuted)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
