package com.billsuper.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.billsuper.data.SettingsRepository
import com.billsuper.printing.StoreConfig
import com.billsuper.ui.theme.ThemeMode
import com.billsuper.ui.theme.UiPreferences
import kotlinx.coroutines.flow.StateFlow
import com.billsuper.data.UserSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PrintSettingsViewModel(
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    val settings: StateFlow<StoreConfig> = settingsRepository.settings
    val uiPreferences: StateFlow<UiPreferences> = settingsRepository.uiPreferences
    val userSession: StateFlow<UserSession?> = settingsRepository.userSession

    fun saveSettings(
        storeName: String,
        address: String,
        phone: String,
        gst: String,
        thankYou: String,
        paperWidth: Int
    ) {
        val config = StoreConfig(
            storeName = storeName,
            address = address,
            phone = if (phone.isBlank()) null else phone,
            gstNumber = if (gst.isBlank()) null else gst,
            thankYouMessage = if (thankYou.isBlank()) null else thankYou,
            paperWidthChars = paperWidth
        )
        viewModelScope.launch {
            _isLoading.value = true
            try {
                settingsRepository.saveSettings(config)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun saveAppearance(mode: ThemeMode) {
        viewModelScope.launch {
            settingsRepository.saveUiPreferences(mode)
        }
    }
}

