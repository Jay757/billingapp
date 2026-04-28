package com.billsuper.ui.screens

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.billsuper.bluetooth.BluetoothPrinterManager
import com.billsuper.bluetooth.BtConnectionState
import com.billsuper.bluetooth.BtDeviceUi
import com.billsuper.data.BluetoothPrinterConfig
import com.billsuper.data.BluetoothPrinterConfigRepository
import com.billsuper.data.db.BillItemEntity
import com.billsuper.data.db.BillWithItemsRow
import com.billsuper.data.SettingsRepository
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import androidx.lifecycle.viewModelScope

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class BluetoothPrinterViewModel(
    app: Application,
    private val settingsRepository: SettingsRepository,
    private val manager: BluetoothPrinterManager,
    private val configRepository: BluetoothPrinterConfigRepository
) : AndroidViewModel(app) {
  val devices: StateFlow<List<BtDeviceUi>> = manager.devices
  val state: StateFlow<BtConnectionState> = manager.state

  private val _isLoading = MutableStateFlow(false)
  val isLoading = _isLoading.asStateFlow()

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
      _isLoading.value = true
      try {
        val config = settingsRepository.settings.value
        val res = manager.testPrint(title = config.storeName)
        onDone(res.isSuccess, res.exceptionOrNull()?.message)
      } finally {
        _isLoading.value = false
      }
    }
  }

  fun printBill(
    bill: BillWithItemsRow,
    items: List<BillItemEntity>,
    onDone: (Boolean, String?) -> Unit
  ) {
    viewModelScope.launch {
      _isLoading.value = true
      try {
        val config = settingsRepository.settings.value
        val res = manager.printBill(bill, items, config)
        onDone(res.isSuccess, res.exceptionOrNull()?.message)
      } finally {
        _isLoading.value = false
      }
    }
  }

  override fun onCleared() {
    // Stop background discovery/receivers when leaving screens,
    // but keep the active socket connection alive for the rest of the app.
    manager.stopScan()
    super.onCleared()
  }
}


