package com.aslibill.data

import com.aslibill.data.db.CustomerEntity
import com.aslibill.network.ApiHttpClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONObject

class CustomerRepository(
  private val authRepository: AuthRepository,
  private val client: ApiHttpClient
) {

  private val _customers = MutableStateFlow<List<CustomerEntity>>(emptyList())
  val customers: StateFlow<List<CustomerEntity>> = _customers.asStateFlow()

  private suspend fun getUserId(): Int = authRepository.userSession.value?.id ?: throw IllegalStateException("Not logged in")

  fun observeAll(): StateFlow<List<CustomerEntity>> = customers

  suspend fun add(entity: CustomerEntity): Long {
    val token = authRepository.currentToken() ?: return 0
    val body = JSONObject()
      .put("name", entity.name)
      .put("mobile", entity.mobile)
      .put("address", entity.address)
    val resp = client.postJson("/customers", token, body)
    refresh()
    return resp.optLong("id", 0)
  }

  suspend fun update(entity: CustomerEntity) {
    val token = authRepository.currentToken() ?: return
    val body = JSONObject()
      .put("name", entity.name)
      .put("mobile", entity.mobile)
      .put("address", entity.address)
    client.putJson("/customers/${entity.id}", token, body)
    refresh()
  }

  suspend fun delete(entity: CustomerEntity) {
    val token = authRepository.currentToken() ?: return
    client.delete("/customers/${entity.id}", token)
    refresh()
  }

  suspend fun refresh() {
    val uid = getUserId()
    val token = authRepository.currentToken() ?: return
    runCatching {
      val resp = client.getJsonArray("/customers", token)
      val list = mutableListOf<CustomerEntity>()
      for (i in 0 until resp.length()) {
        val obj = resp.getJSONObject(i)
        list.add(CustomerEntity(
          id = obj.getLong("id"),
          userId = uid,
          name = obj.getString("name"),
          mobile = obj.getString("mobile"),
          address = obj.optString("address").takeIf { !obj.isNull("address") }
        ))
      }
      _customers.value = list.sortedBy { it.name }
    }
  }

  suspend fun syncFromRemote() = refresh()
}

