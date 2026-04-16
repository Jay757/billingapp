package com.aslibill.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.material3.lightColorScheme
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.graphics.Color

private fun lightColors() = lightColorScheme(
    primary = AsliColors.Orange,
    secondary = AsliColors.Orange2,
    tertiary = AsliColors.Green,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    surface = Color(0xFFF7FAFF),
    onSurface = Color(0xFF0F172A),
    background = Color(0xFFF2F6FD),
    onBackground = Color(0xFF0F172A),
    error = AsliColors.Red,
    onError = Color.White
)

private fun darkColors() = darkColorScheme(
    primary = AsliColors.Orange,
    secondary = AsliColors.Orange2,
    tertiary = AsliColors.Green,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.Black,
    surface = Color(0xFF121826),
    onSurface = Color(0xFFF8FAFC),
    background = Color(0xFF0B1220),
    onBackground = Color(0xFFF8FAFC),
    error = AsliColors.Red,
    onError = Color.White
)

@Composable
fun NovaBillTheme(
  mode: ThemeMode = ThemeMode.LIGHT,
  palette: ThemePalette = ThemePalette.BLUE,
  content: @Composable () -> Unit
) {
  AsliColors.applyPalette(palette)
  val useDarkTheme = when (mode) {
    ThemeMode.DARK -> true
    ThemeMode.LIGHT -> false
    ThemeMode.SYSTEM -> isSystemInDarkTheme()
  }
  val scheme = if (useDarkTheme) darkColors() else lightColors()
  MaterialTheme(
    colorScheme = scheme,
    typography = AppTypography,
    content = content
  )
}

