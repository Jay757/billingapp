package com.aslibill.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.aslibill.AsliBillApplication
import com.aslibill.data.SettingsRepository

class BluetoothPrinterViewModelFactory(
    private val app: AsliBillApplication
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BluetoothPrinterViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BluetoothPrinterViewModel(app, app.container.settingsRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
