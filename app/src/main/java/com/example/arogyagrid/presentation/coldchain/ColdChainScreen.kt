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
        // ── 1. Official National Tricolor Ribbon (Tiranga) ──
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
        ) {
            Box(modifier = Modifier.weight(1f).fillMaxHeight().background(TirangaSaffron))
            Box(modifier = Modifier.weight(1f).fillMaxHeight().background(Color.White))
            Box(modifier = Modifier.weight(1f).fillMaxHeight().background(TirangaGreen))
        }

        // ── 2. Official eVIN Top Masthead ──
        Surface(
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        IconButton(
                            onClick = onNavigateBack,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.onSurface)
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        // National Emblem
                        Image(
                            painter = painterResource(id = R.drawable.ic_emblem_lion),
                            contentDescription = "Emblem",
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "राष्ट्रीय स्वास्थ्य मिशन • eVIN राजस्थान",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = TirangaSaffron,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = "इलेक्ट्रॉनिक वैक्सीन इंटेलिजेंस नेटवर्क",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(3.dp),
                        color = if (breachedUnitsCount > 0) StatusRedBg else StatusGreenBg,
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (breachedUnitsCount > 0) StatusRed else TirangaGreen)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .background(if (breachedUnitsCount > 0) StatusRed else TirangaGreen, CircleShape)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = if (breachedUnitsCount > 0) "$breachedUnitsCount विचलन (Alert)" else "समस्त सुरक्षित (Optimal)",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (breachedUnitsCount > 0) StatusRed else TirangaGreen
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Telemetry Quick Stats & Filter Chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    FilterChip(
                        selected = selectedFilter == "ALL",
                        onClick = { selectedFilter = "ALL" },
                        shape = RoundedCornerShape(4.dp),
                        label = { Text("समस्त उपकरण (${units.size})", fontSize = 10.5.sp, fontWeight = FontWeight.Bold) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                    FilterChip(
                        selected = selectedFilter == "BREACHED",
                        onClick = { selectedFilter = "BREACHED" },
                        shape = RoundedCornerShape(4.dp),
                        label = { Text("तापमान विचलन ($breachedUnitsCount)", fontSize = 10.5.sp, fontWeight = FontWeight.Bold) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = StatusRed,
                            selectedLabelColor = Color.White,
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        leadingIcon = {
                            if (breachedUnitsCount > 0) {
                                Icon(Icons.Default.Warning, contentDescription = null, tint = if (selectedFilter == "BREACHED") Color.White else StatusRed, modifier = Modifier.size(13.dp))
                            }
                        }
                    )
                    FilterChip(
                        selected = selectedFilter == "AMER",
                        onClick = { selectedFilter = "AMER" },
                        shape = RoundedCornerShape(4.dp),
                        label = { Text("पीएचसी आमेर (Amer)", fontSize = 10.5.sp, fontWeight = FontWeight.Bold) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }
        }

        val filteredUnits = when (selectedFilter) {
            "BREACHED" -> units.filter { it.isBreached }
            "AMER" -> units.filter { it.facilityId == "FAC-JPR-011" }
            else -> units
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // ── 3. Official eVIN Vaccine Cold Chain Picture Hero Banner ──
            item {
                Card(
                    shape = RoundedCornerShape(4.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder),
                    elevation = CardDefaults.cardElevation(2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                androidx.compose.ui.graphics.Brush.horizontalGradient(
                                    listOf(Color(0xFF064E3B), Color(0xFF046A38), Color(0xFF0D9488))
                                )
                            )
                            .padding(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Surface(
                                    shape = RoundedCornerShape(3.dp),
                                    color = Color.White.copy(alpha = 0.2f),
                                    border = androidx.compose.foundation.BorderStroke(0.5.dp, Color.White.copy(alpha = 0.4f))
                                ) {
                                    Text(
                                        text = "NHM • UNDP • राजस्थान सरकार",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "जिला वैक्सीन एवं बायोलॉजिकल थर्मल सर्विलांस",
                                    fontSize = 14.5.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "आईएलआर व डीप फ्रीजर 24x7 आईओटी टेलीमेट्री • सोलर व ग्रिड बैकअप • तापमान विचलन पर त्वरित वाहन प्रेषण",
                                    fontSize = 10.5.sp,
                                    color = Color.White.copy(alpha = 0.88f),
                                    lineHeight = 14.sp
                                )
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            // Cold Chain Hero Illustration Picture
                            Box(
                                modifier = Modifier
                                    .size(width = 96.dp, height = 76.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color.White.copy(alpha = 0.12f))
                                    .border(0.5.dp, Color.White.copy(alpha = 0.25f), RoundedCornerShape(4.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.banner_evin_coldchain),
                                    contentDescription = "eVIN Telemetry Picture",
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                    }
                }
            }

            // ── 4. Active Thermal Emergency Alert Banner if any unit is breached ──
            if (breachedUnitsCount > 0) {
                val primaryBreached = units.first { it.isBreached }
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(StatusRed)),
                        shape = RoundedCornerShape(4.dp),
                        elevation = CardDefaults.cardElevation(1.dp),
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
                                    .background(StatusRed)
                            )
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .background(StatusRedBg, RoundedCornerShape(4.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.DeviceThermostat, contentDescription = null, tint = StatusRed, modifier = Modifier.size(20.dp))
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "शीत-श्रृंखला तापमान विचलन चेतावनी (CRITICAL EXCURSION)",
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = StatusRed
                                    )
                                    Text(
                                        text = "${primaryBreached.name} (${primaryBreached.facilityName}) का तापमान ${primaryBreached.currentTempCelsius}°C है (सुरक्षित सीमा: ${primaryBreached.maxSafeTemp}°C)।",
                                        fontSize = 11.sp,
                                        color = TextDark
                                    )
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                Button(
                                    onClick = {
                                        actionDialogMessage = "आपातकालीन वाहन अलर्ट जारी: जिला शीत-श्रृंखला अधिकारी व पीएचसी आमेर प्रभारी को आइस-पैक आरक्षित वाहन रवाना करने के निर्देश दिए गए।"
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = StatusRed),
                                    shape = RoundedCornerShape(4.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                                ) {
                                    Text("वाहन प्रेषण", fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            // ── 5. Equipment Simulation Controls Bar ──
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "पंजीकृत शीत-श्रृंखला उपकरण (${filteredUnits.size} Units)",
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        OutlinedButton(
                            onClick = {
                                units = units.map { u ->
                                    if (u.id == "CCU-AMR-ILR-01") u.copy(currentTempCelsius = 9.2) else u
                                }
                            },
                            shape = RoundedCornerShape(3.dp),
                            border = androidx.compose.foundation.BorderStroke(0.8.dp, StatusRed),
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 3.dp)
                        ) {
                            Text("विचलन सिम्युलेट", fontSize = 10.sp, color = StatusRed, fontWeight = FontWeight.Bold)
                        }
                        OutlinedButton(
                            onClick = {
                                units = units.map { u ->
                                    if (u.id == "CCU-AMR-ILR-01") u.copy(currentTempCelsius = 4.5) else u
                                }
                            },
                            shape = RoundedCornerShape(3.dp),
                            border = androidx.compose.foundation.BorderStroke(0.8.dp, TirangaGreen),
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 3.dp)
                        ) {
                            Text("सामान्य रीसेट", fontSize = 10.sp, color = TirangaGreen, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // ── 6. List of Telemetry Equipment Cards ──
            items(filteredUnits) { unit ->
                val cardBorderColor by animateColorAsState(
                    targetValue = if (unit.isBreached) StatusRed else SlateBorder,
                    label = "border"
                )

                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(cardBorderColor)),
                    shape = RoundedCornerShape(4.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(
                                        if (unit.isBreached) StatusRedBg else MaterialTheme.colorScheme.primaryContainer,
                                        RoundedCornerShape(4.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (unit.isBreached) Icons.Default.Warning else Icons.Default.AcUnit,
                                    contentDescription = null,
                                    tint = if (unit.isBreached) StatusRed else MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        shape = RoundedCornerShape(2.dp),
                                        color = MaterialTheme.colorScheme.primaryContainer
                                    ) {
                                        Text(
                                            text = unit.id,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(text = unit.name, fontSize = 13.5.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                                Text(
                                    text = "${unit.facilityName} • मॉडल: ${unit.model}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "${unit.currentTempCelsius}°C",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (unit.isBreached) StatusRed else TirangaGreen
                                )
                                Surface(
                                    shape = RoundedCornerShape(2.dp),
                                    color = if (unit.isBreached) StatusRedBg else StatusGreenBg
                                ) {
                                    Text(
                                        text = if (unit.isBreached) "तापमान विचलन" else "सुरक्षित सीमा",
                                        fontSize = 8.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (unit.isBreached) StatusRed else TirangaGreen,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Target range & telemetry telemetry bar
                        Surface(
                            shape = RoundedCornerShape(3.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                            border = androidx.compose.foundation.BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "मानक थर्मल सीमा: ${unit.minSafeTemp}°C से ${unit.maxSafeTemp}°C",
                                    fontSize = 10.5.sp,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.Medium
                                )
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Sensors, contentDescription = null, modifier = Modifier.size(12.dp), tint = MaterialTheme.colorScheme.primary)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "पिंग: ${unit.lastPingSecondsAgo}s पूर्व • बैटरी: ${unit.batteryPercent}%",
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "संरक्षित बायोलॉजिकल/वैक्सीन: ${unit.storedSupplies.joinToString(", ")}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Normal
                        )

                        Spacer(modifier = Modifier.height(3.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "ऊर्जा स्रोत: ${unit.powerSource}",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "eVIN IoT प्रमाणित",
                                fontSize = 10.sp,
                                color = TirangaGreen,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
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
