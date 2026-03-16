package com.aslibill.data

import com.aslibill.data.db.CustomerDao
import com.aslibill.data.db.CustomerEntity
import kotlinx.coroutines.flow.Flow

class CustomerRepository(private val dao: CustomerDao) {
  fun observeAll(): Flow<List<CustomerEntity>> = dao.observeAll()
  suspend fun add(entity: CustomerEntity): Long = dao.insert(entity)
  suspend fun update(entity: CustomerEntity) = dao.update(entity)
  suspend fun delete(entity: CustomerEntity) = dao.delete(entity)
}
