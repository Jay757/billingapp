package com.aslibill.data

import android.content.Context
import com.aslibill.printing.StoreConfig
import com.aslibill.ui.theme.ThemeMode
import com.aslibill.ui.theme.UiPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.*

class SettingsRepository(
    context: Context,
    private val authRepository: AuthRepository,
    private val client: com.aslibill.network.ApiHttpClient,
    private val appScope: kotlinx.coroutines.CoroutineScope
) {
    private val prefs = context.getSharedPreferences("ui_settings", Context.MODE_PRIVATE)

    private val _settings = MutableStateFlow(StoreConfig(
        storeName = "Loading...",
        address = "",
        phone = null,
        gstNumber = null,
        thankYouMessage = null,
        paperWidthChars = 32
    ))
    val settings: StateFlow<StoreConfig> = _settings.asStateFlow()
    val userSession = authRepository.userSession
    
    private val _uiPreferences = MutableStateFlow(loadUiPreferences())
    val uiPreferences: StateFlow<UiPreferences> = _uiPreferences.asStateFlow()

    fun saveSettings(config: StoreConfig) {
        _settings.value = config

        // Sync to remote immediately
        appScope.launch(Dispatchers.IO) {
            runCatching {
                val token = authRepository.currentToken() ?: return@runCatching
                val body = org.json.JSONObject()
                    .put("storeName", config.storeName)
                    .put("address", config.address)
                    .put("phone", config.phone)
                    .put("gstNumber", config.gstNumber)
                    .put("thankYouMessage", config.thankYouMessage)
                    .put("paperWidthChars", config.paperWidthChars)
                client.putJson("/settings/print", token, body)
            }
        }
    }

    suspend fun syncFromRemote() {
        val token = authRepository.currentToken() ?: return
        runCatching {
            val obj = client.getJson("/settings/print", token)
            val config = StoreConfig(
                storeName = obj.getString("storeName"),
                address = obj.getString("address"),
                phone = obj.optString("phone").takeIf { !obj.isNull("phone") },
                gstNumber = obj.optString("gstNumber").takeIf { !obj.isNull("gstNumber") },
                thankYouMessage = obj.optString("thankYouMessage").takeIf { !obj.isNull("thankYouMessage") },
                paperWidthChars = obj.getInt("paperWidthChars")
            )
            _settings.value = config
        }
    }

    private fun loadUiPreferences(): UiPreferences {
        val mode = prefs.getString("theme_mode", ThemeMode.LIGHT.name)
        return UiPreferences(
            mode = runCatching { ThemeMode.valueOf(mode ?: ThemeMode.LIGHT.name) }.getOrDefault(ThemeMode.LIGHT)
        )
    }

    fun saveUiPreferences(mode: ThemeMode) {
        prefs.edit().apply {
            putString("theme_mode", mode.name)
            apply()
        }
        _uiPreferences.value = UiPreferences(mode = mode)
    }
}

