package com.aslibill.data

import com.aslibill.BuildConfig
import com.aslibill.data.db.CashDao
import com.aslibill.data.db.CashTransactionEntity
import com.aslibill.network.ApiHttpClient
import kotlinx.coroutines.flow.Flow
import org.json.JSONObject

class CashRepository(
  private val dao: CashDao,
  private val authRepository: AuthRepository
) {
  private val client = ApiHttpClient(BuildConfig.API_BASE_URL)

  private suspend fun getUserId(): Int = authRepository.userSession.value?.id ?: throw IllegalStateException("Not logged in")

  fun observeAll(): Flow<List<CashTransactionEntity>> {
    val session = authRepository.userSession.value ?: return kotlinx.coroutines.flow.flowOf(emptyList())
    return dao.observeAll(session.id)
  }

  suspend fun add(entity: CashTransactionEntity) {
    val uid = getUserId()
    dao.insert(entity.copy(userId = uid))
    runCatching {
      val token = authRepository.currentToken() ?: return@runCatching
      val body = JSONObject()
        .put("type", entity.type)
        .put("amount", entity.amount)
        .put("note", entity.note)
        .put("createdAtEpochMs", entity.createdAtEpochMs)
      client.postJson("/cash/transactions", token, body)
    }
  }

  suspend fun deleteAll() {
    val uid = getUserId()
    dao.deleteAll(uid)
    runCatching {
      val token = authRepository.currentToken() ?: return@runCatching
      client.delete("/cash/transactions", token)
    }
  }

  suspend fun syncFromRemote() {
    val uid = getUserId()
    val token = authRepository.currentToken() ?: return
    runCatching {
      val resp = client.getJsonArray("/cash/transactions", token)
      for (i in 0 until resp.length()) {
        val obj = resp.getJSONObject(i)
        dao.insert(CashTransactionEntity(
          id = obj.getLong("id"),
          userId = uid,
          type = obj.getString("type"),
          amount = obj.getDouble("amount"),
          note = obj.optString("note").takeIf { !obj.isNull("note") },
          createdAtEpochMs = obj.getLong("createdAtEpochMs")
        ))
      }
    }
  }
}
