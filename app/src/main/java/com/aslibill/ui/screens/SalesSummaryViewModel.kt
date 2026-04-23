package com.aslibill.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.aslibill.data.AnalyticsRepository
import com.aslibill.data.db.DayReportRow
import com.aslibill.data.db.ItemSalesRow
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.util.Calendar
import kotlinx.coroutines.flow.flow

private fun thisMonthRange(): DateRangeFilter {
  val cal = Calendar.getInstance()
  cal.set(Calendar.DAY_OF_MONTH, 1)
  cal.set(Calendar.HOUR_OF_DAY, 0); cal.set(Calendar.MINUTE, 0); cal.set(Calendar.SECOND, 0)
  val from = cal.timeInMillis
  cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH))
  cal.set(Calendar.HOUR_OF_DAY, 23); cal.set(Calendar.MINUTE, 59); cal.set(Calendar.SECOND, 59)
  return DateRangeFilter(from, cal.timeInMillis)
}

data class SalesSummaryState(
  val totalBills: Int = 0,
  val totalRevenue: Double = 0.0,
  val avgBillValue: Double = 0.0,
  val cashTotal: Double = 0.0,
  val onlineTotal: Double = 0.0,
  val topItems: List<ItemSalesRow> = emptyList()
)



@OptIn(ExperimentalCoroutinesApi::class)
class SalesSummaryViewModel(private val repo: AnalyticsRepository) : ViewModel() {
  val filters = MutableStateFlow(thisMonthRange())

  private val dayRows: StateFlow<List<DayReportRow>> = filters.flatMapLatest { f ->
    flow { emit(repo.fetchDayReport(f.fromEpochMs, f.toEpochMs)) }
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  private val itemRows: StateFlow<List<ItemSalesRow>> = filters.flatMapLatest { f ->
    flow { emit(repo.fetchItemSales(f.fromEpochMs, f.toEpochMs)) }
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val summary: StateFlow<SalesSummaryState> = dayRows.map { days ->
    val bills = days.sumOf { it.billCount }
    val revenue = days.sumOf { it.grandTotal }
    SalesSummaryState(
      totalBills = bills,
      totalRevenue = revenue,
      avgBillValue = if (bills > 0) revenue / bills else 0.0,
      cashTotal = days.sumOf { it.cashTotal },
      onlineTotal = days.sumOf { it.onlineTotal },
      topItems = itemRows.value.take(5)
    )
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SalesSummaryState())

  fun setFrom(epochMs: Long) {
    val cal = Calendar.getInstance().apply { timeInMillis = epochMs }
    cal.set(Calendar.HOUR_OF_DAY, 0); cal.set(Calendar.MINUTE, 0); cal.set(Calendar.SECOND, 0); cal.set(Calendar.MILLISECOND, 0)
    filters.value = filters.value.copy(fromEpochMs = cal.timeInMillis)
  }

  fun setTo(epochMs: Long) {
    val cal = Calendar.getInstance().apply { timeInMillis = epochMs }
    cal.set(Calendar.HOUR_OF_DAY, 23); cal.set(Calendar.MINUTE, 59); cal.set(Calendar.SECOND, 59); cal.set(Calendar.MILLISECOND, 999)
    filters.value = filters.value.copy(toEpochMs = cal.timeInMillis)
  }
}

class SalesSummaryViewModelFactory(private val repo: AnalyticsRepository) : ViewModelProvider.Factory {
  override fun <T : ViewModel> create(modelClass: Class<T>): T {
    @Suppress("UNCHECKED_CAST")
    return SalesSummaryViewModel(repo) as T
  }
}
