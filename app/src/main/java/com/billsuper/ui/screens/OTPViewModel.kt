package com.billsuper.ui.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import android.util.Log
import com.billsuper.data.AuthRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class OTPViewModel(private val authRepository: AuthRepository) : ViewModel() {
    private val tag = "OTPFlow"
    var otpCode by mutableStateOf("")
    var isLoading by mutableStateOf(false)
    var error by mutableStateOf<String?>(null)
    var generatedOtp by mutableStateOf<String?>(null)
    var resendTimer by mutableStateOf(30)
    var canResend by mutableStateOf(false)

    init {
        startResendTimer()
    }

    private fun startResendTimer() {
        canResend = false
        resendTimer = 30
        viewModelScope.launch {
            while (resendTimer > 0) {
                delay(1000)
                resendTimer--
            }
            canResend = true
        }
    }

    fun onVerify(phone: String, onSuccess: () -> Unit) {
        if (otpCode.length < 6) {
            error = "Please enter 6-digit OTP"
            return
        }
        isLoading = true
        error = null
        viewModelScope.launch {
            val success = authRepository.verifyOtp(phone, otpCode)
            isLoading = false
            if (success) {
                onSuccess()
            } else {
                error = authRepository.lastError.value ?: "Verification failed"
            }
        }
    }

    fun onResend(phone: String) {
        if (!canResend) return
        viewModelScope.launch {
            val code = authRepository.resendOtp(phone)
            if (code != null) {
                generatedOtp = code
                startResendTimer()
            } else {
                error = authRepository.lastError.value ?: "Resend failed"
            }
        }
    }
}

class OTPViewModelFactory(private val authRepository: AuthRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OTPViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return OTPViewModel(authRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

