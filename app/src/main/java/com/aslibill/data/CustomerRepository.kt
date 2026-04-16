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

  fun observeAll(): Flow<List<CustomerEntity>> = dao.observeAll()
  suspend fun add(entity: CustomerEntity): Long {
    val id = dao.insert(entity)
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
    dao.update(entity)
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
}
