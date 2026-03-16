package com.aslibill.data

import com.aslibill.data.db.BillDao
import com.aslibill.data.db.BillItemEntity
import com.aslibill.data.db.BillWithItemsRow
import com.aslibill.data.db.BillEntity
import kotlinx.coroutines.flow.Flow

class BillingRepository(
  private val billDao: BillDao
) {
  suspend fun saveBill(
    bill: BillEntity,
    items: List<BillItemEntity>
  ): Long = billDao.insertBillWithItems(bill, items)

  fun observeBillsBetween(fromEpochMs: Long, toEpochMs: Long): Flow<List<BillWithItemsRow>> =
    billDao.observeBillsBetween(fromEpochMs, toEpochMs)

  suspend fun getBillItems(billId: Long): List<BillItemEntity> = billDao.getBillItems(billId)
  suspend fun deleteBill(billId: Long) = billDao.deleteBillById(billId)
  suspend fun deleteAllBills() = billDao.deleteAllBills()
}

