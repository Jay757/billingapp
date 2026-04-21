package com.aslibill.data

import android.content.Context
import com.aslibill.bluetooth.BluetoothPrinterManager
import com.aslibill.data.db.AppDatabase
import com.aslibill.data.db.BillDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class AppContainer(context: Context) {
  private val appScope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

  private val db = AppDatabase.get(context)
  val authRepository = AuthRepository(context)
  val inventoryRepository = InventoryRepository(db.categoryDao(), db.productDao(), authRepository)
  val billingRepository = BillingRepository(db.billDao(), db.productDao(), authRepository)
  val customerRepository = CustomerRepository(db.customerDao(), authRepository)
  val staffRepository = StaffRepository(db.staffDao(), authRepository)
  val cashRepository = CashRepository(db.cashDao(), authRepository)
  val analyticsRepository = AnalyticsRepository(db.billAnalyticsDao(), authRepository)
  val settingsRepository = SettingsRepository(context, authRepository)
  val billDao: BillDao = db.billDao()

  init {
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
    // 1. Database is kept locally as requested by user
    // db.clearPersonalData() 
    
    // 2. Clear all preferences (auth, print, printer)
    authRepository.logout()

    // 3. Disconnect hardware
    bluetoothPrinterManager.disconnect()
  }

  // Keep Bluetooth manager alive across navigation so an active printer connection persists.
  val bluetoothPrinterManager = BluetoothPrinterManager(context, appScope)

  val bluetoothPrinterConfigRepository = BluetoothPrinterConfigRepository(context, authRepository)
}
