package com.billsuper.data

import com.billsuper.data.db.StaffEntity
import com.billsuper.network.ApiHttpClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONObject

class StaffRepository(
  private val authRepository: AuthRepository,
  private val client: ApiHttpClient
) {

  private val _staff = MutableStateFlow<List<StaffEntity>>(emptyList())
  val staff: StateFlow<List<StaffEntity>> = _staff.asStateFlow()

  private suspend fun getUserId(): Int = authRepository.userSession.value?.id ?: throw IllegalStateException("Not logged in")

  fun observeAll(): StateFlow<List<StaffEntity>> = staff

  suspend fun add(entity: StaffEntity): Long {
    val token = authRepository.currentToken() ?: return 0
    val body = JSONObject()
      .put("name", entity.name)
      .put("role", entity.role)
      .put("mobile", entity.mobile)
      .put("isActive", entity.isActive)
    val resp = client.postJson("/staff", token, body)
    refresh()
    return resp.optLong("id", 0)
  }

  suspend fun update(entity: StaffEntity) {
    val token = authRepository.currentToken() ?: return
    val body = JSONObject()
      .put("name", entity.name)
      .put("role", entity.role)
      .put("mobile", entity.mobile)
      .put("isActive", entity.isActive)
    client.putJson("/staff/${entity.id}", token, body)
    refresh()
  }

  suspend fun delete(entity: StaffEntity) {
    val token = authRepository.currentToken() ?: return
    client.delete("/staff/${entity.id}", token)
    refresh()
  }

  suspend fun refresh() {
    val uid = getUserId()
    val token = authRepository.currentToken() ?: return
    runCatching {
      val resp = client.getJsonArray("/staff", token)
      val list = mutableListOf<StaffEntity>()
      for (i in 0 until resp.length()) {
        val obj = resp.getJSONObject(i)
        list.add(StaffEntity(
          id = obj.getLong("id"),
          userId = uid,
          name = obj.getString("name"),
          role = obj.getString("role"),
          mobile = obj.getString("mobile"),
          isActive = obj.getBoolean("isActive")
        ))
      }
      _staff.value = list.sortedBy { it.name }
    }
  }

  suspend fun syncFromRemote() = refresh()
}


