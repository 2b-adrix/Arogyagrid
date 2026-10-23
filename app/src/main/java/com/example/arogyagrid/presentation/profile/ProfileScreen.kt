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
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SettingsBrightness
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import com.example.arogyagrid.domain.models.Facility
import com.example.arogyagrid.domain.repository.FacilityRepository
import com.example.arogyagrid.presentation.components.CommandTopBar
import com.example.arogyagrid.presentation.theme.AccentBlue
import com.example.arogyagrid.presentation.theme.PrimaryTeal
import com.example.arogyagrid.presentation.theme.PrimaryTealLight
import com.example.arogyagrid.presentation.theme.SlateBorder
import com.example.arogyagrid.presentation.theme.StatusRed
import com.example.arogyagrid.presentation.theme.TextDark
import com.example.arogyagrid.presentation.theme.TextMuted

@Composable
fun ProfileScreen(
    facilityRepository: FacilityRepository?,
    onNavigateToRegisterFacility: () -> Unit,
    onLogout: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val facilitiesState = facilityRepository?.getFacilities()?.collectAsState(initial = emptyList())
    val facilities = facilitiesState?.value ?: listOf(
        Facility("FAC-001", "Jaipur District Hospital 1", "Jaipur", "District Hospital", 26.9124, 75.7873, 250, 210),
        Facility("FAC-002", "Jaipur PHC 1", "Jaipur", "Primary Health Centre", 26.9200, 75.8000, 30, 28),
        Facility("FAC-003", "Jaipur PHC 2", "Jaipur", "Primary Health Centre", 26.9500, 75.8200, 25, 12),
        Facility("FAC-004", "Jaipur DH 2", "Jaipur", "District Hospital", 26.8500, 75.7500, 300, 280)
    )

    val filteredFacilities = facilities.filter {
        searchQuery.isBlank() ||
        it.name.contains(searchQuery, ignoreCase = true) ||
        it.district.contains(searchQuery, ignoreCase = true) ||
        it.facilityType.contains(searchQuery, ignoreCase = true)
    }

    val themeMode by ThemeManager.themeMode.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        CommandTopBar(
            roleLabel = "District Officer",
            districtName = "Jaipur",
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
            // --- 1. USER OFFICER PROFILE CARD ---
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(SlateBorder)),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(CircleShape)
                                    .background(PrimaryTeal),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "D",
                                    color = Color.White,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "Jaipur District Officer",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextDark
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Icon(
                                        imageVector = Icons.Default.VerifiedUser,
                                        contentDescription = "Verified",
                                        tint = PrimaryTeal,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }

                                Text(
                                    text = "officer_jaipur@arogyagrid.gov.in",
                                    fontSize = 12.sp,
                                    color = TextMuted
                                )

                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = PrimaryTealLight,
                                    modifier = Modifier.padding(top = 6.dp)
                                ) {
                                    Text(
                                        text = "DISTRICT OFFICER Scope: Jaipur (24 Centers)",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = PrimaryTeal,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 14.dp), color = SlateBorder)

                        // Quick Account Action Buttons
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Button(
                                onClick = onNavigateToRegisterFacility,
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.AddBusiness, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Register Hospital / PHC", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // --- 2. THEME & DISPLAY PREFERENCES ---
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

            // --- 3. HOSPITAL / PHC DISCOVERY & PROFILE SEARCH ---
            item {
                Column {
                    Text(
                        text = "Hospital & PHC Profile Directory",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )
                    Text(
                        text = "Search and view supply availability across all 1,000+ connected health centers in the grid.",
                        fontSize = 12.sp,
                        color = TextMuted
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Search hospital name, PHC, district...", fontSize = 12.sp, color = TextMuted) },
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

            // Hospital Profile Items List
            items(filteredFacilities) { facility ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(SlateBorder)),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(1.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(PrimaryTealLight),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocalHospital,
                                contentDescription = null,
                                tint = PrimaryTeal,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = facility.name,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextDark
                            )
                            Text(
                                text = "${facility.facilityType} • ${facility.district}",
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                            Text(
                                text = "Beds: ${facility.occupiedBeds} / ${facility.totalBeds} occupied",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = PrimaryTeal
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Text(
                                text = facility.id,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextDark,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
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
            onLogout = {}
        )
    }
}
