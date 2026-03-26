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

  fun observeAll(): Flow<List<CashTransactionEntity>> = dao.observeAll()
  suspend fun add(entity: CashTransactionEntity) {
    dao.insert(entity)
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
    dao.deleteAll()
    runCatching {
      val token = authRepository.currentToken() ?: return@runCatching
      client.delete("/cash/transactions", token)
    }
  }
}
