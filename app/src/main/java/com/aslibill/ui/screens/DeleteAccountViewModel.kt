package com.aslibill.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.aslibill.data.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DeleteAccountViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _reason = MutableStateFlow("")
    val reason: StateFlow<String> = _reason.asStateFlow()

    private val _isDeleting = MutableStateFlow(false)
    val isDeleting: StateFlow<Boolean> = _isDeleting.asStateFlow()

    private val _deletionRequested = MutableStateFlow(false)
    val deletionRequested: StateFlow<Boolean> = _deletionRequested.asStateFlow()

    fun onReasonChange(newReason: String) {
        _reason.value = newReason
    }

    fun requestDeletion() {
        viewModelScope.launch {
            _isDeleting.value = true
            val ok = authRepository.deleteAccount(_reason.value.ifBlank { null })
            _isDeleting.value = false
            _deletionRequested.value = ok
            if (ok) _reason.value = ""
        }
    }
}

class DeleteAccountViewModelFactory(
    private val authRepository: AuthRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DeleteAccountViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DeleteAccountViewModel(authRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
