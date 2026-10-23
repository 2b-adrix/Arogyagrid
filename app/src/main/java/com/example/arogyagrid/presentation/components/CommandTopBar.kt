package com.example.arogyagrid.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SettingsBrightness
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.arogyagrid.R
import com.example.arogyagrid.core.AppThemeMode
import com.example.arogyagrid.core.ThemeManager
import com.example.arogyagrid.presentation.theme.PrimaryTeal
import com.example.arogyagrid.presentation.theme.PrimaryTealLight
import com.example.arogyagrid.presentation.theme.SlateBorder
import com.example.arogyagrid.presentation.theme.StatusRed
import com.example.arogyagrid.presentation.theme.TextDark
import com.example.arogyagrid.presentation.theme.TextMuted


@Composable
fun CommandTopBar(
    roleLabel: String = "District Officer",
    districtName: String = "Jaipur",
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onSyncClick: () -> Unit = {},
    onCopilotClick: (() -> Unit)? = null,
    onProfileClick: (() -> Unit)? = null
) {
    val themeMode by ThemeManager.themeMode.collectAsState()
    var expandedThemeMenu by remember { mutableStateOf(false) }

    Surface(
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 1.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(PrimaryTealLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_smart_health_logo),
                            contentDescription = "SmartHealth Logo",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "SmartHealth",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextDark
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Wifi,
                                contentDescription = "Online",
                                tint = PrimaryTeal,
                                modifier = Modifier.size(10.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Live Sync • Just now",
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                        }
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box {
                        IconButton(onClick = { expandedThemeMenu = true }) {
                            val currentIcon = when (themeMode) {
                                AppThemeMode.SYSTEM -> Icons.Default.SettingsBrightness
                                AppThemeMode.LIGHT -> Icons.Default.LightMode
                                AppThemeMode.DARK -> Icons.Default.DarkMode
                            }
                            Icon(
                                imageVector = currentIcon,
                                contentDescription = "Theme Options",
                                tint = TextMuted
                            )
                        }
                        
                        DropdownMenu(
                            expanded = expandedThemeMenu,
                            onDismissRequest = { expandedThemeMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("System Theme") },
                                leadingIcon = { Icon(Icons.Default.SettingsBrightness, contentDescription = null) },
                                onClick = {
                                    ThemeManager.setThemeMode(AppThemeMode.SYSTEM)
                                    expandedThemeMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Light Mode") },
                                leadingIcon = { Icon(Icons.Default.LightMode, contentDescription = null) },
                                onClick = {
                                    ThemeManager.setThemeMode(AppThemeMode.LIGHT)
                                    expandedThemeMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Dark Mode") },
                                leadingIcon = { Icon(Icons.Default.DarkMode, contentDescription = null) },
                                onClick = {
                                    ThemeManager.setThemeMode(AppThemeMode.DARK)
                                    expandedThemeMenu = false
                                }
                            )
                        }
                    }

                    onCopilotClick?.let { onClick ->
                        IconButton(onClick = onClick) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "AI Copilot",
                                tint = PrimaryTeal
                            )
                        }
                    }

                    IconButton(onClick = onSyncClick) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh Data",
                            tint = TextMuted
                        )
                    }

                    BadgedBox(
                        badge = {
                            Badge(containerColor = StatusRed) {
                                Text("3", color = Color.White, fontSize = 9.sp)
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Alerts",
                            tint = TextMuted,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = PrimaryTealLight,
                        modifier = Modifier
                            .padding(start = 2.dp)
                            .then(
                                if (onProfileClick != null) Modifier.clickable { onProfileClick.invoke() }
                                else Modifier
                            )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(22.dp)
                                    .clip(CircleShape)
                                    .background(PrimaryTeal),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = roleLabel.take(1),
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text(
                                    text = roleLabel,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryTeal
                                )
                                Text(
                                    text = districtName,
                                    fontSize = 9.sp,
                                    color = TextMuted
                                )
                            }
                        }
                    }
                }
            }

            // Search Bar Inline
            Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp)) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    placeholder = { Text("Search medicines, facilities, inventory...", fontSize = 12.sp, color = TextMuted) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextMuted) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        focusedBorderColor = PrimaryTeal,
                        unfocusedBorderColor = SlateBorder
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CommandTopBarPreview() {
    MaterialTheme {
        CommandTopBar(
            searchQuery = "",
            onSearchQueryChange = {}
        )
    }
}
