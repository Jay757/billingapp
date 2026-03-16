package com.aslibill.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.aslibill.data.BillingRepository

class QuickBillViewModelFactory(
  private val billing: BillingRepository
) : ViewModelProvider.Factory {
  @Suppress("UNCHECKED_CAST")
  override fun <T : ViewModel> create(modelClass: Class<T>): T {
    if (modelClass.isAssignableFrom(QuickBillViewModel::class.java)) {
      return QuickBillViewModel(billing) as T
    }
    throw IllegalArgumentException("Unknown ViewModel class: $modelClass")
  }
}

