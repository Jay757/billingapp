package com.aslibill.data

import com.aslibill.BuildConfig
import com.aslibill.data.db.StaffDao
import com.aslibill.data.db.StaffEntity
import com.aslibill.network.ApiHttpClient
import kotlinx.coroutines.flow.Flow
import org.json.JSONObject

class StaffRepository(
  private val dao: StaffDao,
  private val authRepository: AuthRepository
) {
  private val client = ApiHttpClient(BuildConfig.API_BASE_URL)

  fun observeAll(): Flow<List<StaffEntity>> = dao.observeAll()
  suspend fun add(entity: StaffEntity): Long {
    val id = dao.insert(entity)
    runCatching {
      val token = authRepository.currentToken() ?: return@runCatching
      val body = JSONObject()
        .put("name", entity.name)
        .put("role", entity.role)
        .put("mobile", entity.mobile)
        .put("isActive", entity.isActive)
      client.postJson("/staff", token, body)
    }
    return id
  }

  suspend fun update(entity: StaffEntity) {
    dao.update(entity)
    runCatching {
      val token = authRepository.currentToken() ?: return@runCatching
      val body = JSONObject()
        .put("name", entity.name)
        .put("role", entity.role)
        .put("mobile", entity.mobile)
        .put("isActive", entity.isActive)
      client.putJson("/staff/${entity.id}", token, body)
    }
  }

  suspend fun delete(entity: StaffEntity) {
    dao.delete(entity)
    runCatching {
      val token = authRepository.currentToken() ?: return@runCatching
      client.delete("/staff/${entity.id}", token)
    }
  }
}
