package com.aslibill.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.aslibill.data.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FeedbackViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {
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
            val ok = authRepository.submitFeedback(
                message = _message.value,
                contactInfo = _contactInfo.value.ifBlank { null }
            )
            _isSubmitting.value = false
            _submissionSuccess.value = ok
            if (ok) {
                _message.value = ""
                _contactInfo.value = ""
            }
        }
    }

    fun resetSuccess() {
        _submissionSuccess.value = false
    }
}

class FeedbackViewModelFactory(
    private val authRepository: AuthRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FeedbackViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FeedbackViewModel(authRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
