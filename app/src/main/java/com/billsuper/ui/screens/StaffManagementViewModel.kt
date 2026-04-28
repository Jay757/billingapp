package com.billsuper.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.billsuper.data.StaffRepository
import com.billsuper.data.db.StaffEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class StaffManagementViewModel(private val repo: StaffRepository) : ViewModel() {
  private val _isLoading = MutableStateFlow(true)
  val isLoading = _isLoading.asStateFlow()

  val staffList: StateFlow<List<StaffEntity>> = repo.observeAll()
    .onEach { _isLoading.value = false }
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  fun addStaff(name: String, role: String, mobile: String) {
    viewModelScope.launch {
      _isLoading.value = true
      try {
        repo.add(StaffEntity(name = name.trim(), role = role.trim(), mobile = mobile.trim()))
      } finally {
        _isLoading.value = false
      }
    }
  }

  fun updateStaff(entity: StaffEntity, name: String, role: String, mobile: String) {
    viewModelScope.launch {
      _isLoading.value = true
      try {
        repo.update(entity.copy(name = name.trim(), role = role.trim(), mobile = mobile.trim()))
      } finally {
        _isLoading.value = false
      }
    }
  }

  fun toggleActive(entity: StaffEntity) {
    viewModelScope.launch {
      _isLoading.value = true
      try {
        repo.update(entity.copy(isActive = !entity.isActive))
      } finally {
        _isLoading.value = false
      }
    }
  }

  fun deleteStaff(entity: StaffEntity) {
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

class StaffManagementViewModelFactory(private val repo: StaffRepository) : ViewModelProvider.Factory {
  override fun <T : ViewModel> create(modelClass: Class<T>): T {
    @Suppress("UNCHECKED_CAST")
    return StaffManagementViewModel(repo) as T
  }
}

