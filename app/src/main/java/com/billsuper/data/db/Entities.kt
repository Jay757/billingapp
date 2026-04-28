package com.billsuper.data.db

data class CategoryEntity(
  val id: Long = 0,
  val userId: Int = 0,
  val name: String
)

data class ProductEntity(
  val id: Long = 0,
  val userId: Int = 0,
  val categoryId: Long,
  val name: String,
  val price: Double,
  val isActive: Boolean = true
)

data class CustomerEntity(
  val id: Long = 0,
  val userId: Int = 0,
  val name: String,
  val mobile: String,
  val address: String? = null
)

data class BillEntity(
  val id: Long = 0,
  val userId: Int = 0,
  val createdAtEpochMs: Long,
  val cashierName: String? = null,
  val customerId: Long? = null,
  val subtotal: Double,
  val tax: Double,
  val total: Double,
  val paymentMethod: String = "CASH"
)

data class BillItemEntity(
  val id: Long = 0,
  val userId: Int = 0,
  val billId: Long,
  val productId: Long? = null,
  val productNameSnapshot: String,
  val qty: Double,
  val rate: Double,
  val lineTotal: Double
)

data class StaffEntity(
  val id: Long = 0,
  val userId: Int = 0,
  val name: String,
  val role: String,
  val mobile: String,
  val isActive: Boolean = true
)

data class CashTransactionEntity(
  val id: Long = 0,
  val userId: Int = 0,
  val type: String, // OPEN | IN | OUT | CLOSE
  val amount: Double,
  val note: String? = null,
  val createdAtEpochMs: Long
)


