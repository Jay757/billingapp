package com.aslibill.ui.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.aslibill.data.AuthRepository
import kotlinx.coroutines.launch

class SignupViewModel(private val authRepository: AuthRepository) : ViewModel() {
    var name by mutableStateOf("")
    var phone by mutableStateOf("")
    var password by mutableStateOf("")
    var isLoading by mutableStateOf(false)
    var error by mutableStateOf<String?>(null)

    fun onSignup(onSuccess: () -> Unit) {
        if (name.isBlank() || phone.isBlank() || password.isBlank()) {
            error = "Please fill all fields"
            return
        }
        isLoading = true
        error = null
        viewModelScope.launch {
            val success = authRepository.signup(name, phone, password)
            isLoading = false
            if (success) {
                onSuccess()
            } else {
                error = "Signup failed"
            }
        }
    }
}

class SignupViewModelFactory(private val authRepository: AuthRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SignupViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SignupViewModel(authRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
