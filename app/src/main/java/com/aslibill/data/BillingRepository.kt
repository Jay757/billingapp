package com.aslibill.data

import com.aslibill.data.db.BillEntity
import com.aslibill.data.db.BillItemEntity
import com.aslibill.data.db.BillWithItemsRow
import com.aslibill.network.ApiHttpClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject

class BillingRepository(
  private val authRepository: AuthRepository,
  private val client: ApiHttpClient
) {

  private val _bills = MutableStateFlow<List<BillWithItemsRow>>(emptyList())
  val bills: StateFlow<List<BillWithItemsRow>> = _bills.asStateFlow()

  // Keep a local cache of items for currently loaded bills (in-memory only)
  private val billItemsMap = mutableMapOf<Long, List<BillItemEntity>>()

  private suspend fun getUserId(): Int = authRepository.userSession.value?.id ?: throw IllegalStateException("Not logged in")

  suspend fun saveBill(
    bill: BillEntity,
    items: List<BillItemEntity>
  ): Long {
    val token = authRepository.currentToken() ?: return 0
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
    val resp = client.postJson("/bills", token, body)
    refresh()
    return resp.optLong("id", 0)
  }

  fun observeBillsBetween(fromEpochMs: Long, toEpochMs: Long): StateFlow<List<BillWithItemsRow>> {
    // Note: In a real app we might filter the existing _bills or trigger a refresh with specific dates.
    // For now, we return the common bills flow.
    return bills
  }

  suspend fun getBillItems(billId: Long): List<BillItemEntity> {
    return billItemsMap[billId] ?: emptyList()
  }

  suspend fun deleteBill(billId: Long) {
    val token = authRepository.currentToken() ?: return
    client.delete("/bills/$billId", token)
    refresh()
  }

  suspend fun deleteAllBills() {
    val token = authRepository.currentToken() ?: return
    client.delete("/bills?confirm=true", token)
    refresh()
  }

  suspend fun refresh() {
    val uid = getUserId()
    val token = authRepository.currentToken() ?: return
    
    runCatching {
      val now = System.currentTimeMillis()
      val thirtyDaysAgo = now - (30L * 24 * 60 * 60 * 1000)
      
      val url = "/bills?fromEpochMs=$thirtyDaysAgo&toEpochMs=$now"
      val billsResp = client.getJsonArray(url, token)
      
      val newList = mutableListOf<BillWithItemsRow>()
      billItemsMap.clear()

      for (i in 0 until billsResp.length()) {
        val bObj = billsResp.getJSONObject(i)
        val billId = bObj.getLong("billId")
        
        // Items are now embedded in the bill response
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
        billItemsMap[billId] = itemsList

        newList.add(BillWithItemsRow(
          billId = billId,
          createdAtEpochMs = bObj.getLong("createdAtEpochMs"),
          cashierName = bObj.optString("cashierName").takeIf { !bObj.isNull("cashierName") },
          subtotal = bObj.getDouble("subtotal"),
          tax = bObj.getDouble("tax"),
          total = bObj.getDouble("total"),
          paymentMethod = bObj.getString("paymentMethod"),
          itemCount = itemsList.size
        ))
      }
      _bills.value = newList.sortedByDescending { it.createdAtEpochMs }
    }
  }

  suspend fun syncFromRemote() = refresh()
}


