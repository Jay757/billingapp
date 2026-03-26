package com.aslibill.data

import com.aslibill.BuildConfig
import com.aslibill.data.db.BillDao
import com.aslibill.data.db.BillItemEntity
import com.aslibill.data.db.BillWithItemsRow
import com.aslibill.data.db.BillEntity
import com.aslibill.network.ApiHttpClient
import kotlinx.coroutines.flow.Flow
import org.json.JSONArray
import org.json.JSONObject

class BillingRepository(
  private val billDao: BillDao,
  private val authRepository: AuthRepository
) {
  private val client = ApiHttpClient(BuildConfig.API_BASE_URL)

  suspend fun saveBill(
    bill: BillEntity,
    items: List<BillItemEntity>
  ): Long {
    val id = billDao.insertBillWithItems(bill, items)
    runCatching {
      val token = authRepository.currentToken() ?: return@runCatching
      val body = JSONObject().apply {
        put(
          "bill",
          JSONObject()
            .put("createdAtEpochMs", bill.createdAtEpochMs)
            .put("cashierName", bill.cashierName)
            .put("customerId", bill.customerId)
            .put("subtotal", bill.subtotal)
            .put("tax", bill.tax)
            .put("total", bill.total)
            .put("paymentMethod", bill.paymentMethod)
        )
        put(
          "items",
          JSONArray().apply {
            items.forEach { it ->
              put(
                JSONObject()
                  .put("productId", it.productId)
                  .put("productNameSnapshot", it.productNameSnapshot)
                  .put("qty", it.qty)
                  .put("rate", it.rate)
                  .put("lineTotal", it.lineTotal)
              )
            }
          }
        )
      }
      client.postJson("/bills", token, body)
    }
    return id
  }

  fun observeBillsBetween(fromEpochMs: Long, toEpochMs: Long): Flow<List<BillWithItemsRow>> =
    billDao.observeBillsBetween(fromEpochMs, toEpochMs)

  suspend fun getBillItems(billId: Long): List<BillItemEntity> = billDao.getBillItems(billId)
  suspend fun deleteBill(billId: Long) {
    billDao.deleteBillById(billId)
    runCatching {
      val token = authRepository.currentToken() ?: return@runCatching
      client.delete("/bills/$billId", token)
    }
  }

  suspend fun deleteAllBills() {
    billDao.deleteAllBills()
    runCatching {
      val token = authRepository.currentToken() ?: return@runCatching
      client.delete("/bills", token)
    }
  }
}

