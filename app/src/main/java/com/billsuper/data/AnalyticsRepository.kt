package com.billsuper.data

import com.billsuper.data.db.CreditSummaryRow
import com.billsuper.data.db.DayReportRow
import com.billsuper.data.db.ItemSalesRow
import com.billsuper.data.db.SalesSummaryRow
import com.billsuper.network.ApiHttpClient
import org.json.JSONArray
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AnalyticsRepository(
    private val authRepository: AuthRepository,
    private val client: ApiHttpClient
) {
  suspend fun fetchItemSales(from: Long? = null, to: Long? = null, range: String? = null): List<ItemSalesRow> {
    val token = authRepository.currentToken() ?: return emptyList()
    return try {
      val url = when {
        range != null -> "/analytics/item-sales?range=$range"
        else -> {
          val df = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
          val f = if (from != null) df.format(Date(from)) else ""
          val t = if (to != null) df.format(Date(to)) else ""
          "/analytics/item-sales?fromDate=$f&toDate=$t"
        }
      }
      val resp = client.getJsonArray(url, token)
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

  suspend fun fetchDayReport(from: Long? = null, to: Long? = null, range: String? = null): List<DayReportRow> {
    val token = authRepository.currentToken() ?: return emptyList()
    return try {
      val url = when {
        range != null -> "/analytics/day-report?range=$range"
        else -> {
          val df = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
          val f = if (from != null) df.format(Date(from)) else ""
          val t = if (to != null) df.format(Date(to)) else ""
          "/analytics/day-report?fromDate=$f&toDate=$t"
        }
      }
      val resp = client.getJsonArray(url, token)
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

  suspend fun fetchSalesSummary(from: Long? = null, to: Long? = null, range: String? = null): SalesSummaryRow? {
    val token = authRepository.currentToken() ?: return null
    return try {
      val url = when {
        range != null -> "/analytics/sales-summary?range=$range"
        else -> {
          val df = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
          val f = if (from != null) df.format(Date(from)) else ""
          val t = if (to != null) df.format(Date(to)) else ""
          "/analytics/sales-summary?fromDate=$f&toDate=$t"
        }
      }
      val obj = client.getJson(url, token)
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


