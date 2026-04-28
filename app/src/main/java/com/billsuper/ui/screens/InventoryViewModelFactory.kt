package com.billsuper.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.billsuper.data.InventoryRepository

class InventoryViewModelFactory(
  private val repo: InventoryRepository
) : ViewModelProvider.Factory {
  @Suppress("UNCHECKED_CAST")
  override fun <T : ViewModel> create(modelClass: Class<T>): T {
    if (modelClass.isAssignableFrom(InventoryViewModel::class.java)) {
      return InventoryViewModel(repo) as T
    }
    throw IllegalArgumentException("Unknown ViewModel class: $modelClass")
  }
}


