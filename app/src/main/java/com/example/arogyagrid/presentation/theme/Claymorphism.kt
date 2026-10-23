package com.example.arogyagrid.presentation.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * A modifier that applies a stunning 3D claymorphic puffed effect.
 * Uses directional gradients to simulate volumetric lighting and depth.
 */
fun Modifier.clay(
    backgroundColor: Color,
    cornerRadius: Dp = 24.dp,
    elevation: Dp = 10.dp,
    isDarkTheme: Boolean = false
): Modifier = this
    // 1. Soft 3D Drop Shadow
    .shadow(
        elevation = if (isDarkTheme) 6.dp else elevation,
        shape = RoundedCornerShape(cornerRadius),
        spotColor = if (isDarkTheme) Color.Black else Color(0x33000000), // Richer shadow
        ambientColor = if (isDarkTheme) Color.Black else Color(0x11000000)
    )
    // 2. Clip to Shape
    .clip(RoundedCornerShape(cornerRadius))
    // 3. Base Background Color
    .background(backgroundColor)
    // 4. 3D Puffed Gradient Overlay (Simulates volumetric curve)
    .background(
        brush = Brush.linearGradient(
            colors = if (isDarkTheme) {
                listOf(
                    Color(0x22FFFFFF), // Top-left light catch
                    Color(0x00FFFFFF), // Mid-point flat
                    Color(0x44000000)  // Bottom-right shadow curve
                )
            } else {
                listOf(
                    Color(0x66FFFFFF), // Bright top-left rim
                    Color(0x00FFFFFF), // Flat center
                    Color(0x11000000)  // Soft bottom-right dip
                )
            }
        )
    )
    // 5. Specular Highlight Border (Catches light on top, shadows on bottom)
    .border(
        width = 1.5.dp,
        brush = Brush.linearGradient(
            colors = if (isDarkTheme) {
                listOf(
                    Color(0x44FFFFFF), // Bright edge top-left
                    Color(0x05FFFFFF),
                    Color(0x22000000)  // Shaded edge bottom-right
                )
            } else {
                listOf(
                    Color(0xFFFFFFFF), // Pure white rim light
                    Color(0x44FFFFFF),
                    Color(0x05000000)
                )
            }
        ),
        shape = RoundedCornerShape(cornerRadius)
    )
