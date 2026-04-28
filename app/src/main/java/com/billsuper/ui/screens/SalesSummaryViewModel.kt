package com.billsuper.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.billsuper.data.AnalyticsRepository
import com.billsuper.data.db.DayReportRow
import com.billsuper.data.db.ItemSalesRow
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.util.Calendar
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.flow

private fun defaultMonthRange(): DateRangeFilter {
  val cal = Calendar.getInstance()
  cal.set(Calendar.DAY_OF_MONTH, 1)
  return DateRangeFilter(fromEpochMs = cal.timeInMillis, toEpochMs = System.currentTimeMillis())
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
  private val _isLoading = MutableStateFlow(true)
  val isLoading = _isLoading.asStateFlow()

  val filters = MutableStateFlow(defaultMonthRange())

  val summary: StateFlow<SalesSummaryState> = filters
    .onEach { _isLoading.value = true }
    .flatMapLatest { f ->
      flow {
        val res = repo.fetchSalesSummary(f.fromEpochMs, f.toEpochMs, f.range)
        if (res != null) {
          emit(SalesSummaryState(
            totalBills = res.totalBills,
            totalRevenue = res.totalRevenue,
            avgBillValue = if (res.totalBills > 0) res.totalRevenue / res.totalBills else 0.0,
            cashTotal = res.cashTotal,
            onlineTotal = res.onlineTotal,
            topItems = res.topItems
          ))
        } else {
          emit(SalesSummaryState())
        }
      }
    }.onEach { _isLoading.value = false }
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SalesSummaryState())

  fun setFrom(epochMs: Long) {
    filters.value = filters.value.copy(fromEpochMs = epochMs, range = null)
  }

  fun setTo(epochMs: Long) {
    filters.value = filters.value.copy(toEpochMs = epochMs, range = null)
  }
}

class SalesSummaryViewModelFactory(private val repo: AnalyticsRepository) : ViewModelProvider.Factory {
  override fun <T : ViewModel> create(modelClass: Class<T>): T {
    @Suppress("UNCHECKED_CAST")
    return SalesSummaryViewModel(repo) as T
  }
}

