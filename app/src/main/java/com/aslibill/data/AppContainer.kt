package com.aslibill.data

import android.content.Context
import com.aslibill.data.db.AppDatabase

class AppContainer(context: Context) {
  private val db = AppDatabase.get(context)
  val inventoryRepository = InventoryRepository(db.categoryDao(), db.productDao())
  val billingRepository = BillingRepository(db.billDao())
  val customerRepository = CustomerRepository(db.customerDao())
  val staffRepository = StaffRepository(db.staffDao())
  val cashRepository = CashRepository(db.cashDao())
  val analyticsRepository = AnalyticsRepository(db.billAnalyticsDao())
  val authRepository = AuthRepository(context)
  val settingsRepository = SettingsRepository(context)
}
