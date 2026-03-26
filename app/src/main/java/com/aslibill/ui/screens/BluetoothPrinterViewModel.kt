package com.aslibill.ui.screens

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.aslibill.bluetooth.BluetoothPrinterManager
import com.aslibill.bluetooth.BtConnectionState
import com.aslibill.bluetooth.BtDeviceUi
import com.aslibill.data.BluetoothPrinterConfig
import com.aslibill.data.BluetoothPrinterConfigRepository
import com.aslibill.data.db.BillItemEntity
import com.aslibill.data.db.BillWithItemsRow
import com.aslibill.data.SettingsRepository
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import androidx.lifecycle.viewModelScope

class BluetoothPrinterViewModel(
    app: Application,
    private val settingsRepository: SettingsRepository,
    private val manager: BluetoothPrinterManager,
    private val configRepository: BluetoothPrinterConfigRepository
) : AndroidViewModel(app) {
  val devices: StateFlow<List<BtDeviceUi>> = manager.devices
  val state: StateFlow<BtConnectionState> = manager.state

  private var lastPersistedAddress: String? = null

  init {
    // Auto-connect previously saved printer (remote if logged in; local fallback).
    viewModelScope.launch {
      val cfg = configRepository.loadRemoteConfig()
      if (state.value.status != BtConnectionState.Status.CONNECTED && !cfg.deviceAddress.isNullOrBlank()) {
        try {
          manager.connect(cfg.deviceAddress)
        } catch (_: Throwable) {
          // Permissions (BLUETOOTH_CONNECT) might not be granted yet.
        }
      }
    }

    // Persist the selected printer when a connection becomes active.
    viewModelScope.launch {
      state.collect { s ->
        if (s.status == BtConnectionState.Status.CONNECTED && !s.connectedAddress.isNullOrBlank()) {
          if (lastPersistedAddress == s.connectedAddress) return@collect
          lastPersistedAddress = s.connectedAddress

          val deviceName = devices.value.firstOrNull { it.address == s.connectedAddress }?.name
          try {
            configRepository.persistRemoteConfig(
              BluetoothPrinterConfig(
                deviceAddress = s.connectedAddress,
                deviceName = deviceName
              )
            )
          } catch (_: Throwable) {
            // Best-effort persistence.
          }
        }
      }
    }
  }

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
    // Stop background discovery/receivers when leaving screens,
    // but keep the active socket connection alive for the rest of the app.
    manager.stopScan()
    super.onCleared()
  }
}

