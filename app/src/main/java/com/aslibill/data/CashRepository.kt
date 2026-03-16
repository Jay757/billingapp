package com.aslibill.data

import com.aslibill.data.db.CashDao
import com.aslibill.data.db.CashTransactionEntity
import kotlinx.coroutines.flow.Flow

class CashRepository(private val dao: CashDao) {
  fun observeAll(): Flow<List<CashTransactionEntity>> = dao.observeAll()
  suspend fun add(entity: CashTransactionEntity) = dao.insert(entity)
  suspend fun deleteAll() = dao.deleteAll()
}
