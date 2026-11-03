package com.example.arogyagrid.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.arogyagrid.R
import com.example.arogyagrid.core.AppThemeMode
import com.example.arogyagrid.core.ThemeManager
import com.example.arogyagrid.presentation.theme.*

@Composable
fun CommandTopBar(
    roleLabel: String = com.example.arogyagrid.core.UserSession.getFormattedRole(),
    districtName: String = com.example.arogyagrid.core.UserSession.userDistrict ?: "Jaipur",
    searchQuery: String = "",
    onSearchQueryChange: (String) -> Unit = {},
    showSearch: Boolean = true,
    onSyncClick: () -> Unit = {},
    onCopilotClick: (() -> Unit)? = null,
    onProfileClick: (() -> Unit)? = null
) {
    val themeMode by ThemeManager.themeMode.collectAsState()
    var expandedThemeMenu by remember { mutableStateOf(false) }
    val isOnline by com.example.arogyagrid.util.NetworkMonitor.isOnline.collectAsState()
    val context = androidx.compose.ui.platform.LocalContext.current
    val pendingCount by com.example.arogyagrid.sync.SyncManager.getPendingCountFlow(context).collectAsState(initial = 0)

    Surface(
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            // ── 1. Official National Tricolor Ribbon (Tiranga) ──
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(TirangaSaffron)
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(Color.White)
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(TirangaGreen)
                )
            }

            // ── 2. Official Government Masthead ──
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    // Official State Emblem of India (Satyameva Jayate)
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color.White)
                            .border(1.dp, SlateBorder, RoundedCornerShape(4.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_emblem_lion),
                            contentDescription = "National Emblem of India • सत्यमेव जयते",
                            modifier = Modifier.size(34.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Official App Insignia / Brand Crest
                    Image(
                        painter = painterResource(id = R.drawable.arogyagride),
                        contentDescription = "ArogyaGrid Official Crest",
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .border(0.5.dp, SlateBorder, RoundedCornerShape(4.dp))
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Column {
                        // Saffron Sub-heading: Official State Authority
                        Text(
                            text = "राजस्थान सरकार • GOVT. OF RAJASTHAN",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = TirangaSaffron,
                            letterSpacing = 0.5.sp
                        )

                        // Main Portal Title (Hindi + English)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "आरोग्य ग्रिड",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = GovtNavy
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "ArogyaGrid",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextDark
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            // Official NHM Tag
                            Surface(
                                shape = RoundedCornerShape(3.dp),
                                color = TirangaGreenLight,
                                border = null
                            ) {
                                Text(
                                    text = "NHM RJ",
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TirangaGreen,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                        }

                        // Medical & Health Department Subtitle
                        Text(
                            text = "चिकित्सा एवं स्वास्थ्य विभाग • RMSCL ड्रग व वैक्सीन निगरानी",
                            fontSize = 9.5.sp,
                            color = TextMuted,
                            maxLines = 1
                        )
                    }
                }

                // Action Controls on Right
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // CDSS / Clinical Decision Support System Button
                    onCopilotClick?.let { onClick ->
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = GovtNavyLight,
                            border = null,
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .clickable { onClick() }
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 5.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MedicalServices,
                                    contentDescription = "CDSS Assistant",
                                    tint = GovtNavy,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "निर्णय CDSS",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GovtNavy
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Theme selector
                    Box {
                        IconButton(
                            onClick = { expandedThemeMenu = true },
                            modifier = Modifier.size(34.dp)
                        ) {
                            val currentIcon = when (themeMode) {
                                AppThemeMode.SYSTEM -> Icons.Default.SettingsBrightness
                                AppThemeMode.LIGHT -> Icons.Default.LightMode
                                AppThemeMode.DARK -> Icons.Default.DarkMode
                            }
                            Icon(
                                imageVector = currentIcon,
                                contentDescription = "Theme",
                                tint = TextMuted,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        DropdownMenu(
                            expanded = expandedThemeMenu,
                            onDismissRequest = { expandedThemeMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("सिस्टम थीम (System)") },
                                leadingIcon = { Icon(Icons.Default.SettingsBrightness, contentDescription = null) },
                                onClick = {
                                    ThemeManager.setThemeMode(AppThemeMode.SYSTEM)
                                    expandedThemeMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("लाइट मोड (Light)") },
                                leadingIcon = { Icon(Icons.Default.LightMode, contentDescription = null) },
                                onClick = {
                                    ThemeManager.setThemeMode(AppThemeMode.LIGHT)
                                    expandedThemeMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("डार्क मोड (Dark)") },
                                leadingIcon = { Icon(Icons.Default.DarkMode, contentDescription = null) },
                                onClick = {
                                    ThemeManager.setThemeMode(AppThemeMode.DARK)
                                    expandedThemeMenu = false
                                }
                            )
                        }
                    }

                    // Refresh Button
                    IconButton(
                        onClick = onSyncClick,
                        modifier = Modifier.size(34.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh",
                            tint = TextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Alerts Bell with Badge
                    BadgedBox(
                        badge = {
                            Badge(containerColor = StatusRed) {
                                Text("3", color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Alerts",
                            tint = TextMuted,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Officer Official Badge
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        border = null,
                        modifier = Modifier.then(
                            if (onProfileClick != null) Modifier.clickable { onProfileClick.invoke() }
                            else Modifier
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .clip(CircleShape)
                                    .background(GovtNavy),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = roleLabel.take(1).uppercase(),
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.width(5.dp))
                            Column {
                                Text(
                                    text = if (roleLabel.contains("DISTRICT", ignoreCase = true)) "CMHO $districtName" else roleLabel,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GovtNavy
                                )
                                Text(
                                    text = "RJ-JPR-SEC",
                                    fontSize = 8.sp,
                                    color = TextMuted
                                )
                            }
                        }
                    }
                }
            }

            // ── 3. Administrative Status Strip ──
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Live Sync / NIC Network Status
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(if (isOnline) StatusGreen else StatusOrange)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = if (isOnline) {
                                if (pendingCount > 0) "NIC नेटवर्क • $pendingCount सिंक में..." else "NIC डेटाबेस • ऑनलाइन सिंक सक्रिय"
                            } else {
                                if (pendingCount > 0) "ऑफलाइन • $pendingCount कतार में" else "ऑफ़लाइन मोड • स्थानीय कैश"
                            },
                            fontSize = 10.sp,
                            color = if (isOnline) TextMuted else StatusOrange,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    // Official Stamp / Date
                    Text(
                        text = "IST 11:25 AM • NIC Cloud RJ",
                        fontSize = 9.sp,
                        color = TextMuted
                    )
                }
            }

            // ── 4. Government Inventory Search Bar ──
            if (showSearch) {
                Box(modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = onSearchQueryChange,
                        placeholder = {
                            Text(
                                "आवश्यक औषधि, संस्थान या बैच नंबर खोजें (EDL Search)...",
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = null,
                                tint = TextMuted,
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            focusedBorderColor = GovtNavy,
                            unfocusedBorderColor = SlateBorder
                        ),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                    )
                }
            }
        }
    }
}
