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

import com.aslibill.data.db.ProductDao

class BillingRepository(
  private val billDao: BillDao,
  private val productDao: ProductDao,
  private val authRepository: AuthRepository,
  private val client: ApiHttpClient
) {

  private suspend fun getUserId(): Int = authRepository.userSession.value?.id ?: throw IllegalStateException("Not logged in")

  suspend fun saveBill(
    bill: BillEntity,
    items: List<BillItemEntity>
  ): Long {
    val uid = getUserId()
    val id = billDao.insertBillWithItems(
        bill.copy(userId = uid),
        items.map { it.copy(userId = uid) }
    )
    
    // Decrement local stock for each product in the bill
    items.forEach { item ->
      item.productId?.let { pid ->
        productDao.decrementStock(uid, pid, item.qty)
      }
    }

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

  fun observeBillsBetween(fromEpochMs: Long, toEpochMs: Long): Flow<List<BillWithItemsRow>> {
    val session = authRepository.userSession.value ?: return kotlinx.coroutines.flow.flowOf(emptyList())
    return billDao.observeBillsBetween(session.id, fromEpochMs, toEpochMs)
  }

  suspend fun getBillItems(billId: Long): List<BillItemEntity> {
    val uid = getUserId()
    return billDao.getBillItems(uid, billId)
  }

  suspend fun deleteBill(billId: Long) {
    val uid = getUserId()
    billDao.deleteBillById(uid, billId)
    runCatching {
      val token = authRepository.currentToken() ?: return@runCatching
      client.delete("/bills/$billId", token)
    }
  }

  suspend fun deleteAllBills() {
    val uid = getUserId()
    billDao.deleteAllBills(uid)
    runCatching {
      val token = authRepository.currentToken() ?: return@runCatching
      client.delete("/bills", token)
    }
  }

  suspend fun syncFromRemote() {
    val uid = getUserId()
    val token = authRepository.currentToken() ?: return
    
    runCatching {
      val now = System.currentTimeMillis()
      val thirtyDaysAgo = now - (30L * 24 * 60 * 60 * 1000)
      
      val url = "/bills?fromEpochMs=$thirtyDaysAgo&toEpochMs=$now"
      val billsResp = client.getJsonArray(url, token)
      
      for (i in 0 until billsResp.length()) {
        val bObj = billsResp.getJSONObject(i)
        val billId = bObj.getLong("billId")
        
        // Fetch items for this bill
        val itemsResp = client.getJsonArray("/bills/$billId/items", token)
        val itemsList = mutableListOf<BillItemEntity>()
        for (j in 0 until itemsResp.length()) {
          val itObj = itemsResp.getJSONObject(j)
          itemsList.add(BillItemEntity(
            id = itObj.getLong("id"),
            userId = uid,
            billId = billId,
            productId = itObj.optLong("productId", -1L).takeIf { it != -1L },
            productNameSnapshot = itObj.getString("productNameSnapshot"),
            qty = itObj.getDouble("qty"),
            rate = itObj.getDouble("rate"),
            lineTotal = itObj.getDouble("lineTotal")
          ))
        }
        
        // Insert bill and items
        val billEntity = BillEntity(
          id = billId,
          userId = uid,
          createdAtEpochMs = bObj.getLong("createdAtEpochMs"),
          cashierName = bObj.optString("cashierName").takeIf { !bObj.isNull("cashierName") },
          subtotal = bObj.getDouble("subtotal"),
          tax = bObj.getDouble("tax"),
          total = bObj.getDouble("total"),
          paymentMethod = bObj.getString("paymentMethod")
        )
        billDao.insertBillWithItems(billEntity, itemsList)
      }
    }
  }
}

