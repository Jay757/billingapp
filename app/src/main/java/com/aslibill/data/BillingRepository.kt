package com.aslibill.data

import com.aslibill.BuildConfig
import com.aslibill.data.db.BillDao
import com.aslibill.data.db.BillItemEntity
import com.aslibill.data.db.BillWithItemsRow
import com.aslibill.data.db.BillEntity
import com.aslibill.network.ApiHttpClient
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
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

  // Reactive: re-subscribes automatically when session becomes available after cold start
  @OptIn(ExperimentalCoroutinesApi::class)
  fun observeBillsBetween(fromEpochMs: Long, toEpochMs: Long): Flow<List<BillWithItemsRow>> {
    return authRepository.userSession.flatMapLatest { session ->
      if (session == null) flowOf(emptyList())
      else billDao.observeBillsBetween(session.id, fromEpochMs, toEpochMs)
    }
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
      // ?confirm=true required by backend to prevent accidental full wipe
      client.delete("/bills?confirm=true", token)
    }
  }

  suspend fun syncFromRemote() {
    val uid = getUserId()
    val token = authRepository.currentToken() ?: return
    
    runCatching {
      val now = System.currentTimeMillis()
      val thirtyDaysAgo = now - (30L * 24 * 60 * 60 * 1000)
      
      // Single request — items embedded inline, eliminating N+1 round trips
      val url = "/bills?fromEpochMs=$thirtyDaysAgo&toEpochMs=$now"
      val billsResp = client.getJsonArray(url, token)
      
      for (i in 0 until billsResp.length()) {
        val bObj = billsResp.getJSONObject(i)
        val billId = bObj.getLong("billId")
        
        val billEntity = BillEntity(
          id = billId,
          userId = uid,
          createdAtEpochMs = bObj.getLong("createdAtEpochMs"),
          cashierName = bObj.optString("cashierName").takeIf { !bObj.isNull("cashierName") },
          customerId = bObj.optLong("customerId", -1L).takeIf { it != -1L },
          subtotal = bObj.getDouble("subtotal"),
          tax = bObj.getDouble("tax"),
          total = bObj.getDouble("total"),
          paymentMethod = bObj.getString("paymentMethod")
        )

        // Items are now embedded in the bill response — no per-bill HTTP request
        val itemsArray = bObj.optJSONArray("items") ?: JSONArray()
        val itemsList = mutableListOf<BillItemEntity>()
        for (j in 0 until itemsArray.length()) {
          val itObj = itemsArray.getJSONObject(j)
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
        
        // REPLACE strategy in DAO handles re-syncs without constraint crashes
        billDao.insertBillWithItems(billEntity, itemsList)
      }
    }
  }
}

