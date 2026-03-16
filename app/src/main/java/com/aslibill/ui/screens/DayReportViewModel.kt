package com.aslibill.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.aslibill.data.AnalyticsRepository
import com.aslibill.data.db.DayReportRow
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import java.util.Calendar

private fun last7DaysRange(): DateRangeFilter {
  val cal = Calendar.getInstance()
  cal.set(Calendar.HOUR_OF_DAY, 23); cal.set(Calendar.MINUTE, 59); cal.set(Calendar.SECOND, 59)
  val to = cal.timeInMillis
  cal.add(Calendar.DAY_OF_YEAR, -6)
  cal.set(Calendar.HOUR_OF_DAY, 0); cal.set(Calendar.MINUTE, 0); cal.set(Calendar.SECOND, 0)
  return DateRangeFilter(cal.timeInMillis, to)
}

@OptIn(ExperimentalCoroutinesApi::class)
class DayReportViewModel(private val repo: AnalyticsRepository) : ViewModel() {
  val filters = MutableStateFlow(last7DaysRange())

  val rows: StateFlow<List<DayReportRow>> = filters.flatMapLatest { f ->
    repo.observeDayReport(f.fromEpochMs, f.toEpochMs)
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  fun setFrom(epochMs: Long) { filters.value = filters.value.copy(fromEpochMs = epochMs) }
  fun setTo(epochMs: Long)   { filters.value = filters.value.copy(toEpochMs = epochMs) }
}

class DayReportViewModelFactory(private val repo: AnalyticsRepository) : ViewModelProvider.Factory {
  override fun <T : ViewModel> create(modelClass: Class<T>): T {
    @Suppress("UNCHECKED_CAST")
    return DayReportViewModel(repo) as T
  }
}
