package com.aslibill.data.db

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
  tableName = "categories",
  indices = [Index(value = ["name"], unique = true)]
)
data class CategoryEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val name: String
)

@Entity(
  tableName = "products",
  foreignKeys = [
    ForeignKey(
      entity = CategoryEntity::class,
      parentColumns = ["id"],
      childColumns = ["categoryId"],
      onDelete = ForeignKey.RESTRICT
    )
  ],
  indices = [Index("categoryId"), Index(value = ["name"], unique = false)]
)
data class ProductEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val categoryId: Long,
  val name: String,
  val price: Double,
  val isActive: Boolean = true
)

@Entity(tableName = "customers", indices = [Index(value = ["mobile"], unique = true)])
data class CustomerEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val name: String,
  val mobile: String,
  val address: String? = null
)

@Entity(tableName = "bills", indices = [Index("createdAtEpochMs"), Index("customerId")])
data class BillEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val createdAtEpochMs: Long,
  val cashierName: String? = null,
  val customerId: Long? = null,
  val subtotal: Double,
  val tax: Double,
  val total: Double,
  val paymentMethod: String = "CASH"
)

@Entity(
  tableName = "bill_items",
  foreignKeys = [
    ForeignKey(
      entity = BillEntity::class,
      parentColumns = ["id"],
      childColumns = ["billId"],
      onDelete = ForeignKey.CASCADE
    )
  ],
  indices = [Index("billId"), Index("productId")]
)
data class BillItemEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val billId: Long,
  val productId: Long? = null,
  val productNameSnapshot: String,
  val qty: Double,
  val rate: Double,
  val lineTotal: Double
)


@Entity(tableName = "staff")
data class StaffEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val name: String,
  val role: String,
  val mobile: String,
  val isActive: Boolean = true
)

@Entity(tableName = "cash_transactions")
data class CashTransactionEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val type: String, // OPEN | IN | OUT | CLOSE
  val amount: Double,
  val note: String? = null,
  val createdAtEpochMs: Long
)
