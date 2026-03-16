package com.aslibill.data

import com.aslibill.data.db.BillAnalyticsDao
import com.aslibill.data.db.CreditSummaryRow
import com.aslibill.data.db.DayReportRow
import com.aslibill.data.db.ItemSalesRow
import kotlinx.coroutines.flow.Flow

class AnalyticsRepository(private val dao: BillAnalyticsDao) {
  fun observeItemSales(from: Long, to: Long): Flow<List<ItemSalesRow>> =
    dao.observeItemSales(from, to)

  fun observeDayReport(from: Long, to: Long): Flow<List<DayReportRow>> =
    dao.observeDayReport(from, to)

  fun observeCreditSummary(): Flow<List<CreditSummaryRow>> =
    dao.observeCreditSummary()
}
