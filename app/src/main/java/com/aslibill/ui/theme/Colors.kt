package com.aslibill.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.mutableStateOf

object AsliColors {
  private val paletteState = mutableStateOf(ThemePalette.BLUE)

  fun applyPalette(palette: ThemePalette) {
    paletteState.value = palette
  }

  val Bg = Color(0xFFF8FAFC)
  val Card = Color(0xFFFFFFFF)
  val Card2 = Color(0xFFF1F5F9)
  
  val Primary: Color
    get() = when (paletteState.value) {
      ThemePalette.BLUE -> Color(0xFF2563EB)
      ThemePalette.ORANGE -> Color(0xFFEA580C)
      ThemePalette.GREEN -> Color(0xFF16A34A)
    }

  val PrimaryLight: Color
    get() = when (paletteState.value) {
      ThemePalette.BLUE -> Color(0xFFDBEAFE)
      ThemePalette.ORANGE -> Color(0xFFFFEDD5)
      ThemePalette.GREEN -> Color(0xFFDCFCE7)
    }

  val Orange: Color get() = Primary
  val Orange2: Color get() = PrimaryLight
  val DividerOrange: Color get() = Primary

  val Green = Color(0xFF22C55E)
  val Red = Color(0xFFEF4444)
  val TextPrimary = Color(0xFF0F172A)
  val TextSecondary = Color(0xFF64748B)
  val Surface = Color(0xFFFFFFFF)
}


