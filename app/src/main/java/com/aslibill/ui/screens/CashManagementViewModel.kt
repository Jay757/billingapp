package com.aslibill.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.aslibill.data.CashRepository
import com.aslibill.data.db.CashTransactionEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CashManagementViewModel(private val repo: CashRepository) : ViewModel() {

  val transactions: StateFlow<List<CashTransactionEntity>> = repo.observeAll()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val balance: StateFlow<Double> = repo.observeAll().map { list ->
    list.fold(0.0) { acc, tx ->
      when (tx.type) {
        "OPEN", "IN" -> acc + tx.amount
        "OUT" -> acc - tx.amount
        else -> acc
      }
    }
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

  fun addTransaction(type: String, amount: Double, note: String) {
    viewModelScope.launch {
      repo.add(
        CashTransactionEntity(
          type = type,
          amount = amount,
          note = note.trim().ifEmpty { null },
          createdAtEpochMs = System.currentTimeMillis()
        )
      )
    }
  }

  fun clearAll() {
    viewModelScope.launch { repo.deleteAll() }
  }
}

class CashManagementViewModelFactory(private val repo: CashRepository) : ViewModelProvider.Factory {
  override fun <T : ViewModel> create(modelClass: Class<T>): T {
    @Suppress("UNCHECKED_CAST")
    return CashManagementViewModel(repo) as T
  }
}
