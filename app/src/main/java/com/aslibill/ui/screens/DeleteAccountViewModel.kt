package com.aslibill.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DeleteAccountViewModel : ViewModel() {
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
            // Simulate network delay for processing deletion request
            kotlinx.coroutines.delay(2000)
            
            println("Account Deletion Requested. Reason: ${_reason.value}")
            
            _isDeleting.value = false
            _deletionRequested.value = true
            _reason.value = ""
        }
    }
}
