package com.aslibill.data

import com.aslibill.data.db.CashTransactionEntity
import com.aslibill.network.ApiHttpClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONObject

class CashRepository(
  private val authRepository: AuthRepository,
  private val client: ApiHttpClient
) {

  private val _transactions = MutableStateFlow<List<CashTransactionEntity>>(emptyList())
  val transactions: StateFlow<List<CashTransactionEntity>> = _transactions.asStateFlow()

  private suspend fun getUserId(): Int = authRepository.userSession.value?.id ?: throw IllegalStateException("Not logged in")

  fun observeAll(): StateFlow<List<CashTransactionEntity>> = transactions

  suspend fun add(entity: CashTransactionEntity) {
    val token = authRepository.currentToken() ?: return
    val body = JSONObject()
      .put("type", entity.type)
      .put("amount", entity.amount)
      .put("note", entity.note)
      .put("createdAtEpochMs", entity.createdAtEpochMs)
    client.postJson("/cash/transactions", token, body)
    refresh()
  }

  suspend fun deleteAll() {
    val token = authRepository.currentToken() ?: return
    client.delete("/cash/transactions", token)
    refresh()
  }

  suspend fun refresh() {
    val uid = getUserId()
    val token = authRepository.currentToken() ?: return
    runCatching {
      val resp = client.getJsonArray("/cash/transactions", token)
      val list = mutableListOf<CashTransactionEntity>()
      for (i in 0 until resp.length()) {
        val obj = resp.getJSONObject(i)
        list.add(CashTransactionEntity(
          id = obj.getLong("id"),
          userId = uid,
          type = obj.getString("type"),
          amount = obj.getDouble("amount"),
          note = obj.optString("note").takeIf { !obj.isNull("note") },
          createdAtEpochMs = obj.getLong("createdAtEpochMs")
        ))
      }
      _transactions.value = list.sortedByDescending { it.createdAtEpochMs }
    }
  }

  suspend fun syncFromRemote() = refresh()
}

