package com.aslibill.data

import com.aslibill.data.db.BillAnalyticsDao
import com.aslibill.data.db.CreditSummaryRow
import com.aslibill.data.db.DayReportRow
import com.aslibill.data.db.ItemSalesRow
import kotlinx.coroutines.flow.Flow

class AnalyticsRepository(
    private val dao: BillAnalyticsDao,
    private val authRepository: AuthRepository
) {
  fun observeItemSales(from: Long, to: Long): Flow<List<ItemSalesRow>> {
    val uid = authRepository.userSession.value?.id ?: return kotlinx.coroutines.flow.flowOf(emptyList())
    return dao.observeItemSales(uid, from, to)
  }

  fun observeDayReport(from: Long, to: Long): Flow<List<DayReportRow>> {
    val uid = authRepository.userSession.value?.id ?: return kotlinx.coroutines.flow.flowOf(emptyList())
    return dao.observeDayReport(uid, from, to)
  }

  fun observeCreditSummary(): Flow<List<CreditSummaryRow>> {
    val uid = authRepository.userSession.value?.id ?: return kotlinx.coroutines.flow.flowOf(emptyList())
    return dao.observeCreditSummary(uid)
  }
}
