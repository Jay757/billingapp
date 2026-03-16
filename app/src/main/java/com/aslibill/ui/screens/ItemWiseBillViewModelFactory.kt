package com.aslibill.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.aslibill.data.BillingRepository
import com.aslibill.data.InventoryRepository

class ItemWiseBillViewModelFactory(
  private val inventory: InventoryRepository,
  private val billing: BillingRepository
) : ViewModelProvider.Factory {
  @Suppress("UNCHECKED_CAST")
  override fun <T : ViewModel> create(modelClass: Class<T>): T {
    if (modelClass.isAssignableFrom(ItemWiseBillViewModel::class.java)) {
      return ItemWiseBillViewModel(inventory, billing) as T
    }
    throw IllegalArgumentException("Unknown ViewModel class: $modelClass")
  }
}

