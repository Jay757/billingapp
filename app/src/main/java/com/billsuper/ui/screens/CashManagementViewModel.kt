package com.billsuper.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.billsuper.data.CashRepository
import com.billsuper.data.db.CashTransactionEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class CashManagementViewModel(private val repo: CashRepository) : ViewModel() {

  private val _isLoading = MutableStateFlow(true)
  val isLoading = _isLoading.asStateFlow()

  val transactions: StateFlow<List<CashTransactionEntity>> = repo.observeAll()
    .onEach { _isLoading.value = false }
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
      _isLoading.value = true
      try {
        repo.add(
          CashTransactionEntity(
            type = type,
            amount = amount,
            note = note.trim().ifEmpty { null },
            createdAtEpochMs = System.currentTimeMillis()
          )
        )
      } finally {
        _isLoading.value = false
      }
    }
  }

  fun clearAll() {
    viewModelScope.launch {
      _isLoading.value = true
      try { repo.deleteAll() } finally { _isLoading.value = false }
    }
  }
}

class CashManagementViewModelFactory(private val repo: CashRepository) : ViewModelProvider.Factory {
  override fun <T : ViewModel> create(modelClass: Class<T>): T {
    @Suppress("UNCHECKED_CAST")
    return CashManagementViewModel(repo) as T
  }
}

