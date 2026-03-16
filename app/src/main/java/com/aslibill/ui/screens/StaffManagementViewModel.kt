package com.aslibill.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.aslibill.data.StaffRepository
import com.aslibill.data.db.StaffEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class StaffManagementViewModel(private val repo: StaffRepository) : ViewModel() {
  val staffList: StateFlow<List<StaffEntity>> = repo.observeAll()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  fun addStaff(name: String, role: String, mobile: String) {
    viewModelScope.launch {
      repo.add(StaffEntity(name = name.trim(), role = role.trim(), mobile = mobile.trim()))
    }
  }

  fun updateStaff(entity: StaffEntity, name: String, role: String, mobile: String) {
    viewModelScope.launch {
      repo.update(entity.copy(name = name.trim(), role = role.trim(), mobile = mobile.trim()))
    }
  }

  fun toggleActive(entity: StaffEntity) {
    viewModelScope.launch { repo.update(entity.copy(isActive = !entity.isActive)) }
  }

  fun deleteStaff(entity: StaffEntity) {
    viewModelScope.launch { repo.delete(entity) }
  }
}

class StaffManagementViewModelFactory(private val repo: StaffRepository) : ViewModelProvider.Factory {
  override fun <T : ViewModel> create(modelClass: Class<T>): T {
    @Suppress("UNCHECKED_CAST")
    return StaffManagementViewModel(repo) as T
  }
}
