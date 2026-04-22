package com.aslibill.data

import android.content.Context
import com.aslibill.BuildConfig
import com.aslibill.bluetooth.BluetoothPrinterManager
import com.aslibill.data.db.AppDatabase
import com.aslibill.data.db.BillDao
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

  private val db = AppDatabase.get(context)

  // authRepository must be declared first — BluetoothPrinterConfigRepository depends on it
  val authRepository = AuthRepository(context, apiClient)
  val inventoryRepository = InventoryRepository(db.categoryDao(), db.productDao(), authRepository, apiClient)
  val billingRepository = BillingRepository(db.billDao(), db.productDao(), authRepository, apiClient)
  val customerRepository = CustomerRepository(db.customerDao(), authRepository, apiClient)
  val staffRepository = StaffRepository(db.staffDao(), authRepository, apiClient)
  val cashRepository = CashRepository(db.cashDao(), authRepository, apiClient)
  val analyticsRepository = AnalyticsRepository(db.billAnalyticsDao(), authRepository, apiClient)
  val settingsRepository = SettingsRepository(context, authRepository, apiClient, appScope)
  val billDao: BillDao = db.billDao()

  // BT repos declared after authRepository (dependency) and before init block (usage)
  val bluetoothPrinterManager = BluetoothPrinterManager(context, appScope)
  val bluetoothPrinterConfigRepository = BluetoothPrinterConfigRepository(context, authRepository, apiClient)

  private val healthMonitor = BackendHealthMonitor(apiClient, networkStatusRepository, appScope)

  init {
    // 30 s interval — keeps the offline banner accurate during normal usage
    healthMonitor.start(intervalMs = 30_000)

    appScope.launch {
      authRepository.userSession.collect { session ->
        if (session != null) {
          appScope.launch(Dispatchers.IO) {
            runCatching {
              inventoryRepository.syncFromRemote()
              staffRepository.syncFromRemote()
              customerRepository.syncFromRemote()
              billingRepository.syncFromRemote()
              cashRepository.syncFromRemote()
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
