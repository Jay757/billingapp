package com.aslibill.data

import com.aslibill.BuildConfig
import com.aslibill.data.db.StaffDao
import com.aslibill.data.db.StaffEntity
import com.aslibill.network.ApiHttpClient
import kotlinx.coroutines.flow.Flow
import org.json.JSONObject

class StaffRepository(
  private val dao: StaffDao,
  private val authRepository: AuthRepository,
  private val client: ApiHttpClient
) {

  private suspend fun getUserId(): Int = authRepository.userSession.value?.id ?: throw IllegalStateException("Not logged in")

  fun observeAll(): Flow<List<StaffEntity>> {
    val session = authRepository.userSession.value ?: return kotlinx.coroutines.flow.flowOf(emptyList())
    return dao.observeAll(session.id)
  }

  suspend fun add(entity: StaffEntity): Long {
    val uid = getUserId()
    val id = dao.insert(entity.copy(userId = uid))
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
    val uid = getUserId()
    dao.update(entity.copy(userId = uid))
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

  suspend fun syncFromRemote() {
    val uid = getUserId()
    val token = authRepository.currentToken() ?: return
    runCatching {
      val resp = client.getJsonArray("/staff", token)
      for (i in 0 until resp.length()) {
        val obj = resp.getJSONObject(i)
        dao.insert(StaffEntity(
          id = obj.getLong("id"),
          userId = uid,
          name = obj.getString("name"),
          role = obj.getString("role"),
          mobile = obj.getString("mobile"),
          isActive = obj.getBoolean("isActive")
        ))
      }
    }
  }
}
