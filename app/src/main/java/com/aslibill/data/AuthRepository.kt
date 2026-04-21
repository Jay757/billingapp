package com.aslibill.data

import android.content.Context
import android.util.Log
import com.aslibill.BuildConfig
import com.aslibill.network.ApiHttpClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONObject

data class UserSession(
    val id: Int,
    val name: String,
    val phone: String
)

class AuthRepository(private val context: Context) {
    private val tag = "AuthRepository"
    private val prefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
    
    private val _userSession = MutableStateFlow<UserSession?>(null)
    val userSession: StateFlow<UserSession?> = _userSession.asStateFlow()

    private val _token = MutableStateFlow<String?>(prefs.getString("token", null))
    val token: StateFlow<String?> = _token.asStateFlow()
    private val _lastError = MutableStateFlow<String?>(null)
    val lastError: StateFlow<String?> = _lastError.asStateFlow()

    private val client = ApiHttpClient(BuildConfig.API_BASE_URL)

    init {
        val id = prefs.getInt("user_id", -1)
        val name = prefs.getString("user_name", null)
        val phone = prefs.getString("user_phone", null)
        val token = prefs.getString("token", null)
        if (id != -1 && name != null && phone != null) {
            _userSession.value = UserSession(id, name, phone)
            // Token is optional to allow offline/dev flow.
            _token.value = token
        }
    }

    suspend fun login(phone: String, password: String): Boolean {
        return try {
            val req = JSONObject()
              .put("phone", phone)
              .put("password", password)

            val resp = client.postJson("/auth/login", token = null, body = req)
            val token = resp.getString("token")
            val userObj = resp.getJSONObject("user")
            val userId = userObj.getInt("id")
            val name = userObj.getString("name")

            saveSession(id = userId, name = name, phone = phone, token = token)
            _lastError.value = null
            true
        } catch (t: Throwable) {
            Log.e(tag, "Login failed. baseUrl=${BuildConfig.API_BASE_URL}, phone=$phone", t)
            
            val errorMsg = extractErrorMessage(t.message)
            if (errorMsg.contains("403") || errorMsg.contains("not verified")) {
                _lastError.value = "NEEDS_VERIFICATION"
            } else {
                _lastError.value = errorMsg
            }
            false
        }
    }

    suspend fun signup(name: String, phone: String, password: String): String? {
        return try {
            val req = JSONObject()
              .put("name", name)
              .put("phone", phone)
              .put("password", password)

            val resp = client.postJson("/auth/signup", token = null, body = req)
            _lastError.value = null
            resp.optString("code").takeIf { !resp.isNull("code") }
        } catch (t: Throwable) {
            Log.e(tag, "Signup failed. phone=$phone", t)
            _lastError.value = extractErrorMessage(t.message)
            null
        }
    }

    suspend fun verifyOtp(phone: String, code: String): Boolean {
        return try {
            val req = JSONObject()
              .put("phone", phone)
              .put("code", code)

            val resp = client.postJson("/auth/verify-otp", token = null, body = req)
            val token = resp.getString("token")
            val userObj = resp.getJSONObject("user")
            val userId = userObj.getInt("id")
            val name = userObj.getString("name")

            saveSession(id = userId, name = name, phone = phone, token = token)
            _lastError.value = null
            true
        } catch (t: Throwable) {
            Log.e(tag, "OTP verification failed. phone=$phone", t)
            _lastError.value = extractErrorMessage(t.message)
            false
        }
    }

    suspend fun resendOtp(phone: String): String? {
        return try {
            val req = JSONObject().put("phone", phone)
            val resp = client.postJson("/auth/resend-otp", token = null, body = req)
            _lastError.value = null
            resp.optString("code").takeIf { !resp.isNull("code") }
        } catch (t: Throwable) {
            Log.e(tag, "Resend OTP failed. phone=$phone", t)
            _lastError.value = extractErrorMessage(t.message)
            null
        }
    }

    private fun extractErrorMessage(rawMessage: String?): String {
        if (rawMessage == null) return "Unknown error"
        return try {
            // ApiHttpClient message format: "HTTP 500: {"error": "..."}"
            val jsonStart = rawMessage.indexOf("{")
            if (jsonStart != -1) {
                val jsonPart = rawMessage.substring(jsonStart)
                val obj = JSONObject(jsonPart)
                obj.optString("error", rawMessage)
            } else {
                rawMessage
            }
        } catch (_: Exception) {
            rawMessage
        }
    }

    fun currentToken(): String? = _token.value

    private fun saveSession(id: Int, name: String, phone: String, token: String) {
        prefs.edit().apply {
            putInt("user_id", id)
            putString("user_name", name)
            putString("user_phone", phone)
            putString("token", token)
            apply()
        }
        _userSession.value = UserSession(id, name, phone)
        _token.value = token
    }

    suspend fun logout() {
        // Clear all relevant user preferences
        listOf("auth_prefs", "print_settings", "bluetooth_printer").forEach { name ->
            context.getSharedPreferences(name, Context.MODE_PRIVATE).edit().clear().apply()
        }
        
        _userSession.value = null
        _token.value = null
    }

    suspend fun submitFeedback(message: String, contactInfo: String?): Boolean {
        val token = currentToken() ?: return false
        return try {
            val body = JSONObject()
              .put("message", message)
              .put("contactInfo", contactInfo)
            client.postJson("/support/feedback", token = token, body = body)
            true
        } catch (_: Throwable) {
            false
        }
    }

    suspend fun submitContactMessage(message: String, name: String?, phone: String?): Boolean {
        return try {
            val body = JSONObject()
              .put("message", message)
              .put("name", name)
              .put("phone", phone)
            client.postJson("/support/contact", token = currentToken(), body = body)
            true
        } catch (_: Throwable) {
            false
        }
    }

    suspend fun getSubscriptionPlan(): String {
        val token = currentToken() ?: return "FREE"
        return try {
            val resp = client.getJson("/subscription", token)
            resp.optString("plan", "FREE")
        } catch (_: Throwable) {
            "FREE"
        }
    }

    suspend fun upgradeSubscription(): Boolean {
        val token = currentToken() ?: return false
        return try {
            client.postJson("/subscription/upgrade", token, JSONObject())
            true
        } catch (_: Throwable) {
            false
        }
    }

    suspend fun deleteAccount(reason: String?): Boolean {
        val token = currentToken() ?: return false
        val contact = _userSession.value

        // Best-effort: send reason to support before account deletion.
        runCatching {
            if (!reason.isNullOrBlank()) {
                submitContactMessage(
                    message = "Account deletion requested. Reason: $reason",
                    name = contact?.name,
                    phone = contact?.phone
                )
            }
        }

        return try {
            client.delete("/account", token)
            logout()
            true
        } catch (_: Throwable) {
            false
        }
    }
}
