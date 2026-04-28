package com.billsuper.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = BillSuperColors.PrimaryDark,
    secondary = BillSuperColors.SurfaceAccentDark,
    background = BillSuperColors.BgDark,
    surface = BillSuperColors.CardDark,
    onPrimary = Color.Black,
    onSecondary = Color.White,
    onBackground = BillSuperColors.TextPrimaryDark,
    onSurface = BillSuperColors.TextPrimaryDark,
    surfaceVariant = BillSuperColors.SurfaceAccentDark,
    onSurfaceVariant = BillSuperColors.TextSecondaryDark
)

private val LightColorScheme = lightColorScheme(
    primary = BillSuperColors.PrimaryLight,
    secondary = BillSuperColors.SurfaceAccentLight,
    background = BillSuperColors.BgLight,
    surface = BillSuperColors.CardLight,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = BillSuperColors.TextPrimaryLight,
    onSurface = BillSuperColors.TextPrimaryLight,
    surfaceVariant = BillSuperColors.SurfaceAccentLight,
    onSurfaceVariant = BillSuperColors.TextSecondaryLight
)

@Composable
fun BillSuperTheme(
    mode: ThemeMode = ThemeMode.SYSTEM,
    content: @Composable () -> Unit
) {
    val darkTheme = when (mode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }
    
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current

    if (!view.isInEditMode) {
        @Suppress("DEPRECATION")
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppMainTypography,
        content = content
    )
}


