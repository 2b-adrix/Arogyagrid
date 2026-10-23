package com.example.arogyagrid.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Primary Accents (Vibrant & Neon)
val PrimaryTeal = Color(0xFF6366F1) // Indigo Primary
val PrimaryTealSoft = Color(0xFF818CF8)
val PrimaryTealLight = Color(0x266366F1) // Adaptive 15% Alpha

val AccentBlue = Color(0xFFEC4899) // Vibrant Pink
val AccentBlueLight = Color(0x26EC4899) // Adaptive 15% Alpha

// Light Theme - Clay Base
val SlateBg = Color(0xFFE2E8F0) // Slightly deeper soft background to make white cards pop
val SlateCard = Color(0xFFF8FAFC)
val SlateCardVariant = Color(0xFFF1F5F9)
val SlateBorder = Color(0xFFFFFFFF) // White border creates the clay inset

// Dark Theme - Midnight Aurora (NO GREY, Pure vibrant midnight blues)
val DarkBg = Color(0xFF040614) // Deep midnight space
val DarkCard = Color(0xFF0B1021) // Rich midnight card
val DarkCardVariant = Color(0xFF141A33) // Elevated neon card
val DarkBorder = Color(0xFF222A4F) // Glowing indigo border

// Typography Colors
val SlateTextDark = Color(0xFF0F172A)
val SlateTextMuted = Color(0xFF64748B)

val MidnightTextLight = Color(0xFFF8FAFC)
val MidnightTextMuted = Color(0xFF94A3B8)

val TextDark: Color
    @Composable get() = MaterialTheme.colorScheme.onSurface
    
val TextMuted: Color
    @Composable get() = MaterialTheme.colorScheme.onSurfaceVariant

val TextLight = Color(0xFFF8FAFC) 

// Status Colors (Vibrant Neon)
val StatusRed = Color(0xFFFF4D6D) // Neon Red
val StatusRedBg = Color(0x26FF4D6D)

val StatusOrange = Color(0xFFFF9F1C) // Neon Orange
val StatusOrangeBg = Color(0x26FF9F1C)

val StatusGreen = Color(0xFF06D6A0) // Neon Green
val StatusGreenBg = Color(0x2606D6A0)
