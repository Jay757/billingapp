package com.aslibill.ui.theme

enum class ThemeMode {
  LIGHT,
  DARK,
  SYSTEM
}

enum class ThemePalette {
  BLUE,
  ORANGE,
  GREEN
}

data class UiPreferences(
  val mode: ThemeMode = ThemeMode.LIGHT,
  val palette: ThemePalette = ThemePalette.BLUE
)
