package com.aslibill.data

import android.content.Context
import com.aslibill.BuildConfig
import com.aslibill.bluetooth.BluetoothPrinterManager
import com.aslibill.network.ApiHttpClient
import com.aslibill.network.BackendHealthMonitor
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class AppContainer(context: Context) {
  val appScope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

  val networkStatusRepository = NetworkStatusRepository()
  val apiClient = ApiHttpClient(BuildConfig.API_BASE_URL, networkStatusRepository)

  // AuthRepository must be declared first — other repositories depend on it
  val authRepository = AuthRepository(context, apiClient)
  
  // All repositories are now purely API-based, no local DAO dependencies
  val inventoryRepository = InventoryRepository(authRepository, apiClient)
  val billingRepository = BillingRepository(authRepository, apiClient)
  val customerRepository = CustomerRepository(authRepository, apiClient)
  val staffRepository = StaffRepository(authRepository, apiClient)
  val cashRepository = CashRepository(authRepository, apiClient)
  val analyticsRepository = AnalyticsRepository(authRepository, apiClient)
  val settingsRepository = SettingsRepository(context, authRepository, apiClient, appScope)

  val bluetoothPrinterManager = BluetoothPrinterManager(context, appScope)
  val bluetoothPrinterConfigRepository = BluetoothPrinterConfigRepository(authRepository, apiClient)

  private val healthMonitor = BackendHealthMonitor(apiClient, networkStatusRepository, appScope)

  init {
    // Check every 60s when online, every 5s when offline for fast reconnection
    healthMonitor.start(onlineIntervalMs = 60_000, offlineIntervalMs = 5_000)

    appScope.launch {
      // Monitor both session AND network status for seamless data loading
      authRepository.userSession.collect { session ->
        if (session != null) {
          // Trigger initial fetch if we're online
          if (networkStatusRepository.isOnline.value) {
            refreshData()
          }
        }
      }
    }

    appScope.launch {
      // When transitioning from Offline to Online, trigger a refresh
      networkStatusRepository.isOnline.collect { online ->
        if (online && authRepository.userSession.value != null) {
          refreshData()
        }
      }
    }
  }

  private fun refreshData() {
    appScope.launch(Dispatchers.IO) {
      runCatching {
        Log.d("AppContainer", "Refreshing data due to online status or session start")
        inventoryRepository.refresh()
        staffRepository.refresh()
        customerRepository.refresh()
        billingRepository.refresh()
        cashRepository.refresh()
        settingsRepository.syncFromRemote()

        val btConfig = bluetoothPrinterConfigRepository.loadRemoteConfig()
        bluetoothPrinterConfigRepository.saveLocalConfig(btConfig)
      }.onFailure {
        Log.e("AppContainer", "Failed to refresh data", it)
      }
    }
  }

  suspend fun performLogout() {
    authRepository.logout()
    bluetoothPrinterManager.disconnect()
  }

  fun checkConnection() {
    healthMonitor.checkNow()
  }
}

