package com.example.arogyagrid.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = PrimaryTeal,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE0EDF8),
    onPrimaryContainer = GovtNavyDark,
    secondary = PrimaryTealSoft,
    onSecondary = Color.White,
    tertiary = AccentBlue,
    background = SlateBg,
    onBackground = SlateTextDark,
    surface = SlateCard,
    onSurface = SlateTextDark,
    surfaceVariant = SlateCardVariant,
    onSurfaceVariant = SlateTextMuted,
    outline = SlateBorderLight,
    outlineVariant = Color(0xFFE2E8F0)
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF38BDF8),             // Sky 400 - Vivid, high-legibility cyan primary
    onPrimary = Color(0xFF031628),           // Deep contrast text on primary
    primaryContainer = Color(0xFF0C2444),    // Rich deep navy container
    onPrimaryContainer = Color(0xFFBAE6FD),  // Sky 200 readable container text
    secondary = Color(0xFF60A5FA),           // Blue 400
    onSecondary = Color(0xFF0F172A),
    tertiary = Color(0xFFFBBF24),            // Warm amber
    background = DarkBg,                     // Slate 950 deep canvas
    onBackground = MidnightTextLight,        // High-contrast clean text
    surface = DarkCard,                      // Slate 900 card surface
    onSurface = MidnightTextLight,           // Crisp, clear white-slate text
    surfaceVariant = DarkCardVariant,        // Slate 800 subtle container
    onSurfaceVariant = MidnightTextMuted,    // Slate 400 muted text
    outline = DarkBorder,                    // Slate 700 visible border
    outlineVariant = Color(0xFF1E293B)
)

@Composable
fun SmartHealthTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}
