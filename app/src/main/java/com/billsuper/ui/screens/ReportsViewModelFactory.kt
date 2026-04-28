package com.billsuper.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.billsuper.data.BillingRepository

class ReportsViewModelFactory(
  private val billing: BillingRepository
) : ViewModelProvider.Factory {
  @Suppress("UNCHECKED_CAST")
  override fun <T : ViewModel> create(modelClass: Class<T>): T {
    if (modelClass.isAssignableFrom(ReportsViewModel::class.java)) {
      return ReportsViewModel(billing) as T
    }
    throw IllegalArgumentException("Unknown ViewModel class: $modelClass")
  }
}


