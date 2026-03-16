package com.aslibill.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.aslibill.data.CustomerRepository
import com.aslibill.data.db.CustomerEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CustomerManagementViewModel(private val repo: CustomerRepository) : ViewModel() {
  val customers: StateFlow<List<CustomerEntity>> = repo.observeAll()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  fun addCustomer(name: String, mobile: String, address: String) {
    viewModelScope.launch {
      repo.add(
        CustomerEntity(
          name = name.trim(),
          mobile = mobile.trim(),
          address = address.trim().ifEmpty { null }
        )
      )
    }
  }

  fun updateCustomer(entity: CustomerEntity, name: String, mobile: String, address: String) {
    viewModelScope.launch {
      repo.update(
        entity.copy(
          name = name.trim(),
          mobile = mobile.trim(),
          address = address.trim().ifEmpty { null }
        )
      )
    }
  }

  fun deleteCustomer(entity: CustomerEntity) {
    viewModelScope.launch { repo.delete(entity) }
  }
}

class CustomerManagementViewModelFactory(private val repo: CustomerRepository) : ViewModelProvider.Factory {
  override fun <T : ViewModel> create(modelClass: Class<T>): T {
    @Suppress("UNCHECKED_CAST")
    return CustomerManagementViewModel(repo) as T
  }
}
