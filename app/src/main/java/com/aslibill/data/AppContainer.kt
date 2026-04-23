package com.aslibill.data

import android.content.Context
import com.aslibill.BuildConfig
import com.aslibill.bluetooth.BluetoothPrinterManager
import com.aslibill.network.ApiHttpClient
import com.aslibill.network.BackendHealthMonitor
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
    healthMonitor.start(intervalMs = 30_000)

    appScope.launch {
      authRepository.userSession.collect { session ->
        if (session != null) {
          appScope.launch(Dispatchers.IO) {
            runCatching {
              // Trigger initial fetch from API for all modules
              inventoryRepository.refresh()
              staffRepository.refresh()
              customerRepository.refresh()
              billingRepository.refresh()
              cashRepository.refresh()
              settingsRepository.syncFromRemote()

              val btConfig = bluetoothPrinterConfigRepository.loadRemoteConfig()
              bluetoothPrinterConfigRepository.saveLocalConfig(btConfig)
            }
          }
        }
      }
    }
  }

  suspend fun performLogout() {
    authRepository.logout()
    bluetoothPrinterManager.disconnect()
  }
}

