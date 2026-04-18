package com.aslibill.data

import android.content.Context
import com.aslibill.printing.StoreConfig
import com.aslibill.ui.theme.ThemeMode
import com.aslibill.ui.theme.UiPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsRepository(context: Context) {
    private val prefs = context.getSharedPreferences("print_settings", Context.MODE_PRIVATE)

    private val _settings = MutableStateFlow(loadSettings())
    val settings: StateFlow<StoreConfig> = _settings.asStateFlow()
    private val _uiPreferences = MutableStateFlow(loadUiPreferences())
    val uiPreferences: StateFlow<UiPreferences> = _uiPreferences.asStateFlow()

    private fun loadSettings(): StoreConfig {
        val storeName = prefs.getString("store_name", "NOVABILL") ?: "NOVABILL"
        val address1 = prefs.getString("address_1", "Address line 1") ?: "Address line 1"
        val address2 = prefs.getString("address_2", "Address line 2") ?: "Address line 2"
        val phone = prefs.getString("phone", "") ?: ""
        val gst = prefs.getString("gst", "") ?: ""
        val thankYou = prefs.getString("thank_you", "THANK YOU") ?: "THANK YOU"
        val paperWidth = prefs.getInt("paper_width", 32)
        
        return StoreConfig(
            storeName = storeName,
            addressLines = listOf(address1, address2),
            phone = if (phone.isBlank()) null else phone,
            gstNumber = if (gst.isBlank()) null else gst,
            thankYouMessage = if (thankYou.isBlank()) null else thankYou,
            paperWidthChars = paperWidth
        )
    }

    fun saveSettings(config: StoreConfig) {
        prefs.edit().apply {
            putString("store_name", config.storeName)
            putString("address_1", config.addressLines.getOrNull(0) ?: "")
            putString("address_2", config.addressLines.getOrNull(1) ?: "")
            putString("phone", config.phone ?: "")
            putString("gst", config.gstNumber ?: "")
            putString("thank_you", config.thankYouMessage ?: "")
            putInt("paper_width", config.paperWidthChars)
            apply()
        }
        _settings.value = config
    }

    private fun loadUiPreferences(): UiPreferences {
        val mode = prefs.getString("theme_mode", ThemeMode.LIGHT.name)
        return UiPreferences(
            mode = runCatching { ThemeMode.valueOf(mode ?: ThemeMode.LIGHT.name) }.getOrDefault(ThemeMode.LIGHT)
        )
    }

    fun saveUiPreferences(mode: ThemeMode) {
        prefs.edit().apply {
            putString("theme_mode", mode.name)
            apply()
        }
        _uiPreferences.value = UiPreferences(mode = mode)
    }
}
