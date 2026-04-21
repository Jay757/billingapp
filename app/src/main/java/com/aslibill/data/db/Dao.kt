package com.aslibill.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

data class ProductWithCategory(
  val id: Long,
  val categoryId: Long,
  val categoryName: String,
  val name: String,
  val price: Double,
  val stock: Double,
  val isActive: Boolean
)

@Dao
interface CategoryDao {
  @Query("SELECT * FROM categories WHERE userId = :userId ORDER BY name ASC")
  fun observeAll(userId: Int): Flow<List<CategoryEntity>>

  @Query("SELECT COUNT(*) FROM categories WHERE userId = :userId")
  suspend fun count(userId: Int): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insert(category: CategoryEntity): Long

  @Update
  suspend fun update(category: CategoryEntity)

  @Delete
  suspend fun delete(category: CategoryEntity)
}

@Dao
interface ProductDao {
  @Query("SELECT * FROM products WHERE userId = :userId AND isActive = 1 ORDER BY name ASC")
  fun observeActive(userId: Int): Flow<List<ProductEntity>>

  @Query(
    """
      SELECT p.id, p.categoryId, c.name as categoryName, p.name, p.price, p.stock, p.isActive
      FROM products p
      JOIN categories c ON c.id = p.categoryId
      WHERE p.userId = :userId AND p.isActive = 1
      ORDER BY c.name ASC, p.name ASC
    """
  )
  fun observeActiveWithCategory(userId: Int): Flow<List<ProductWithCategory>>

  @Query("SELECT * FROM products WHERE userId = :userId AND categoryId = :categoryId AND isActive = 1 ORDER BY name ASC")
  fun observeByCategory(userId: Int, categoryId: Long): Flow<List<ProductEntity>>

  @Query("SELECT COUNT(*) FROM products WHERE userId = :userId")
  suspend fun count(userId: Int): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insert(product: ProductEntity): Long

  @Update
  suspend fun update(product: ProductEntity)

  @Delete
  suspend fun delete(product: ProductEntity)

  @Query("Update products SET stock = stock - :qty WHERE userId = :userId AND id = :productId")
  suspend fun decrementStock(userId: Int, productId: Long, qty: Double)
}

data class BillWithItemsRow(
  val billId: Long,
  val createdAtEpochMs: Long,
  val cashierName: String?,
  val subtotal: Double,
  val tax: Double,
  val total: Double,
  val paymentMethod: String,
  val itemCount: Int
)

@Dao
interface BillDao {
  @Insert
  suspend fun insertBill(bill: BillEntity): Long

  @Insert
  suspend fun insertItems(items: List<BillItemEntity>)

  @Transaction
  suspend fun insertBillWithItems(bill: BillEntity, items: List<BillItemEntity>): Long {
    val billId = insertBill(bill)
    insertItems(items.map { it.copy(billId = billId) })
    return billId
  }

  @Query(
    """
      SELECT 
        b.id as billId,
        b.createdAtEpochMs,
        b.cashierName,
        b.subtotal,
        b.tax,
        b.total,
        b.paymentMethod,
        (SELECT COUNT(*) FROM bill_items bi WHERE bi.billId = b.id) as itemCount
      FROM bills b
      WHERE b.userId = :userId
      ORDER BY b.createdAtEpochMs DESC
    """
  )
  fun observeBills(userId: Int): Flow<List<BillWithItemsRow>>

  @Query(
    """
      SELECT 
        b.id as billId,
        b.createdAtEpochMs,
        b.cashierName,
        b.subtotal,
        b.tax,
        b.total,
        b.paymentMethod,
        (SELECT COUNT(*) FROM bill_items bi WHERE bi.billId = b.id) as itemCount
      FROM bills b
      WHERE b.userId = :userId AND b.createdAtEpochMs BETWEEN :fromEpochMs AND :toEpochMs
      ORDER BY b.createdAtEpochMs DESC
    """
  )
  fun observeBillsBetween(userId: Int, fromEpochMs: Long, toEpochMs: Long): Flow<List<BillWithItemsRow>>

