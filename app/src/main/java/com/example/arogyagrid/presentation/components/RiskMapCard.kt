package com.example.arogyagrid.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.arogyagrid.core.AppThemeMode
import com.example.arogyagrid.core.ThemeManager
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.arogyagrid.domain.models.Facility
import com.example.arogyagrid.presentation.theme.SlateBorder
import com.example.arogyagrid.presentation.theme.StatusGreen
import com.example.arogyagrid.presentation.theme.StatusOrange
import com.example.arogyagrid.presentation.theme.StatusRed
import com.example.arogyagrid.presentation.theme.TextDark
import com.example.arogyagrid.presentation.theme.TextMuted
import com.example.arogyagrid.presentation.theme.clay

@Composable
fun RiskMapCard(
    facilities: List<Facility>,
    modifier: Modifier = Modifier
) {
    val themeMode by ThemeManager.themeMode.collectAsState()
    val isDark = when (themeMode) {
        AppThemeMode.SYSTEM -> isSystemInDarkTheme()
        AppThemeMode.LIGHT -> false
        AppThemeMode.DARK -> true
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clay(
                backgroundColor = MaterialTheme.colorScheme.surface,
                cornerRadius = 24.dp,
                elevation = 12.dp,
                isDarkTheme = isDark
            )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Healthcare Network Map",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Real-time facility risk & capacity across Jaipur District",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Map Simulation Area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(190.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))
            ) {
                // Pins
                facilities.take(6).forEachIndexed { i, f ->
                    val isCritical = f.name.contains("PHC 1") || f.occupancyPercentage > 90
                    val isHigh = f.occupancyPercentage > 80
                    val pinColor = when {
                        isCritical -> StatusRed
                        isHigh -> StatusOrange
                        else -> StatusGreen
                    }

                    Box(
                        modifier = Modifier
                            .align(
                                when (i) {
                                    0 -> Alignment.TopStart
                                    1 -> Alignment.TopCenter
                                    2 -> Alignment.TopEnd
                                    3 -> Alignment.CenterStart
                                    4 -> Alignment.Center
                                    else -> Alignment.BottomEnd
                                }
                            )
                            .padding(14.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.95f), shape = RoundedCornerShape(8.dp))
                                .border(0.5.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = f.name,
                                tint = pinColor,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = f.name.replace("District Hospital", "DH"),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                // Legend Bar
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.95f))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    LegendDot(color = StatusRed, label = "Critical Risk (1)")
                    LegendDot(color = StatusOrange, label = "High Pressure (2)")
                    LegendDot(color = StatusGreen, label = "Normal (21)")
                }
            }
        }
    }
}

@Composable
private fun LegendDot(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = label, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f), fontWeight = FontWeight.Medium)
    }
}
