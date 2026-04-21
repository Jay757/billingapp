package com.aslibill.data

import com.aslibill.BuildConfig
import com.aslibill.data.db.CustomerDao
import com.aslibill.data.db.CustomerEntity
import com.aslibill.network.ApiHttpClient
import kotlinx.coroutines.flow.Flow
import org.json.JSONObject

class CustomerRepository(
  private val dao: CustomerDao,
  private val authRepository: AuthRepository
) {
  private val client = ApiHttpClient(BuildConfig.API_BASE_URL)

  private suspend fun getUserId(): Int = authRepository.userSession.value?.id ?: throw IllegalStateException("Not logged in")

  fun observeAll(): Flow<List<CustomerEntity>> {
    val session = authRepository.userSession.value ?: return kotlinx.coroutines.flow.flowOf(emptyList())
    return dao.observeAll(session.id)
  }

  suspend fun add(entity: CustomerEntity): Long {
    val uid = getUserId()
    val id = dao.insert(entity.copy(userId = uid))
    runCatching {
      val token = authRepository.currentToken() ?: return@runCatching
      val body = JSONObject()
        .put("name", entity.name)
        .put("mobile", entity.mobile)
        .put("address", entity.address)
      client.postJson("/customers", token, body)
    }
    return id
  }

  suspend fun update(entity: CustomerEntity) {
    val uid = getUserId()
    dao.update(entity.copy(userId = uid))
    runCatching {
      val token = authRepository.currentToken() ?: return@runCatching
      val body = JSONObject()
        .put("name", entity.name)
        .put("mobile", entity.mobile)
        .put("address", entity.address)
      client.putJson("/customers/${entity.id}", token, body)
    }
  }

  suspend fun delete(entity: CustomerEntity) {
    dao.delete(entity)
    runCatching {
      val token = authRepository.currentToken() ?: return@runCatching
      client.delete("/customers/${entity.id}", token)
    }
  }

  suspend fun syncFromRemote() {
    val uid = getUserId()
    val token = authRepository.currentToken() ?: return
    runCatching {
      val resp = client.getJsonArray("/customers", token)
      for (i in 0 until resp.length()) {
        val obj = resp.getJSONObject(i)
        dao.insert(CustomerEntity(
          id = obj.getLong("id"),
          userId = uid,
          name = obj.getString("name"),
          mobile = obj.getString("mobile"),
          address = obj.optString("address", null)
        ))
      }
    }
  }
}
