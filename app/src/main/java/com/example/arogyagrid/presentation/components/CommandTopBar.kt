package com.example.arogyagrid.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.automirrored.filled.Logout
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
import com.example.arogyagrid.core.AppLanguage
import com.example.arogyagrid.core.AppThemeMode
import com.example.arogyagrid.core.LanguageManager
import com.example.arogyagrid.core.ThemeManager
import com.example.arogyagrid.core.UserSession
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
    onProfileClick: (() -> Unit)? = null,
    onLogoutClick: (() -> Unit)? = null,
    onLoginClick: (() -> Unit)? = null
) {
    val themeMode by ThemeManager.themeMode.collectAsState()
    val currentLang by LanguageManager.currentLanguage.collectAsState()
    var expandedMenu by remember { mutableStateOf(false) }
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

            // ── 2. Official Government Masthead (Clean & Organized) ──
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
                            .size(40.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(MaterialTheme.colorScheme.surface)
                            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(6.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_emblem_lion),
                            contentDescription = "National Emblem of India • सत्यमेव जयते",
                            modifier = Modifier.size(30.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        // Saffron Sub-heading: Official State Authority
                        Text(
                            text = "राजस्थान सरकार • GOVT. OF RAJASTHAN",
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = TirangaSaffron,
                            letterSpacing = 0.5.sp
                        )

                        // Main Portal Title (Hindi + English)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "आरोग्य ग्रिड",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "ArogyaGrid",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            // Official NHM Tag
                            Surface(
                                shape = RoundedCornerShape(3.dp),
                                color = TirangaGreenLight
                            ) {
                                Text(
                                    text = "NHM RJ",
                                    fontSize = 7.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TirangaGreen,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                        }

                        // Medical & Health Department Subtitle
                        Text(
                            text = "चिकित्सा एवं स्वास्थ्य विभाग • RMSCL ड्रग व वैक्सीन निगरानी",
                            fontSize = 9.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1
                        )
                    }
                }

                // Action Controls on Right (Clean & Streamlined)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Officer Official Badge
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
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
                                    .background(MaterialTheme.colorScheme.primary),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = roleLabel.take(1).uppercase(),
                                    color = MaterialTheme.colorScheme.onPrimary,
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
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "RJ-JPR",
                                    fontSize = 7.5.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    // Options & Settings Menu
                    Box {
                        IconButton(
                            onClick = { expandedMenu = true },
                            modifier = Modifier.size(34.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "Menu",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        DropdownMenu(
                            expanded = expandedMenu,
                            onDismissRequest = { expandedMenu = false }
                        ) {
                            // ── Language Selection ──
                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            "हिन्दी (Hindi)",
                                            color = if (currentLang == AppLanguage.HINDI) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                                            fontWeight = if (currentLang == AppLanguage.HINDI) FontWeight.Bold else FontWeight.Normal
                                        )
                                        if (currentLang == AppLanguage.HINDI) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Icon(Icons.Default.Check, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                                        }
                                    }
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.Translate, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                },
                                onClick = {
                                    LanguageManager.setLanguage(AppLanguage.HINDI)
                                    expandedMenu = false
                                }
                            )

                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            "English",
                                            color = if (currentLang == AppLanguage.ENGLISH) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                                            fontWeight = if (currentLang == AppLanguage.ENGLISH) FontWeight.Bold else FontWeight.Normal
                                        )
                                        if (currentLang == AppLanguage.ENGLISH) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Icon(Icons.Default.Check, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                                        }
                                    }
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.Language, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                },
                                onClick = {
                                    LanguageManager.setLanguage(AppLanguage.ENGLISH)
                                    expandedMenu = false
                                }
                            )

                            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                            // ── Theme Selection ──
                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            if (currentLang == AppLanguage.HINDI) "सिस्टम थीम (System)" else "System Theme",
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        if (themeMode == AppThemeMode.SYSTEM) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Icon(Icons.Default.Check, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                                        }
                                    }
                                },
                                leadingIcon = { Icon(Icons.Default.SettingsBrightness, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant) },
                                onClick = {
                                    ThemeManager.setThemeMode(AppThemeMode.SYSTEM)
                                    expandedMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            if (currentLang == AppLanguage.HINDI) "लाइट मोड (Light)" else "Light Mode",
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        if (themeMode == AppThemeMode.LIGHT) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Icon(Icons.Default.Check, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                                        }
                                    }
                                },
                                leadingIcon = { Icon(Icons.Default.LightMode, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant) },
                                onClick = {
                                    ThemeManager.setThemeMode(AppThemeMode.LIGHT)
                                    expandedMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            if (currentLang == AppLanguage.HINDI) "डार्क मोड (Dark)" else "Dark Mode",
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        if (themeMode == AppThemeMode.DARK) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Icon(Icons.Default.Check, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                                        }
                                    }
                                },
                                leadingIcon = { Icon(Icons.Default.DarkMode, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant) },
                                onClick = {
                                    ThemeManager.setThemeMode(AppThemeMode.DARK)
                                    expandedMenu = false
                                }
                            )

                            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                            // ── Login / Logout Account Action ──
                            if (UserSession.isLoggedIn) {
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            if (currentLang == AppLanguage.HINDI) "लॉगआउट (${UserSession.userName ?: "उपयोगकर्ता"})" else "Logout (${UserSession.userName ?: "User"})",
                                            color = StatusRed,
                                            fontWeight = FontWeight.Bold
                                        )
                                    },
                                    leadingIcon = { Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, tint = StatusRed) },
                                    onClick = {
                                        expandedMenu = false
                                        UserSession.logout()
                                        onLogoutClick?.invoke()
                                    }
                                )
                            } else {
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            if (currentLang == AppLanguage.HINDI) "लॉगिन करें (Login)" else "Sign In (Login)",
                                            color = MaterialTheme.colorScheme.primary,
                                            fontWeight = FontWeight.Bold
                                        )
                                    },
                                    leadingIcon = { Icon(Icons.AutoMirrored.Filled.Login, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                                    onClick = {
                                        expandedMenu = false
                                        onLoginClick?.invoke()
                                    }
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
                            color = if (isOnline) MaterialTheme.colorScheme.onSurfaceVariant else StatusOrange,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    // Official Stamp / Date
                    Text(
                        text = "IST 11:25 AM • NIC Cloud RJ",
                        fontSize = 9.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
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
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
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
