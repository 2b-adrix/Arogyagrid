package com.example.arogyagrid.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = PrimaryTeal,
    secondary = PrimaryTealSoft,
    tertiary = AccentBlue,
    background = SlateBg,
    surface = SlateCard,
    surfaceVariant = SlateCardVariant,
    onPrimary = SlateCard,
    onBackground = SlateTextDark,
    onSurface = SlateTextDark,
    onSurfaceVariant = SlateTextMuted,
    outline = SlateBorder
)

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryTealSoft,
    secondary = PrimaryTeal,
    tertiary = AccentBlueLight,
    background = DarkBg,
    surface = DarkCard,
    surfaceVariant = DarkCardVariant,
    onPrimary = Color.Black,
    onBackground = MidnightTextLight,
    onSurface = MidnightTextLight,
    onSurfaceVariant = MidnightTextMuted,
    outline = DarkBorder
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
