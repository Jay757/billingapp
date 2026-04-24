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
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.flow

private fun defaultMonthRange(): DateRangeFilter {
  val cal = Calendar.getInstance()
  cal.set(Calendar.DAY_OF_MONTH, 1)
  return DateRangeFilter(fromEpochMs = cal.timeInMillis, toEpochMs = System.currentTimeMillis())
}



@OptIn(ExperimentalCoroutinesApi::class)
class DayReportViewModel(private val repo: AnalyticsRepository) : ViewModel() {
  private val _isLoading = MutableStateFlow(true)
  val isLoading = _isLoading.asStateFlow()

  val filters = MutableStateFlow(defaultMonthRange())

  val rows: StateFlow<List<DayReportRow>> = filters
    .onEach { _isLoading.value = true }
    .flatMapLatest { f ->
      flow { emit(repo.fetchDayReport(f.fromEpochMs, f.toEpochMs, f.range)) }
    }.onEach { _isLoading.value = false }
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  fun setFrom(epochMs: Long) {
    filters.value = filters.value.copy(fromEpochMs = epochMs, range = null)
  }

  fun setTo(epochMs: Long) {
    filters.value = filters.value.copy(toEpochMs = epochMs, range = null)
  }
}

class DayReportViewModelFactory(private val repo: AnalyticsRepository) : ViewModelProvider.Factory {
  override fun <T : ViewModel> create(modelClass: Class<T>): T {
    @Suppress("UNCHECKED_CAST")
    return DayReportViewModel(repo) as T
  }
}
