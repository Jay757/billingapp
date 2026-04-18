package com.aslibill.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.Composable
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.isSystemInDarkTheme

object AsliColors {
    // Light Mode Palette
    val BgLight = Color(0xFFF8FAFC)        // Slate 50
    val CardLight = Color(0xFFFFFFFF)
    val PrimaryLight = Color(0xFF2563EB)     // Blue 600
    val SurfaceAccentLight = Color(0xFFEFF6FF) // Blue 50
    val TextPrimaryLight = Color(0xFF0F172A)   // Slate 900
    val TextSecondaryLight = Color(0xFF475569) // Slate 600

    // Dark Mode Palette
    val BgDark = Color(0xFF020617)         // Slate 950
    val CardDark = Color(0xFF1E293B)         // Slate 800
    val PrimaryDark = Color(0xFF60A5FA)      // Blue 400
    val SurfaceAccentDark = Color(0xFF1E293B)  // Slate 800
    val TextPrimaryDark = Color(0xFFF8FAFC)    // Slate 50
    val TextSecondaryDark = Color(0xFF94A3B8)  // Slate 400

    // Common/Brand Colors - Theme-aware versions
    val Green @Composable get() = if (isSystemInDarkTheme()) Color(0xFF34D399) else Color(0xFF10B981)
    val Red @Composable get() = if (isSystemInDarkTheme()) Color(0xFFFB7185) else Color(0xFFEF4444)
    val Orange @Composable get() = if (isSystemInDarkTheme()) Color(0xFFFBBF24) else Color(0xFFF59E0B)

    val PrimaryBlue @Composable get() = MaterialTheme.colorScheme.primary
    val SuccessGreen @Composable get() = if (isSystemInDarkTheme()) Color(0xFF34D399) else Color(0xFF10B981)
    val AlertOrange @Composable get() = if (isSystemInDarkTheme()) Color(0xFFFBBF24) else Color(0xFFF59E0B)

    // Helper to get color based on theme - though preferred is MaterialTheme.colorScheme
    @Composable
    fun primary() = MaterialTheme.colorScheme.primary
    
    @Composable
    fun onSurface() = MaterialTheme.colorScheme.onSurface

    // Legacy compatibility - Mapping these to MaterialTheme for automatic dark mode support
    val Primary @Composable get() = MaterialTheme.colorScheme.primary
    val PrimaryLightColor @Composable get() = MaterialTheme.colorScheme.secondary
    val TextPrimary @Composable get() = MaterialTheme.colorScheme.onSurface
    val TextSecondary @Composable get() = MaterialTheme.colorScheme.onSurfaceVariant
    val Bg @Composable get() = MaterialTheme.colorScheme.background
    val Card @Composable get() = MaterialTheme.colorScheme.surface
    val Card2 @Composable get() = MaterialTheme.colorScheme.surfaceVariant
}

