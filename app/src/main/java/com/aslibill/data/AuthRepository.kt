package com.aslibill.data

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class UserSession(
    val name: String,
    val phone: String
)

class AuthRepository(context: Context) {
    private val prefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
    
    private val _userSession = MutableStateFlow<UserSession?>(null)
    val userSession: StateFlow<UserSession?> = _userSession.asStateFlow()

    init {
        val name = prefs.getString("user_name", null)
        val phone = prefs.getString("user_phone", null)
        if (name != null && phone != null) {
            _userSession.value = UserSession(name, phone)
        }
    }

    suspend fun login(phone: String, password: String): Boolean {
        // Simulated login: In this version, we just accept any login
        // but normally we'd verify against a backend or local DB
        val savedPhone = prefs.getString("user_phone", null)
        val savedName = prefs.getString("user_name", "User")
        
        if (savedPhone == phone || savedPhone == null) {
            val name = if (savedPhone == phone) savedName!! else "User"
            saveSession(name, phone)
            return true
        }
        return false
    }

    suspend fun signup(name: String, phone: String, password: String): Boolean {
        saveSession(name, phone)
        return true
    }

    private fun saveSession(name: String, phone: String) {
        prefs.edit().apply {
            putString("user_name", name)
            putString("user_phone", phone)
            apply()
        }
        _userSession.value = UserSession(name, phone)
    }

    suspend fun logout() {
        prefs.edit().clear().apply()
        _userSession.value = null
    }
}
