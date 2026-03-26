package com.aslibill.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.mutableStateOf

object AsliColors {
  private val paletteState = mutableStateOf(ThemePalette.BLUE)

  fun applyPalette(palette: ThemePalette) {
    paletteState.value = palette
  }

  val Bg = Color(0xFFF4F8FF)
  val Card = Color(0xFFFFFFFF)
  val Card2 = Color(0xFFEAF2FF)
  val DividerOrange: Color
    get() = when (paletteState.value) {
      ThemePalette.BLUE -> Color(0xFF3B82F6)
      ThemePalette.ORANGE -> Color(0xFFF59E0B)
      ThemePalette.GREEN -> Color(0xFF22C55E)
    }
  val Orange: Color
    get() = DividerOrange
  val Orange2: Color
    get() = when (paletteState.value) {
      ThemePalette.BLUE -> Color(0xFF60A5FA)
      ThemePalette.ORANGE -> Color(0xFFFBBF24)
      ThemePalette.GREEN -> Color(0xFF4ADE80)
    }
  val Green = Color(0xFF22C55E)
  val Red = Color(0xFFEF4444)
  val TextPrimary = Color(0xFF0F172A)
  val TextSecondary = Color(0xFF475569)
}

