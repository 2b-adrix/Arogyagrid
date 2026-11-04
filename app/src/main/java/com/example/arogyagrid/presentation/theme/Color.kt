package com.example.arogyagrid.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color

// =========================================================================
// Authentic Indian Government & National Health Mission (NHM) Design System
// Inspired by NIC, MoHFW, RMSCL (e-Aushadhi), and Rajasthan Sarkar Health Portals
// Enhanced with High-Contrast Dark Mode & Semantic Surface Tokens
// =========================================================================

// Official National Tricolor & Government Identity Tokens
val TirangaSaffron = Color(0xFFFF671F)       // Official India Saffron
val TirangaSaffronLight = Color(0xFFFFF3E0)  // Saffron 10% tint (Light)
val TirangaWhite = Color(0xFFFFFFFF)        // Pure National White
val TirangaGreen = Color(0xFF046A38)        // Official India Green
val TirangaGreenLight = Color(0xFFE8F5E9)    // India Green 10% tint (Light)
val AshokaChakraBlue = Color(0xFF000080)    // Navy 24-spoke Ashoka Chakra

// Dark-Mode Specific Saffron & Green Tints
val TirangaSaffronDarkTint = Color(0xFF431407) // Deep Amber-Saffron container for dark mode
val TirangaGreenDarkTint = Color(0xFF052E16)   // Deep Forest-Green container for dark mode

// Rajasthan Govt & NIC Authoritative Navy Blue Palette
val GovtNavy = Color(0xFF003366)            // Official Indian Govt Deep Navy Blue
val GovtNavyDark = Color(0xFF001F3F)        // Deep Administrative Midnight
val GovtNavySoft = Color(0xFF0D47A1)        // Classic Secretariat Royal Blue
val GovtNavyLight = Color(0xFFEBF3FA)       // 10% Administrative Navy Tint
val GovtNavyDarkContainer = Color(0xFF0C2444) // Navy container in Dark Mode
val GovtGold = Color(0xFFB45309)            // Official Gold / Brass Emblem

// Primary Application Brand Mapping (Dignified National Health Palette)
val PrimaryTeal = GovtNavy                  // Official Deep Navy Primary (Light)
val PrimaryTealSoft = GovtNavySoft
val PrimaryTealLight = GovtNavyLight

val AccentBlue = TirangaSaffron             // Saffron Accent
val AccentBlueLight = TirangaSaffronLight

// Government Light Theme Surfaces (NIC Clean Bureaucratic Baseline)
val SlateBg = Color(0xFFF1F5F9)             // Crisp, official desk-light paper canvas
val SlateCard = Color(0xFFFFFFFF)           // Clean solid white cards
val SlateCardVariant = Color(0xFFF8FAFC)    // Subtle administrative row tint
val SlateBorderLight = Color(0xFFCBD5E1)    // Formal, structured 1dp gray border

// Government Dark Theme Surfaces (State Secretariat Operations Room)
val DarkBg = Color(0xFF0A0F1D)              // Ultra-deep rich slate navy 950
val DarkCard = Color(0xFF131D31)            // Refined slate navy 900
val DarkCardVariant = Color(0xFF1E293B)     // Refined slate navy 800
val DarkBorder = Color(0xFF334155)          // Clear, visible dark border

// Dynamic Adaptive SlateBorder: Matches MaterialTheme.colorScheme.outline
val SlateBorder: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.outline

// Typography Colors (High contrast, accessible, professional)
val SlateTextDark = Color(0xFF0F172A)
val SlateTextMuted = Color(0xFF475569)

val MidnightTextLight = Color(0xFFF1F5F9)
val MidnightTextMuted = Color(0xFF94A3B8)

val TextDark: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.onSurface

val TextMuted: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.onSurfaceVariant

val TextLight = Color(0xFFF8FAFC)

// Official Administrative Status Tokens
val StatusRed = Color(0xFFDC2626)           // Official Government Red (Urgent / Critical Deficit)
val StatusRedLight = Color(0xFFF87171)      // High-contrast Red for Dark Mode text

val StatusRedBg: Color
    @Composable
    @ReadOnlyComposable
    get() {
        val isDarkTheme = MaterialTheme.colorScheme.background == DarkBg
        return if (isDarkTheme) Color(0xFF450A0A) else Color(0xFFFEE2E2)
    }

val StatusOrange = Color(0xFFEA580C)        // Official Saffron/Amber (Caution / Buffer Low)
val StatusOrangeLight = Color(0xFFFB923C)   // High-contrast Amber for Dark Mode text

val StatusOrangeBg: Color
    @Composable
    @ReadOnlyComposable
    get() {
        val isDarkTheme = MaterialTheme.colorScheme.background == DarkBg
        return if (isDarkTheme) Color(0xFF431407) else Color(0xFFFFEDD5)
    }

val StatusGreen = Color(0xFF16A34A)         // Official Forest Green (Standard / Verified)
val StatusGreenLight = Color(0xFF4ADE80)    // High-contrast Green for Dark Mode text

val StatusGreenBg: Color
    @Composable
    @ReadOnlyComposable
    get() {
        val isDarkTheme = MaterialTheme.colorScheme.background == DarkBg
        return if (isDarkTheme) Color(0xFF052E16) else Color(0xFFDCFCE7)
    }
