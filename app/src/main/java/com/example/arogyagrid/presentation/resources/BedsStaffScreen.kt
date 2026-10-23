package com.example.arogyagrid.presentation.resources

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.example.arogyagrid.presentation.components.CommandTopBar
import com.example.arogyagrid.presentation.dashboard.DashboardViewModel
import com.example.arogyagrid.presentation.theme.PrimaryTeal
import com.example.arogyagrid.presentation.theme.SlateBorder
import com.example.arogyagrid.presentation.theme.SlateCardVariant
import com.example.arogyagrid.presentation.theme.StatusGreen
import com.example.arogyagrid.presentation.theme.StatusRed
import com.example.arogyagrid.presentation.theme.TextDark
import com.example.arogyagrid.presentation.theme.TextMuted

@Composable
fun BedsStaffScreen(
    viewModel: DashboardViewModel
) {
    val summary by viewModel.summaryState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        CommandTopBar(
            roleLabel = "District Officer",
            districtName = "Jaipur",
            searchQuery = searchQuery,
            onSearchQueryChange = { searchQuery = it },
            onSyncClick = {}
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

            val facilities = summary?.facilities?.filter {
                searchQuery.isBlank() || it.name.contains(searchQuery, ignoreCase = true)
            } ?: emptyList()

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(facilities) { fac ->
                    val totalBeds = fac.totalBeds
                    val occupied = fac.occupiedBeds
                    val pct = fac.occupancyPercentage

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
                                Text(
                                    text = fac.name,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextDark
                                )
                                Text(
                                    text = "$pct% occupied",
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
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("Beds: $occupied / $totalBeds", fontSize = 12.sp, color = TextMuted)
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("Staff: 8 / 10 active", fontSize = 12.sp, color = TextMuted)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
