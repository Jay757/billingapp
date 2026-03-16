package com.aslibill.ui.screens

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.aslibill.bluetooth.BluetoothPrinterManager
import com.aslibill.bluetooth.BtConnectionState
import com.aslibill.bluetooth.BtDeviceUi
import com.aslibill.data.db.BillItemEntity
import com.aslibill.data.db.BillWithItemsRow
import com.aslibill.data.SettingsRepository
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class BluetoothPrinterViewModel(
    app: Application,
    private val settingsRepository: SettingsRepository
) : AndroidViewModel(app) {
  private val manager = BluetoothPrinterManager(app.applicationContext, viewModelScope)

  val devices: StateFlow<List<BtDeviceUi>> = manager.devices
  val state: StateFlow<BtConnectionState> = manager.state

  fun isAvailable() = manager.isBluetoothAvailable()
  fun isEnabled() = manager.isBluetoothEnabled()

  fun startScan() = manager.startScan()
  fun stopScan() = manager.stopScan()

  fun connect(address: String) = manager.connect(address)
  fun disconnect() = manager.disconnect()

  fun testPrint(onDone: (Boolean, String?) -> Unit) {
    viewModelScope.launch {
      val config = settingsRepository.settings.value
      val res = manager.testPrint(title = config.storeName)
      onDone(res.isSuccess, res.exceptionOrNull()?.message)
    }
  }

  fun printBill(
    bill: BillWithItemsRow,
    items: List<BillItemEntity>,
    onDone: (Boolean, String?) -> Unit
  ) {
    viewModelScope.launch {
      val config = settingsRepository.settings.value
      val res = manager.printBill(bill, items, config)
      onDone(res.isSuccess, res.exceptionOrNull()?.message)
    }
  }

  override fun onCleared() {
    manager.close()
    super.onCleared()
  }
}

