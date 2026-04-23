package com.aslibill.data

import com.aslibill.data.db.CreditSummaryRow
import com.aslibill.data.db.DayReportRow
import com.aslibill.data.db.ItemSalesRow
import com.aslibill.data.db.SalesSummaryRow
import com.aslibill.network.ApiHttpClient
import org.json.JSONArray

class AnalyticsRepository(
    private val authRepository: AuthRepository,
    private val client: ApiHttpClient
) {
  suspend fun fetchItemSales(from: Long, to: Long): List<ItemSalesRow> {
    val token = authRepository.currentToken() ?: return emptyList()
    return try {
      val resp = client.getJsonArray("/analytics/item-sales?fromEpochMs=$from&toEpochMs=$to", token)
      val list = mutableListOf<ItemSalesRow>()
      for (i in 0 until resp.length()) {
        val obj = resp.getJSONObject(i)
        list.add(ItemSalesRow(
          productName = obj.getString("productName"),
          totalQty = obj.getDouble("totalQty"),
          totalRevenue = obj.getDouble("totalRevenue")
        ))
      }
      list
    } catch (_: Throwable) {
      emptyList()
    }
  }

  suspend fun fetchDayReport(from: Long, to: Long): List<DayReportRow> {
    val token = authRepository.currentToken() ?: return emptyList()
    return try {
      val resp = client.getJsonArray("/analytics/day-report?fromEpochMs=$from&toEpochMs=$to", token)
      val list = mutableListOf<DayReportRow>()
      for (i in 0 until resp.length()) {
        val obj = resp.getJSONObject(i)
        list.add(DayReportRow(
          dateLabel = obj.getString("dateLabel"),
          billCount = obj.getInt("billCount"),
          cashTotal = obj.getDouble("cashTotal"),
          onlineTotal = obj.getDouble("onlineTotal"),
          grandTotal = obj.getDouble("grandTotal")
        ))
      }
      list
    } catch (_: Throwable) {
      emptyList()
    }
  }

  suspend fun fetchCreditSummary(): List<CreditSummaryRow> {
    val token = authRepository.currentToken() ?: return emptyList()
    return try {
      val resp = client.getJsonArray("/analytics/credit-summary", token)
      val list = mutableListOf<CreditSummaryRow>()
      for (i in 0 until resp.length()) {
        val obj = resp.getJSONObject(i)
        list.add(CreditSummaryRow(
          customerId = obj.optLong("customerId", -1L).takeIf { it != -1L },
          customerName = obj.getString("customerName"),
          customerMobile = obj.getString("customerMobile"),
          totalCredit = obj.getDouble("totalCredit"),
          billCount = obj.getInt("billCount")
        ))
      }
      list
    } catch (_: Throwable) {
      emptyList()
    }
  }

  suspend fun fetchSalesSummary(from: Long, to: Long): SalesSummaryRow? {
    val token = authRepository.currentToken() ?: return null
    return try {
      val obj = client.getJson("/analytics/sales-summary?fromEpochMs=$from&toEpochMs=$to", token)
      val itemsArr = obj.optJSONArray("topItems") ?: JSONArray()
      val topItems = mutableListOf<ItemSalesRow>()
      for (i in 0 until itemsArr.length()) {
        val io = itemsArr.getJSONObject(i)
        topItems.add(ItemSalesRow(
          productName = io.getString("productName"),
          totalQty = io.getDouble("totalQty"),
          totalRevenue = io.getDouble("totalRevenue")
        ))
      }
      SalesSummaryRow(
        totalBills = obj.getInt("totalBills"),
        totalRevenue = obj.getDouble("totalRevenue"),
        cashTotal = obj.getDouble("cashTotal"),
        onlineTotal = obj.getDouble("onlineTotal"),
        topItems = topItems
      )
    } catch (_: Throwable) {
      null
    }
  }
}

