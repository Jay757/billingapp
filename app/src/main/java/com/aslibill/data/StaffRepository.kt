package com.aslibill.data

import com.aslibill.data.db.StaffDao
import com.aslibill.data.db.StaffEntity
import kotlinx.coroutines.flow.Flow

class StaffRepository(private val dao: StaffDao) {
  fun observeAll(): Flow<List<StaffEntity>> = dao.observeAll()
  suspend fun add(entity: StaffEntity): Long = dao.insert(entity)
  suspend fun update(entity: StaffEntity) = dao.update(entity)
  suspend fun delete(entity: StaffEntity) = dao.delete(entity)
}
