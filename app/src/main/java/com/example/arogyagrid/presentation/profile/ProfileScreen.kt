package com.example.arogyagrid.presentation.profile

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
import androidx.compose.material.icons.filled.AddBusiness
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SettingsBrightness
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.arogyagrid.core.AppThemeMode
import com.example.arogyagrid.core.ThemeManager
import com.example.arogyagrid.core.UserSession
import com.example.arogyagrid.domain.repository.FacilityRepository
import com.example.arogyagrid.presentation.components.CommandTopBar
import com.example.arogyagrid.presentation.theme.PrimaryTeal
import com.example.arogyagrid.presentation.theme.PrimaryTealLight
import com.example.arogyagrid.presentation.theme.SlateBorder
import com.example.arogyagrid.presentation.theme.StatusRed
import com.example.arogyagrid.presentation.theme.StatusRedBg
import com.example.arogyagrid.presentation.theme.TextDark
import com.example.arogyagrid.presentation.theme.TextMuted

data class HospitalProfile(
    val id: String,
    val name: String,
    val subtitle: String,
    val address: String,
    val totalBeds: Int,
    val occupiedBeds: Int,
    val activeStaff: Int,
    val oxygenCylinders: Int,
    val stockStatus: String,
    val stockColor: Color
)

@Composable
fun ProfileScreen(
    facilityRepository: FacilityRepository?,
    onNavigateToRegisterFacility: () -> Unit,
    onNavigateToMap: () -> Unit = {},
    onLogout: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }

    // Comprehensive list of all 15 real registered hospitals across Jaipur for LinkedIn-style profile browsing & search
    val registeredHospitals = remember {
        listOf(
            HospitalProfile(
                "FAC-JPR-001",
                "Sawai ManSingh (SMS) Hospital, Jaipur",
                "Major Tertiary Super-Specialty Hospital • District Hub",
                "JLN Marg, Jaipur, Rajasthan 302004",
                1500, 1280, 390, 220,
                "Surplus Stock Hub", PrimaryTeal
            ),
            HospitalProfile(
                "FAC-JPR-002",
                "JK Lon Pediatric Hospital, Jaipur",
                "Premier Pediatric & Child Care Hospital",
                "JLN Marg, Near SMS Medical College, Jaipur",
                800, 690, 195, 120,
                "Sufficient Inventory", PrimaryTeal
            ),
            HospitalProfile(
                "FAC-JPR-003",
                "Zanana Maternity Hospital, Jaipur",
                "Women & Maternal Health Specialty Center",
                "Station Road, Chandpole, Jaipur",
                600, 510, 160, 90,
                "Moderate Stock", Color(0xFFD97706)
            ),
            HospitalProfile(
                "FAC-JPR-004",
                "Mahila Chikitsalaya, Jaipur",
                "Women & Infant Health Specialty Hospital",
                "Sanganeri Gate, Jaipur 302003",
                500, 430, 145, 75,
                "Normal Stock", PrimaryTeal
            ),
            HospitalProfile(
                "FAC-JPR-005",
                "RUHS College of Medical Sciences & Hospital, Jaipur",
                "Super-Specialty & State Medical Education Hub",
                "Sector 11, Pratap Nagar, Jaipur 302033",
                750, 610, 185, 160,
                "Surplus Hub", PrimaryTeal
            ),
            HospitalProfile(
                "FAC-JPR-006",
                "CHC Sanganer, Jaipur",
                "Community Health Centre • Suburban Hub",
                "Tonk Road, Sanganer, Jaipur 302029",
                100, 82, 38, 10,
                "Oxygen Low (Reorder)", StatusRed
            ),
            HospitalProfile(
                "FAC-JPR-007",
                "CHC Chomu, Jaipur",
                "Community Health Centre • Northern Corridor Hub",
                "NH-11, Chomu, Jaipur 303702",
                100, 78, 35, 18,
                "Normal Stock", PrimaryTeal
            ),
            HospitalProfile(
                "FAC-JPR-008",
                "CHC Amber, Jaipur",
                "Community Health Centre • Heritage Zone",
                "Amber Fort Road, Jaipur 302028",
                80, 62, 30, 15,
                "Normal Stock", PrimaryTeal
            ),
            HospitalProfile(
                "FAC-JPR-009",
                "CHC Kotputli, Jaipur",
                "Community Health Centre • Highway Trauma Network",
                "NH-48, Kotputli, Jaipur 303108",
                120, 95, 42, 25,
                "Sufficient Inventory", PrimaryTeal
            ),
            HospitalProfile(
                "FAC-JPR-010",
                "CHC Shahpura, Jaipur",
                "Community Health Centre • Rural Sub-District",
                "Delhi Road, Shahpura, Jaipur 303103",
                90, 70, 32, 12,
                "Moderate Stock", Color(0xFFD97706)
            ),
            HospitalProfile(
                "FAC-JPR-011",
                "PHC Amer, Jaipur",
                "Rural Primary Health Center • Outpatient Care",
                "Fort Road, Amer, Jaipur 302028",
                30, 24, 12, 4,
                "Critical ORS Shortage", StatusRed
            ),
            HospitalProfile(
                "FAC-JPR-012",
                "PHC Chaksu, Jaipur",
                "Primary Health Centre • Rural Care Unit",
                "Chaksu Bus Stand, Jaipur 303901",
                25, 18, 10, 5,
                "Normal Stock", PrimaryTeal
            ),
            HospitalProfile(
                "FAC-JPR-013",
                "PHC Phagi, Jaipur",
                "Primary Health Centre • Outlying Community Care",
                "Phagi Town, Jaipur 303005",
                20, 15, 8, 3,
                "Low Stock Warning", Color(0xFFD97706)
            ),
            HospitalProfile(
                "FAC-JPR-014",
                "PHC Bassi, Jaipur",
                "Primary Health Centre • Eastern Sub-District",
                "Bassi Main Market, Jaipur 303301",
                25, 20, 10, 6,
                "Normal Stock", PrimaryTeal
            ),
            HospitalProfile(
                "FAC-JPR-015",
                "PHC Jamwa Ramgarh, Jaipur",
                "Primary Health Centre • Dam Valley Community Hub",
                "Ramgarh Dam Road, Jaipur 303109",
                20, 16, 8, 4,
                "Normal Stock", PrimaryTeal
            )
        )
    }

    val filteredHospitals = registeredHospitals.filter {
        searchQuery.isBlank() ||
        it.name.contains(searchQuery, ignoreCase = true) ||
        it.subtitle.contains(searchQuery, ignoreCase = true) ||
        it.address.contains(searchQuery, ignoreCase = true) ||
        it.id.contains(searchQuery, ignoreCase = true)
    }

    val themeMode by ThemeManager.themeMode.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        CommandTopBar(
            roleLabel = UserSession.getFormattedRole(),
            districtName = UserSession.userDistrict ?: "Jaipur",
            searchQuery = "",
            onSearchQueryChange = {},
            onSyncClick = {}
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // --- 1. CURRENT LOGGED-IN HOSPITAL PROFILE ---
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(SlateBorder)),
                    shape = RoundedCornerShape(18.dp),
                    elevation = CardDefaults.cardElevation(3.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(CircleShape)
                                    .background(PrimaryTealLight),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocalHospital,
                                    contentDescription = null,
                                    tint = PrimaryTeal,
                                    modifier = Modifier.size(32.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = UserSession.userName ?: "Sawai ManSingh Hospital",
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextDark
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        imageVector = Icons.Default.Verified,
                                        contentDescription = "Verified Profile",
                                        tint = PrimaryTeal,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Text(
                                    text = "Role: ${UserSession.getFormattedRole()} • ${UserSession.userFacilityId ?: "FAC-JPR-001"}",
                                    fontSize = 12.sp,
                                    color = TextMuted
                                )
                                Text(
                                    text = "${UserSession.userEmail ?: "sms.manager@arogyagrid.com"} • ${UserSession.userDistrict ?: "Jaipur"}",
                                    fontSize = 11.sp,
                                    color = TextMuted
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Bed Occupancy Progress Bar Section
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Bed Occupancy Rate", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextDark)
                                Text("1,280 / 1,500 Beds (85.3%)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PrimaryTeal)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            LinearProgressIndicator(
                                progress = { 0.853f },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                color = PrimaryTeal,
                                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Quick Stats Row
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Active Staff", fontSize = 10.sp, color = TextMuted)
                                    Text("390 On-Duty", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextDark)
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Oxygen Cylinders", fontSize = 10.sp, color = TextMuted)
                                    Text("220 Units", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = PrimaryTeal)
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = PrimaryTealLight,
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Status", fontSize = 10.sp, color = PrimaryTeal)
                                    Text("Surplus Hub", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PrimaryTeal)
                                }
                            }
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 14.dp), color = SlateBorder)

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Button(
                                onClick = onNavigateToMap,
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.Map, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("View Health Grid Map", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }

                            OutlinedButton(
                                onClick = onNavigateToRegisterFacility,
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.AddBusiness, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Register Facility", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // --- 2. THEME PREFERENCES ---
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(SlateBorder)),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Display & App Theme",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextDark
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (themeMode == AppThemeMode.SYSTEM) PrimaryTealLight else MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { ThemeManager.setThemeMode(AppThemeMode.SYSTEM) }
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(Icons.Default.SettingsBrightness, contentDescription = null, tint = PrimaryTeal, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("System", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (themeMode == AppThemeMode.LIGHT) PrimaryTealLight else MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { ThemeManager.setThemeMode(AppThemeMode.LIGHT) }
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(Icons.Default.LightMode, contentDescription = null, tint = PrimaryTeal, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Light", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (themeMode == AppThemeMode.DARK) PrimaryTealLight else MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { ThemeManager.setThemeMode(AppThemeMode.DARK) }
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(Icons.Default.DarkMode, contentDescription = null, tint = PrimaryTeal, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Dark", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            // --- 3. LINKEDIN-STYLE HOSPITAL SEARCH & DIRECTORY ---
            item {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Explore & Search Registered Hospitals",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextDark
                        )
                        Text(
                            text = "${filteredHospitals.size} of 15 facilities",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = PrimaryTeal
                        )
                    }
                    Text(
                        text = "Search across all 15 Jaipur facilities by name, sub-district, or ID.",
                        fontSize = 12.sp,
                        color = TextMuted
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Search SMS, Amer, Sanganer, Chomu, Kotputli...", fontSize = 12.sp, color = TextMuted) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextMuted) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            focusedBorderColor = PrimaryTeal,
                            unfocusedBorderColor = SlateBorder
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // Hospital Profile Cards List
            items(filteredHospitals) { hospital ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(SlateBorder)),
                    shape = RoundedCornerShape(14.dp),
                    elevation = CardDefaults.cardElevation(2.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToMap() }
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(PrimaryTealLight),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocalHospital,
                                    contentDescription = null,
                                    tint = PrimaryTeal,
                                    modifier = Modifier.size(22.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = hospital.name,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextDark
                                )
                                Text(
                                    text = hospital.subtitle,
                                    fontSize = 11.sp,
                                    color = TextMuted
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (hospital.stockStatus.contains("Shortage") || hospital.stockStatus.contains("Low") || hospital.stockStatus.contains("Warning")) StatusRedBg else PrimaryTealLight
                            ) {
                                Text(
                                    text = hospital.stockStatus,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = hospital.stockColor,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Beds: ${hospital.occupiedBeds} / ${hospital.totalBeds} occupied", fontSize = 11.sp, color = TextMuted)
                            Text("Active Staff: ${hospital.activeStaff} • Tap to view on Map →", fontSize = 11.sp, color = PrimaryTeal)
                        }
                    }
                }
            }

            // --- 4. SIGN OUT BUTTON ---
            item {
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = onLogout,
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = StatusRed),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                ) {
                    Icon(Icons.Default.Logout, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Sign Out of Command Center", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun ProfileScreenPreview() {
    MaterialTheme {
        ProfileScreen(
            facilityRepository = null,
            onNavigateToRegisterFacility = {},
            onNavigateToMap = {},
            onLogout = {}
        )
    }
}
