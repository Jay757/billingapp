package com.aslibill.ui.theme

enum class ThemeMode {
    SYSTEM, LIGHT, DARK
}

data class UiPreferences(
    val mode: ThemeMode = ThemeMode.SYSTEM
)
