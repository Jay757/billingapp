package com.aslibill.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aslibill.data.BillingRepository
import com.aslibill.data.db.BillItemEntity
import com.aslibill.data.db.BillWithItemsRow
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.util.Calendar
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

data class ReportFilters(
  val fromEpochMs: Long,
  val toEpochMs: Long,
  val range: String? = null
)

class ReportsViewModel(
  private val billing: BillingRepository
) : ViewModel() {

  private val _isLoading = MutableStateFlow(true)
  val isLoading = _isLoading.asStateFlow()

  private val _filters = MutableStateFlow(defaultMonthRange())
  val filters: StateFlow<ReportFilters> = _filters

  @OptIn(ExperimentalCoroutinesApi::class)
  val bills: StateFlow<List<BillWithItemsRow>> =
    _filters
      .onEach { _isLoading.value = true }
      .flatMapLatest { billing.observeBillsBetween(it.fromEpochMs, it.toEpochMs, it.range) }
      .onEach { _isLoading.value = false }
      .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

  val totalAmount: StateFlow<Double> =
    bills.map { it.sumOf { row -> row.total } }
      .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0.0)

  fun setFrom(epochMs: Long) {
    _filters.value = _filters.value.copy(fromEpochMs = epochMs)
  }

  fun setTo(epochMs: Long) {
    _filters.value = _filters.value.copy(toEpochMs = epochMs)
  }

  fun deleteBill(billId: Long) = viewModelScope.launch {
    _isLoading.value = true
    try { billing.deleteBill(billId) } finally { _isLoading.value = false }
  }

  fun deleteAll() = viewModelScope.launch {
    _isLoading.value = true
    try { billing.deleteAllBills() } finally { _isLoading.value = false }
  }

  fun loadItems(billId: Long, onLoaded: (List<BillItemEntity>) -> Unit, onError: (Throwable) -> Unit) {
    viewModelScope.launch {
      _isLoading.value = true
      try {
        onLoaded(billing.getBillItems(billId))
      } catch (t: Throwable) {
        onError(t)
      } finally {
        _isLoading.value = false
      }
    }
  }
}

private fun defaultMonthRange(): ReportFilters {
  val cal = Calendar.getInstance()
  cal.set(Calendar.DAY_OF_MONTH, 1)
  val start = cal.timeInMillis
  val end = System.currentTimeMillis()
  return ReportFilters(fromEpochMs = start, toEpochMs = end)
}

