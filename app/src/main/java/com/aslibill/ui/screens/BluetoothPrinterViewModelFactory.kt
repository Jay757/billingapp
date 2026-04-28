package com.aslibill.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.aslibill.BillSuperApplication
import com.aslibill.data.SettingsRepository
import com.aslibill.bluetooth.BluetoothPrinterManager

class BluetoothPrinterViewModelFactory(
    private val app: BillSuperApplication
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BluetoothPrinterViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BluetoothPrinterViewModel(
                app,
                app.container.settingsRepository,
                app.container.bluetoothPrinterManager,
                app.container.bluetoothPrinterConfigRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
