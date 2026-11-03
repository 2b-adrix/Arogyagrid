package com.example.arogyagrid.presentation.coldchain

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.ui.res.painterResource
import com.example.arogyagrid.R
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeviceThermostat
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.NotificationImportant
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SecurityUpdateWarning
import androidx.compose.material.icons.filled.Sensors
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
import com.example.arogyagrid.presentation.theme.*

data class ColdChainUnit(
    val id: String,
    val name: String,
    val facilityId: String,
    val facilityName: String,
    val model: String,
    var currentTempCelsius: Double,
    val minSafeTemp: Double,
    val maxSafeTemp: Double,
    val humidityPercent: Int,
    val batteryPercent: Int,
    val storedSupplies: List<String>,
    val lastPingSecondsAgo: Int,
    val powerSource: String
) {
    val isBreached: Boolean
        get() = currentTempCelsius < minSafeTemp || currentTempCelsius > maxSafeTemp

    val statusText: String
        get() = when {
            currentTempCelsius > maxSafeTemp -> "CRITICAL EXCURSION (HIGH)"
            currentTempCelsius < minSafeTemp -> "FREEZING RISK (LOW)"
            currentTempCelsius in (maxSafeTemp - 0.8)..maxSafeTemp -> "WARNING ELEVATED"
            else -> "OPTIMAL RANGE"
        }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColdChainScreen(
    onNavigateBack: () -> Unit
) {
    var units by remember {
        mutableStateOf(
            listOf(
                ColdChainUnit(
                    id = "CCU-AMR-ILR-01",
                    name = "ILR Unit #1 (Vaccine & Insulin Bay)",
                    facilityId = "FAC-JPR-011",
                    facilityName = "PHC Amer, Jaipur",
                    model = "Godrej Medical ILR 300L (Solar Hybrid)",
                    currentTempCelsius = 8.4, // Live breach (>8°C)
                    minSafeTemp = 2.0,
                    maxSafeTemp = 8.0,
                    humidityPercent = 54,
                    batteryPercent = 68,
                    storedSupplies = listOf("Covaxin (450 doses)", "Rotavirus (320 doses)", "Human Insulin Regular (180 vials)"),
                    lastPingSecondsAgo = 8,
                    powerSource = "Solar Backup Active (Grid Offline)"
                ),
                ColdChainUnit(
                    id = "CCU-SMS-DF-01",
                    name = "Central Deep Freezer #1",
                    facilityId = "FAC-JPR-001",
                    facilityName = "SMS Hospital, Jaipur",
                    model = "Vestfrost Medical Sub-Zero -25°C",
                    currentTempCelsius = -19.4,
                    minSafeTemp = -25.0,
                    maxSafeTemp = -15.0,
                    humidityPercent = 40,
                    batteryPercent = 98,
                    storedSupplies = listOf("OPV Polio (12,000 doses)", "Varicella (1,500 doses)"),
                    lastPingSecondsAgo = 3,
                    powerSource = "Main Grid (Redundant GenSet)"
                ),
                ColdChainUnit(
                    id = "CCU-SMS-ILR-03",
                    name = "Central Vaccine Bank ILR #3",
                    facilityId = "FAC-JPR-001",
                    facilityName = "SMS Hospital, Jaipur",
                    model = "Blue Star Medical ILR 400",
                    currentTempCelsius = 4.2,
                    minSafeTemp = 2.0,
                    maxSafeTemp = 8.0,
                    humidityPercent = 48,
                    batteryPercent = 100,
                    storedSupplies = listOf("BCG Vaccine (2,400 vials)", "Hepatitis B (3,100 vials)", "Pentavalent (1,800 doses)"),
                    lastPingSecondsAgo = 12,
                    powerSource = "Main Grid"
                ),
                ColdChainUnit(
                    id = "CCU-CHM-ILR-02",
                    name = "Community Cold Storage #2",
                    facilityId = "FAC-JPR-004",
                    facilityName = "CHC Chomu, Jaipur",
                    model = "Haier Biomedical HXC-258",
                    currentTempCelsius = 3.9,
                    minSafeTemp = 2.0,
                    maxSafeTemp = 8.0,
                    humidityPercent = 52,
                    batteryPercent = 89,
                    storedSupplies = listOf("Rabies Vaccines (140 doses)", "Tetanus Toxoid (600 vials)", "Insulin Glargine (110 vials)"),
                    lastPingSecondsAgo = 19,
                    powerSource = "Hybrid Solar"
                ),
                ColdChainUnit(
                    id = "CCU-SNG-ILR-01",
                    name = "Sub-District Health ILR #1",
                    facilityId = "FAC-JPR-006",
                    facilityName = "CHC Sanganer, Jaipur",
                    model = "Godrej Medical ILR 200L",
                    currentTempCelsius = 5.1,
                    minSafeTemp = 2.0,
                    maxSafeTemp = 8.0,
                    humidityPercent = 50,
                    batteryPercent = 84,
                    storedSupplies = listOf("Measles-Rubella (380 doses)", "JE Vaccine (210 doses)"),
                    lastPingSecondsAgo = 25,
                    powerSource = "Main Grid"
                )
            )
        )
    }

    var selectedFilter by remember { mutableStateOf("ALL") }
    var actionDialogMessage by remember { mutableStateOf<String?>(null) }

    val breachedUnitsCount = units.count { it.isBreached }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top App Bar
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
                            text = "IoT Cold-Chain Telemetry",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextDark
                        )
                        Text(
                            text = "District Vaccine & Biological Thermal Surveillance",
                            fontSize = 11.sp,
                            color = PrimaryTeal
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (breachedUnitsCount > 0) StatusRed.copy(alpha = 0.15f) else StatusGreen.copy(alpha = 0.15f)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(if (breachedUnitsCount > 0) StatusRed else StatusGreen, CircleShape)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (breachedUnitsCount > 0) "$breachedUnitsCount Thermal Breach" else "All Units Safe",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (breachedUnitsCount > 0) StatusRed else StatusGreen
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Telemetry Quick Stats & Filter Chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = selectedFilter == "ALL",
                        onClick = { selectedFilter = "ALL" },
                        label = { Text("All Units (${units.size})", fontSize = 11.sp) }
                    )
                    FilterChip(
                        selected = selectedFilter == "BREACHED",
                        onClick = { selectedFilter = "BREACHED" },
                        label = { Text("Breached ($breachedUnitsCount)", fontSize = 11.sp) },
                        leadingIcon = {
                            if (breachedUnitsCount > 0) {
                                Icon(Icons.Default.Warning, contentDescription = null, tint = StatusRed, modifier = Modifier.size(14.dp))
                            }
                        }
                    )
                    FilterChip(
                        selected = selectedFilter == "AMER",
                        onClick = { selectedFilter = "AMER" },
                        label = { Text("PHC Amer", fontSize = 11.sp) }
                    )
                }
            }
        }

        // Active Thermal Emergency Alert Banner if any unit is breached
        if (breachedUnitsCount > 0) {
            val primaryBreached = units.first { it.isBreached }
            Card(
                colors = CardDefaults.cardColors(containerColor = StatusRed.copy(alpha = 0.08f)),
                border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(StatusRed)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .background(StatusRed.copy(alpha = 0.15f), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.DeviceThermostat, contentDescription = null, tint = StatusRed, modifier = Modifier.size(24.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "CRITICAL COLD-CHAIN EXCURSION",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = StatusRed
                        )
                        Text(
                            text = "${primaryBreached.name} at ${primaryBreached.facilityName} is currently ${primaryBreached.currentTempCelsius}°C (Safe ceiling: ${primaryBreached.maxSafeTemp}°C).",
                            fontSize = 11.sp,
                            color = TextDark
                        )
                    }
                    Button(
                        onClick = {
                            actionDialogMessage = "Emergency Dispatch Alert sent to District Cold-Chain Officer & PHC Amer Medical In-Charge. Redundant ice-pack transfer vehicle dispatched."
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = StatusRed),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text("Dispatch", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // List of Telemetry Units
        val filteredUnits = when (selectedFilter) {
            "BREACHED" -> units.filter { it.isBreached }
            "AMER" -> units.filter { it.facilityId == "FAC-JPR-011" }
            else -> units
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Live Storage Telemetry (${filteredUnits.size} Units)",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        OutlinedButton(
                            onClick = {
                                // Simulate power outage temp spike at Amer
                                units = units.map { u ->
                                    if (u.id == "CCU-AMR-ILR-01") u.copy(currentTempCelsius = 9.2) else u
                                }
                            },
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text("Simulate Spike", fontSize = 10.sp, color = StatusRed)
                        }
                        OutlinedButton(
                            onClick = {
                                // Reset Amer to safe temp
                                units = units.map { u ->
                                    if (u.id == "CCU-AMR-ILR-01") u.copy(currentTempCelsius = 4.5) else u
                                }
                            },
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text("Reset Safe", fontSize = 10.sp, color = PrimaryTeal)
                        }
                    }
                }
            }

            items(filteredUnits) { unit ->
                val cardBorderColor by animateColorAsState(
                    targetValue = if (unit.isBreached) StatusRed else SlateBorder,
                    label = "border"
                )

                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(cardBorderColor)),
                    shape = RoundedCornerShape(14.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .background(
                                        if (unit.isBreached) StatusRed.copy(alpha = 0.15f) else PrimaryTeal.copy(alpha = 0.15f),
                                        RoundedCornerShape(10.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (unit.isBreached) Icons.Default.Warning else Icons.Default.AcUnit,
                                    contentDescription = null,
                                    tint = if (unit.isBreached) StatusRed else PrimaryTeal,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = unit.name, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextDark)
                                Text(
                                    text = "${unit.facilityName} • Model: ${unit.model}",
                                    fontSize = 11.sp,
                                    color = TextMuted
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "${unit.currentTempCelsius}°C",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (unit.isBreached) StatusRed else PrimaryTeal
                                )
                                Text(
                                    text = unit.statusText,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (unit.isBreached) StatusRed else StatusGreen
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Target range bar
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Target: ${unit.minSafeTemp}°C to ${unit.maxSafeTemp}°C",
                                fontSize = 11.sp,
                                color = TextDark,
                                fontWeight = FontWeight.Medium
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Sensors, contentDescription = null, modifier = Modifier.size(12.dp), tint = PrimaryTeal)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Ping: ${unit.lastPingSecondsAgo}s ago • Bat: ${unit.batteryPercent}%",
                                    fontSize = 10.sp,
                                    color = TextMuted
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Stored Biologicals: ${unit.storedSupplies.joinToString(", ")}",
                            fontSize = 11.sp,
                            color = TextDark,
                            fontWeight = FontWeight.Normal
                        )

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Power: ${unit.powerSource}",
                            fontSize = 10.sp,
                            color = TextMuted
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    if (actionDialogMessage != null) {
        AlertDialog(
            onDismissRequest = { actionDialogMessage = null },
            title = { Text("Emergency Cold-Chain Response", fontWeight = FontWeight.Bold) },
            text = { Text(actionDialogMessage!!) },
            confirmButton = {
                Button(onClick = { actionDialogMessage = null }) {
                    Text("OK")
                }
            }
        )
    }
}
