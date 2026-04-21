package com.aslibill.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class ApiHttpClient(
  private val baseUrl: String,
  private val connectTimeoutMs: Int = 15_000,
  private val readTimeoutMs: Int = 20_000
) {
  private fun buildUrl(path: String): String {
    val base = baseUrl.trimEnd('/')
    val p = path.trimStart('/')
    return "$base/$p"
  }

  private fun readBody(conn: HttpURLConnection): String {
    val input = if (conn.responseCode in 200..299) conn.inputStream else conn.errorStream
    if (input == null) return ""
    val reader = BufferedReader(InputStreamReader(input))
    return reader.readText()
  }

  private fun ensureSuccess(conn: HttpURLConnection, body: String) {
    if (conn.responseCode !in 200..299) {
      throw IllegalStateException("HTTP ${conn.responseCode}: $body")
    }
  }

  suspend fun getJson(path: String, token: String?): JSONObject = withContext(Dispatchers.IO) {
    val url = URL(buildUrl(path))
    val conn = (url.openConnection() as HttpURLConnection).apply {
      requestMethod = "GET"
      connectTimeout = connectTimeoutMs
      readTimeout = readTimeoutMs
      setRequestProperty("Accept", "application/json")
      if (!token.isNullOrBlank()) setRequestProperty("Authorization", "Bearer $token")
    }

    try {
      val body = readBody(conn)
      ensureSuccess(conn, body)
      if (body.isBlank()) return@withContext JSONObject()
      return@withContext JSONObject(body)
    } finally {
      conn.disconnect()
    }
  }

  suspend fun postJson(
    path: String,
    token: String?,
    body: JSONObject
  ): JSONObject = withContext(Dispatchers.IO) {
    val url = URL(buildUrl(path))
    val conn = (url.openConnection() as HttpURLConnection).apply {
      requestMethod = "POST"
      connectTimeout = connectTimeoutMs
      readTimeout = readTimeoutMs
      doOutput = true
      setRequestProperty("Content-Type", "application/json")
      setRequestProperty("Accept", "application/json")
      if (!token.isNullOrBlank()) setRequestProperty("Authorization", "Bearer $token")
    }

    conn.outputStream.use { os ->
      os.write(body.toString().toByteArray(Charsets.UTF_8))
    }

    try {
      val respBody = readBody(conn)
      ensureSuccess(conn, respBody)
      if (respBody.isBlank()) return@withContext JSONObject()
      return@withContext JSONObject(respBody)
    } finally {
      conn.disconnect()
    }
  }

  suspend fun putJson(
    path: String,
    token: String?,
    body: JSONObject
  ): JSONObject = withContext(Dispatchers.IO) {
    val url = URL(buildUrl(path))
    val conn = (url.openConnection() as HttpURLConnection).apply {
      requestMethod = "PUT"
      connectTimeout = connectTimeoutMs
      readTimeout = readTimeoutMs
      doOutput = true
      setRequestProperty("Content-Type", "application/json")
      setRequestProperty("Accept", "application/json")
      if (!token.isNullOrBlank()) setRequestProperty("Authorization", "Bearer $token")
    }

    conn.outputStream.use { os ->
      os.write(body.toString().toByteArray(Charsets.UTF_8))
    }

    try {
      val respBody = readBody(conn)
      ensureSuccess(conn, respBody)
      if (respBody.isBlank()) return@withContext JSONObject()
      return@withContext JSONObject(respBody)
    } finally {
      conn.disconnect()
    }
  }

  suspend fun delete(path: String, token: String?): JSONObject = withContext(Dispatchers.IO) {
    val url = URL(buildUrl(path))
    val conn = (url.openConnection() as HttpURLConnection).apply {
      requestMethod = "DELETE"
      connectTimeout = connectTimeoutMs
      readTimeout = readTimeoutMs
      setRequestProperty("Accept", "application/json")
      if (!token.isNullOrBlank()) setRequestProperty("Authorization", "Bearer $token")
    }

    try {
      val body = readBody(conn)
      ensureSuccess(conn, body)
      if (body.isBlank()) return@withContext JSONObject()
      return@withContext JSONObject(body)
    } finally {
      conn.disconnect()
    }
  }

  suspend fun getJsonArray(path: String, token: String?): org.json.JSONArray = withContext(Dispatchers.IO) {
    val url = URL(buildUrl(path))
    val conn = (url.openConnection() as HttpURLConnection).apply {
      requestMethod = "GET"
      connectTimeout = connectTimeoutMs
      readTimeout = readTimeoutMs
      setRequestProperty("Accept", "application/json")
      if (!token.isNullOrBlank()) setRequestProperty("Authorization", "Bearer $token")
    }

    try {
      val body = readBody(conn)
      ensureSuccess(conn, body)
      if (body.isBlank()) return@withContext org.json.JSONArray()
      return@withContext org.json.JSONArray(body)
    } finally {
      conn.disconnect()
    }
  }
}

