package com.aslibill.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance

private val LightColors = lightColorScheme(
  primary = AsliColors.Orange,
  secondary = AsliColors.Orange2,
  tertiary = AsliColors.Orange2,
  onPrimary = Color.White,
  surface = Color.White,
  onSurface = Color.Black,
  background = Color.White,
  onBackground = Color.Black
)

private val DarkColors = darkColorScheme(
  primary = AsliColors.Orange,
  secondary = AsliColors.Orange2,
  tertiary = AsliColors.Orange2,
  onPrimary = Color.Black,
  surface = AsliColors.Card,
  onSurface = AsliColors.TextPrimary,
  background = AsliColors.Bg,
  onBackground = AsliColors.TextPrimary
)

@Composable
fun AsliBillTheme(
  darkTheme: Boolean = true,
  content: @Composable () -> Unit
) {
  val scheme = if (darkTheme) DarkColors else LightColors
  MaterialTheme(
    colorScheme = scheme,
    typography = androidx.compose.material3.Typography(),
    content = content
  )
}

