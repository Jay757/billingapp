package com.aslibill.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FeedbackViewModel : ViewModel() {
    private val _message = MutableStateFlow("")
    val message: StateFlow<String> = _message.asStateFlow()

    private val _contactInfo = MutableStateFlow("")
    val contactInfo: StateFlow<String> = _contactInfo.asStateFlow()

    private val _isSubmitting = MutableStateFlow(false)
    val isSubmitting: StateFlow<Boolean> = _isSubmitting.asStateFlow()

    private val _submissionSuccess = MutableStateFlow(false)
    val submissionSuccess: StateFlow<Boolean> = _submissionSuccess.asStateFlow()

    fun onMessageChange(newMessage: String) {
        _message.value = newMessage
    }

    fun onContactInfoChange(newInfo: String) {
        _contactInfo.value = newInfo
    }

    fun submitFeedback() {
        if (_message.value.isBlank()) return

        viewModelScope.launch {
            _isSubmitting.value = true
            // Simulate network/database delay
            kotlinx.coroutines.delay(1500)
            
            // In a real app, we would send this to a server or save to local DB
            println("Feedback Submitted: ${_message.value}, Contact: ${_contactInfo.value}")
            
            _isSubmitting.value = false
            _submissionSuccess.value = true
            _message.value = ""
            _contactInfo.value = ""
        }
    }

    fun resetSuccess() {
        _submissionSuccess.value = false
    }
}
