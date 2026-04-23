package com.aslibill.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.aslibill.data.CustomerRepository
import com.aslibill.data.db.CustomerEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class CustomerManagementViewModel(private val repo: CustomerRepository) : ViewModel() {
  private val _isLoading = MutableStateFlow(true)
  val isLoading = _isLoading.asStateFlow()

  val customers: StateFlow<List<CustomerEntity>> = repo.observeAll()
    .onEach { _isLoading.value = false }
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  fun addCustomer(name: String, mobile: String, address: String) {
    viewModelScope.launch {
      _isLoading.value = true
      try {
        repo.add(
          CustomerEntity(
            name = name.trim(),
            mobile = mobile.trim(),
            address = address.trim().ifEmpty { null }
          )
        )
      } finally {
        _isLoading.value = false
      }
    }
  }

  fun updateCustomer(entity: CustomerEntity, name: String, mobile: String, address: String) {
    viewModelScope.launch {
      _isLoading.value = true
      try {
        repo.update(
          entity.copy(
            name = name.trim(),
            mobile = mobile.trim(),
            address = address.trim().ifEmpty { null }
          )
        )
      } finally {
        _isLoading.value = false
      }
    }
  }

  fun deleteCustomer(entity: CustomerEntity) {
    viewModelScope.launch {
      _isLoading.value = true
      try {
        repo.delete(entity)
      } finally {
        _isLoading.value = false
      }
    }
  }
}

class CustomerManagementViewModelFactory(private val repo: CustomerRepository) : ViewModelProvider.Factory {
  override fun <T : ViewModel> create(modelClass: Class<T>): T {
    @Suppress("UNCHECKED_CAST")
    return CustomerManagementViewModel(repo) as T
  }
}
