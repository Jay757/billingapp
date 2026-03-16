package com.aslibill.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aslibill.data.SettingsRepository
import com.aslibill.printing.StoreConfig
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PrintSettingsViewModel(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    val settings: StateFlow<StoreConfig> = settingsRepository.settings

    fun saveSettings(
        storeName: String,
        address1: String,
        address2: String,
        phone: String,
        gst: String,
        thankYou: String,
        paperWidth: Int
    ) {
        val config = StoreConfig(
            storeName = storeName,
            addressLines = listOf(address1, address2),
            phone = if (phone.isBlank()) null else phone,
            gstNumber = if (gst.isBlank()) null else gst,
            thankYouMessage = if (thankYou.isBlank()) null else thankYou,
            paperWidthChars = paperWidth
        )
        viewModelScope.launch {
            settingsRepository.saveSettings(config)
        }
    }
}
