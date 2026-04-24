package com.aslibill.ui.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import android.util.Log
import com.aslibill.data.AuthRepository
import kotlinx.coroutines.launch

class SignupViewModel(private val authRepository: AuthRepository) : ViewModel() {
    private val tag = "AuthFlow"
    var name by mutableStateOf("")
    var phone by mutableStateOf("")
    var password by mutableStateOf("")
    var isLoading by mutableStateOf(false)
    var error by mutableStateOf<String?>(null)

    fun onSignup(onSuccess: (String?) -> Unit) {
        if (name.isBlank() || phone.isBlank() || password.isBlank()) {
            error = "Please fill all fields"
            return
        }
        isLoading = true
        error = null
        viewModelScope.launch {
            val code = authRepository.signup(name, phone, password)
            isLoading = false
            if (code != null || authRepository.lastError.value == null) {
                onSuccess(code)
            } else {
                val reason = authRepository.lastError.value
                error = reason ?: "Signup failed"
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
