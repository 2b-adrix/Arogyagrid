package com.example.arogyagrid.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// =========================================================================
// Authentic Indian Government & National Health Mission (NHM) Design System
// Inspired by NIC, MoHFW, RMSCL (e-Aushadhi), and Rajasthan Sarkar Health Portals
// =========================================================================

// Official National Tricolor & Government Identity Tokens
val TirangaSaffron = Color(0xFFFF671F)       // Official India Saffron
val TirangaSaffronLight = Color(0xFFFFF3E0)  // Saffron 10% tint
val TirangaWhite = Color(0xFFFFFFFF)        // Pure National White
val TirangaGreen = Color(0xFF046A38)        // Official India Green
val TirangaGreenLight = Color(0xFFE8F5E9)    // India Green 10% tint
val AshokaChakraBlue = Color(0xFF000080)    // Navy 24-spoke Ashoka Chakra

// Rajasthan Govt & NIC Authoritative Navy Blue Palette
val GovtNavy = Color(0xFF003366)            // Official Indian Govt Deep Navy Blue
val GovtNavyDark = Color(0xFF001F3F)        // Deep Administrative Midnight
val GovtNavySoft = Color(0xFF0D47A1)        // Classic Secretariat Royal Blue
val GovtNavyLight = Color(0xFFEBF3FA)       // 10% Administrative Navy Tint
val GovtGold = Color(0xFFB45309)            // Official Gold / Brass Emblem

// Primary Application Brand Mapping (Dignified National Health Palette)
val PrimaryTeal = GovtNavy                  // Official Deep Navy Primary (Replaces AI neon)
val PrimaryTealSoft = GovtNavySoft
val PrimaryTealLight = GovtNavyLight

val AccentBlue = TirangaSaffron             // Saffron Accent (Replaces AI pink)
val AccentBlueLight = TirangaSaffronLight

// Government Light Theme Surfaces (NIC Clean Bureaucratic Baseline)
val SlateBg = Color(0xFFF1F5F9)             // Crisp, official desk-light paper canvas
val SlateCard = Color(0xFFFFFFFF)           // Clean solid white cards
val SlateCardVariant = Color(0xFFF8FAFC)    // Subtle administrative row tint
val SlateBorder = Color(0xFFCBD5E1)         // Formal, structured 1dp gray border

// Government Dark Theme (State Secretariat Operations Room)
val DarkBg = Color(0xFF0F172A)              // Formal Slate 900 (No cartoon neon space)
val DarkCard = Color(0xFF1E293B)            // Formal Slate 800
val DarkCardVariant = Color(0xFF334155)     // Formal Slate 700
val DarkBorder = Color(0xFF475569)          // Muted slate border

// Typography Colors (High contrast, accessible, professional)
val SlateTextDark = Color(0xFF0F172A)
val SlateTextMuted = Color(0xFF475569)

val MidnightTextLight = Color(0xFFF8FAFC)
val MidnightTextMuted = Color(0xFF94A3B8)

val TextDark: Color
    @Composable get() = MaterialTheme.colorScheme.onSurface

val TextMuted: Color
    @Composable get() = MaterialTheme.colorScheme.onSurfaceVariant

val TextLight = Color(0xFFF8FAFC)

// Official Administrative Status Tokens (Honest, Human, Serious)
val StatusRed = Color(0xFFB91C1C)           // Official Government Red (Urgent / Critical Deficit)
val StatusRedBg = Color(0xFFFEE2E2)         // 10% Red Tint

val StatusOrange = Color(0xFFC2410C)        // Official Saffron/Amber (Caution / Buffer Low)
val StatusOrangeBg = Color(0xFFFFEDD5)      // 10% Orange Tint

val StatusGreen = Color(0xFF15803D)         // Official Forest Green (Standard / Verified)
val StatusGreenBg = Color(0xFFDCFCE7)       // 10% Green Tint
