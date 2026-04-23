package com.aslibill.data

import com.aslibill.network.ApiHttpClient
import org.json.JSONObject

data class BluetoothPrinterConfig(
  val deviceAddress: String?,
  val deviceName: String?
)

class BluetoothPrinterConfigRepository(
  private val authRepository: AuthRepository,
  private val client: ApiHttpClient
) {
  // In-memory cache for the current session
  private var currentConfig: BluetoothPrinterConfig = BluetoothPrinterConfig(null, null)

  fun loadLocalConfig(): BluetoothPrinterConfig {
    return currentConfig
  }

  suspend fun loadRemoteConfig(): BluetoothPrinterConfig {
    val token = authRepository.currentToken() ?: return currentConfig

    return try {
      val resp = client.getJson("/bluetooth/printer", token = token)
      val config = BluetoothPrinterConfig(
        deviceAddress = resp.optString("deviceAddress").takeIf { it.isNotBlank() },
        deviceName = resp.optString("deviceName").takeIf { it.isNotBlank() }
      )
      currentConfig = config
      config
    } catch (_: Throwable) {
      currentConfig
    }
  }

  suspend fun persistRemoteConfig(config: BluetoothPrinterConfig) {
    currentConfig = config
    val token = authRepository.currentToken() ?: return

    try {
      val body = JSONObject().apply {
        put("deviceAddress", config.deviceAddress)
        put("deviceName", config.deviceName)
      }
      client.putJson("/bluetooth/printer", token = token, body = body)
    } catch (_: Throwable) {
      // Best-effort
    }
  }

  fun saveLocalConfig(config: BluetoothPrinterConfig) {
    currentConfig = config
  }
}

