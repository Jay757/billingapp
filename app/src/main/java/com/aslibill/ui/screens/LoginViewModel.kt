package com.aslibill.ui.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.aslibill.data.AuthRepository
import kotlinx.coroutines.launch

class LoginViewModel(private val authRepository: AuthRepository) : ViewModel() {
    var phone by mutableStateOf("")
    var password by mutableStateOf("")
    var isLoading by mutableStateOf(false)
    var error by mutableStateOf<String?>(null)

    fun onLogin(onSuccess: () -> Unit) {
        if (phone.isBlank() || password.isBlank()) {
            error = "Please fill all fields"
            return
        }
        isLoading = true
        error = null
        viewModelScope.launch {
            val success = authRepository.login(phone, password)
            isLoading = false
            if (success) {
                onSuccess()
            } else {
                val reason = authRepository.lastError.value
                error = reason ?: "Invalid credentials"
            }
        }
    }
}

class LoginViewModelFactory(private val authRepository: AuthRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(authRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