  @Query("SELECT * FROM bill_items WHERE userId = :userId AND billId = :billId ORDER BY id ASC")
  suspend fun getBillItems(userId: Int, billId: Long): List<BillItemEntity>

  @Query("DELETE FROM bills WHERE userId = :userId AND id = :billId")
  suspend fun deleteBillById(userId: Int, billId: Long)

  @Query("DELETE FROM bills WHERE userId = :userId")
  suspend fun deleteAllBills(userId: Int)
}


@Dao
interface CustomerDao {
  @Query("SELECT * FROM customers WHERE userId = :userId ORDER BY name ASC")
  fun observeAll(userId: Int): Flow<List<CustomerEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insert(customer: CustomerEntity): Long

  @Update
  suspend fun update(customer: CustomerEntity)

  @Delete
  suspend fun delete(customer: CustomerEntity)
}

@Dao
interface StaffDao {
  @Query("SELECT * FROM staff WHERE userId = :userId ORDER BY name ASC")
  fun observeAll(userId: Int): Flow<List<StaffEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insert(staff: StaffEntity): Long

  @Update
  suspend fun update(staff: StaffEntity)

  @Delete
  suspend fun delete(staff: StaffEntity)
}

data class ItemSalesRow(
  val productName: String,
  val totalQty: Double,
  val totalRevenue: Double
)

data class DayReportRow(
  val dateLabel: String,
  val billCount: Int,
  val cashTotal: Double,
  val onlineTotal: Double,
  val grandTotal: Double
)

@Dao
interface CashDao {
  @Query("SELECT * FROM cash_transactions WHERE userId = :userId ORDER BY createdAtEpochMs DESC")
  fun observeAll(userId: Int): Flow<List<CashTransactionEntity>>

  @Insert
  suspend fun insert(tx: CashTransactionEntity)

  @Query("DELETE FROM cash_transactions WHERE userId = :userId")
  suspend fun deleteAll(userId: Int)
}

@Dao
interface BillAnalyticsDao {
  @Query("""
    SELECT bi.productNameSnapshot as productName,
           SUM(bi.qty) as totalQty,
           SUM(bi.lineTotal) as totalRevenue
    FROM bill_items bi
    JOIN bills b ON b.id = bi.billId
    WHERE b.userId = :userId AND b.createdAtEpochMs BETWEEN :fromEpochMs AND :toEpochMs
    GROUP BY bi.productNameSnapshot
    ORDER BY totalRevenue DESC
  """)
  fun observeItemSales(userId: Int, fromEpochMs: Long, toEpochMs: Long): Flow<List<ItemSalesRow>>

  @Query("""
    SELECT
      strftime('%d-%m-%Y', datetime(b.createdAtEpochMs/1000,'unixepoch','+05:30')) as dateLabel,
      COUNT(b.id) as billCount,
      SUM(CASE WHEN b.paymentMethod = 'CASH' THEN b.total ELSE 0 END) as cashTotal,
      SUM(CASE WHEN b.paymentMethod != 'CASH' AND b.paymentMethod != 'NONE' THEN b.total ELSE 0 END) as onlineTotal,
      SUM(b.total) as grandTotal
    FROM bills b
    WHERE b.userId = :userId AND b.createdAtEpochMs BETWEEN :fromEpochMs AND :toEpochMs
    GROUP BY dateLabel
    ORDER BY b.createdAtEpochMs DESC
  """)
  fun observeDayReport(userId: Int, fromEpochMs: Long, toEpochMs: Long): Flow<List<DayReportRow>>

  @Query("""
    SELECT
      b.customerId,
      c.name as customerName,
      c.mobile as customerMobile,
      SUM(b.total) as totalCredit,
      COUNT(b.id) as billCount
    FROM bills b
    JOIN customers c ON c.id = b.customerId
    WHERE b.userId = :userId AND b.paymentMethod = 'CREDIT'
    GROUP BY b.customerId
    ORDER BY totalCredit DESC
  """)
  fun observeCreditSummary(userId: Int): Flow<List<CreditSummaryRow>>
}

data class CreditSummaryRow(
  val customerId: Long?,
  val customerName: String,
  val customerMobile: String,
  val totalCredit: Double,
  val billCount: Int
)
