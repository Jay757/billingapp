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
  private val statusRepo: com.aslibill.data.NetworkStatusRepository? = null,
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
      statusRepo?.updateStatus(true)
      if (body.isBlank()) return@withContext JSONObject()
      return@withContext JSONObject(body)
    } catch (e: Exception) {
      statusRepo?.updateStatus(false, e.message)
      throw e
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
      statusRepo?.updateStatus(true)
      if (respBody.isBlank()) return@withContext JSONObject()
      return@withContext JSONObject(respBody)
    } catch (e: Exception) {
      statusRepo?.updateStatus(false, e.message)
      throw e
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
      statusRepo?.updateStatus(true)
      if (respBody.isBlank()) return@withContext JSONObject()
      return@withContext JSONObject(respBody)
    } catch (e: Exception) {
      statusRepo?.updateStatus(false, e.message)
      throw e
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
      statusRepo?.updateStatus(true)
      if (body.isBlank()) return@withContext JSONObject()
      return@withContext JSONObject(body)
    } catch (e: Exception) {
      statusRepo?.updateStatus(false, e.message)
      throw e
    } finally {
      conn.disconnect()
    }
  }

  suspend fun checkHealth(): Boolean = withContext(Dispatchers.IO) {
    val url = URL(buildUrl("/health"))
    val conn = (url.openConnection() as HttpURLConnection).apply {
      requestMethod = "GET"
      connectTimeout = 15000 // Increased for slower backends like Render
      readTimeout = 15000
    }

    return@withContext try {
      val code = conn.responseCode
      code in 200..299
    } catch (e: Exception) {
      false
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
      statusRepo?.updateStatus(true)
      if (body.isBlank()) return@withContext org.json.JSONArray()
      return@withContext org.json.JSONArray(body)
    } catch (e: Exception) {
      statusRepo?.updateStatus(false, e.message)
      throw e
    } finally {
      conn.disconnect()
    }
  }

  suspend fun uploadFile(
    path: String,
    token: String?,
    fileBytes: ByteArray,
    fileName: String,
    mimeType: String = "application/octet-stream"
  ): JSONObject = withContext(Dispatchers.IO) {
    val boundary = "Boundary-${System.currentTimeMillis()}"
    val lineEnd = "\r\n"
    val twoHyphens = "--"

    val url = URL(buildUrl(path))
    val conn = (url.openConnection() as HttpURLConnection).apply {
      requestMethod = "POST"
      connectTimeout = connectTimeoutMs
      readTimeout = readTimeoutMs
      doOutput = true
      setRequestProperty("Connection", "Keep-Alive")
      setRequestProperty("Cache-Control", "no-cache")
      setRequestProperty("Content-Type", "multipart/form-data;boundary=$boundary")
      if (!token.isNullOrBlank()) setRequestProperty("Authorization", "Bearer $token")
    }

    try {
      conn.outputStream.use { os ->
        val writer = os.bufferedWriter()
        
        // Form field: file
        writer.write(twoHyphens + boundary + lineEnd)
        writer.write("Content-Disposition: form-data; name=\"file\"; filename=\"$fileName\"$lineEnd")
        writer.write("Content-Type: $mimeType$lineEnd")
        writer.write(lineEnd)
        writer.flush()
        
        os.write(fileBytes)
        os.write(lineEnd.toByteArray())
        
        // Final boundary
        writer.write(twoHyphens + boundary + twoHyphens + lineEnd)
        writer.flush()
      }

      val respBody = readBody(conn)
      ensureSuccess(conn, respBody)
      statusRepo?.updateStatus(true)
      if (respBody.isBlank()) return@withContext JSONObject()
      return@withContext JSONObject(respBody)
    } catch (e: Exception) {
      statusRepo?.updateStatus(false, e.message)
      throw e
    } finally {
      conn.disconnect()
    }
  }
}
