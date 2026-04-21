package com.aslibill.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.aslibill.data.AnalyticsRepository
import com.aslibill.data.db.ItemSalesRow
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import java.util.Calendar

data class DateRangeFilter(val fromEpochMs: Long, val toEpochMs: Long)

private fun todayRange(): DateRangeFilter {
  val cal = Calendar.getInstance()
  cal.set(Calendar.HOUR_OF_DAY, 0); cal.set(Calendar.MINUTE, 0); cal.set(Calendar.SECOND, 0); cal.set(Calendar.MILLISECOND, 0)
  val from = cal.timeInMillis
  cal.set(Calendar.HOUR_OF_DAY, 23); cal.set(Calendar.MINUTE, 59); cal.set(Calendar.SECOND, 59)
  return DateRangeFilter(from, cal.timeInMillis)
}

@OptIn(ExperimentalCoroutinesApi::class)
class ItemWiseSalesReportViewModel(private val repo: AnalyticsRepository) : ViewModel() {
  val filters = MutableStateFlow(todayRange())

  val items: StateFlow<List<ItemSalesRow>> = filters.flatMapLatest { f ->
    repo.observeItemSales(f.fromEpochMs, f.toEpochMs)
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

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

class ItemWiseSalesReportViewModelFactory(private val repo: AnalyticsRepository) : ViewModelProvider.Factory {
  override fun <T : ViewModel> create(modelClass: Class<T>): T {
    @Suppress("UNCHECKED_CAST")
    return ItemWiseSalesReportViewModel(repo) as T
  }
}
