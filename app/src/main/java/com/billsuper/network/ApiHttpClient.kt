package com.billsuper.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class ApiHttpClient(
  private val baseUrl: String,
  private val statusRepo: com.billsuper.data.NetworkStatusRepository? = null,
  connectTimeoutMs: Int = 15_000,
  readTimeoutMs: Int = 20_000
) {
  private val client = OkHttpClient.Builder()
    .connectTimeout(connectTimeoutMs.toLong(), TimeUnit.MILLISECONDS)
    .readTimeout(readTimeoutMs.toLong(), TimeUnit.MILLISECONDS)
    .writeTimeout(readTimeoutMs.toLong(), TimeUnit.MILLISECONDS)
    .retryOnConnectionFailure(true)
    .build()

  private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

  private fun buildUrl(path: String): String {
    val base = baseUrl.trimEnd('/')
    val p = path.trimStart('/')
    return "$base/$p"
  }

  private fun handleResponse(response: okhttp3.Response): String {
    val body = response.body?.string() ?: ""
    if (!response.isSuccessful) {
      throw IllegalStateException("HTTP ${response.code}: $body")
    }
    statusRepo?.updateStatus(true)
    return body
  }

  suspend fun getJson(path: String, token: String?): JSONObject = withContext(Dispatchers.IO) {
    val request = Request.Builder()
      .url(buildUrl(path))
      .get()
      .apply {
        if (!token.isNullOrBlank()) header("Authorization", "Bearer $token")
      }
      .build()

    client.newCall(request).execute().use { resp ->
      val body = handleResponse(resp)
      if (body.isBlank()) JSONObject() else JSONObject(body)
    }
  }

  suspend fun postJson(path: String, token: String?, body: JSONObject): JSONObject = withContext(Dispatchers.IO) {
    val requestBody = body.toString().toRequestBody(jsonMediaType)
    val request = Request.Builder()
      .url(buildUrl(path))
      .post(requestBody)
      .apply {
        if (!token.isNullOrBlank()) header("Authorization", "Bearer $token")
      }
      .build()

    client.newCall(request).execute().use { resp ->
      val respBody = handleResponse(resp)
      if (respBody.isBlank()) JSONObject() else JSONObject(respBody)
    }
  }

  suspend fun putJson(path: String, token: String?, body: JSONObject): JSONObject = withContext(Dispatchers.IO) {
    val requestBody = body.toString().toRequestBody(jsonMediaType)
    val request = Request.Builder()
      .url(buildUrl(path))
      .put(requestBody)
      .apply {
        if (!token.isNullOrBlank()) header("Authorization", "Bearer $token")
      }
      .build()

    client.newCall(request).execute().use { resp ->
      val respBody = handleResponse(resp)
      if (respBody.isBlank()) JSONObject() else JSONObject(respBody)
    }
  }

  suspend fun delete(path: String, token: String?): JSONObject = withContext(Dispatchers.IO) {
    val request = Request.Builder()
      .url(buildUrl(path))
      .delete()
      .apply {
        if (!token.isNullOrBlank()) header("Authorization", "Bearer $token")
      }
      .build()

    client.newCall(request).execute().use { resp ->
      val body = handleResponse(resp)
      if (body.isBlank()) JSONObject() else JSONObject(body)
    }
  }

  suspend fun checkHealth(): Boolean = withContext(Dispatchers.IO) {
    val request = Request.Builder()
      .url(buildUrl("/health"))
      .head() // HEAD is faster than GET
      .build()

    try {
      client.newCall(request).execute().use { resp ->
        resp.isSuccessful
      }
    } catch (e: Exception) {
      false
    }
  }

  suspend fun getJsonArray(path: String, token: String?): org.json.JSONArray = withContext(Dispatchers.IO) {
    val request = Request.Builder()
      .url(buildUrl(path))
      .get()
      .apply {
        if (!token.isNullOrBlank()) header("Authorization", "Bearer $token")
      }
      .build()

    client.newCall(request).execute().use { resp ->
      val body = handleResponse(resp)
      if (body.isBlank()) org.json.JSONArray() else org.json.JSONArray(body)
    }
  }

  suspend fun uploadFile(
    path: String,
    token: String?,
    fileBytes: ByteArray,
    fileName: String,
    mimeType: String = "application/octet-stream"
  ): JSONObject = withContext(Dispatchers.IO) {
    val requestBody = MultipartBody.Builder()
      .setType(MultipartBody.FORM)
      .addFormDataPart("file", fileName, fileBytes.toRequestBody(mimeType.toMediaType()))
      .build()

    val request = Request.Builder()
      .url(buildUrl(path))
      .post(requestBody)
      .apply {
        if (!token.isNullOrBlank()) header("Authorization", "Bearer $token")
      }
      .build()

    client.newCall(request).execute().use { resp ->
      val respBody = handleResponse(resp)
      if (respBody.isBlank()) JSONObject() else JSONObject(respBody)
    }
  }
}

