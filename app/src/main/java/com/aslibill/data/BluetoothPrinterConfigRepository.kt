package com.aslibill.data

import android.content.Context
import com.aslibill.BuildConfig
import com.aslibill.network.ApiHttpClient
import org.json.JSONObject

data class BluetoothPrinterConfig(
  val deviceAddress: String?,
  val deviceName: String?
)

class BluetoothPrinterConfigRepository(
  context: Context,
  private val authRepository: AuthRepository,
  private val client: ApiHttpClient
) {
  private val prefs = context.getSharedPreferences("bluetooth_printer", Context.MODE_PRIVATE)

  fun loadLocalConfig(): BluetoothPrinterConfig {
    val address = prefs.getString("device_address", null)
    val name = prefs.getString("device_name", null)
    return BluetoothPrinterConfig(
      deviceAddress = address?.takeIf { it.isNotBlank() },
      deviceName = name?.takeIf { it.isNotBlank() }
    )
  }

  fun saveLocalConfig(config: BluetoothPrinterConfig) {
    prefs.edit().apply {
      putString("device_address", config.deviceAddress)
      putString("device_name", config.deviceName)
      apply()
    }
  }

  suspend fun loadRemoteConfig(): BluetoothPrinterConfig {
    val token = authRepository.currentToken()
    if (token.isNullOrBlank()) return loadLocalConfig()

    return try {
      val resp = client.getJson("/bluetooth/printer", token = token)
      BluetoothPrinterConfig(
        deviceAddress = resp.optString("deviceAddress").takeIf { it.isNotBlank() },
        deviceName = resp.optString("deviceName").takeIf { it.isNotBlank() }
      )
    } catch (_: Throwable) {
      loadLocalConfig()
    }
  }

  suspend fun persistRemoteConfig(config: BluetoothPrinterConfig) {
    // Always persist locally too for offline usage.
    saveLocalConfig(config)

    val token = authRepository.currentToken()
    if (token.isNullOrBlank()) return

    try {
      val body = JSONObject().apply {
        put("deviceAddress", config.deviceAddress)
        put("deviceName", config.deviceName)
      }
      client.putJson("/bluetooth/printer", token = token, body = body)
    } catch (_: Throwable) {
      // Best-effort: keep local state even if remote fails.
    }
  }
}
