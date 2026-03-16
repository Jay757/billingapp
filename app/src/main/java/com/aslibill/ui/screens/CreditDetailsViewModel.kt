package com.aslibill.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.aslibill.data.AnalyticsRepository
import com.aslibill.data.db.CreditSummaryRow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class CreditDetailsViewModel(private val repo: AnalyticsRepository) : ViewModel() {

  val credits: StateFlow<List<CreditSummaryRow>> = repo.observeCreditSummary()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val totalOutstanding: StateFlow<Double> = repo.observeCreditSummary().map { list ->
    list.sumOf { it.totalCredit }
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)
}

class CreditDetailsViewModelFactory(private val repo: AnalyticsRepository) : ViewModelProvider.Factory {
  override fun <T : ViewModel> create(modelClass: Class<T>): T {
    @Suppress("UNCHECKED_CAST")
    return CreditDetailsViewModel(repo) as T
  }
}
