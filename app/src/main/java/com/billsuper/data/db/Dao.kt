package com.billsuper.data.db

data class ProductWithCategory(
  val id: Long,
  val categoryId: Long,
  val categoryName: String,
  val name: String,
  val price: Double,
  val isActive: Boolean
)

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

data class CreditSummaryRow(
  val customerId: Long?,
  val customerName: String,
  val customerMobile: String,
  val totalCredit: Double,
  val billCount: Int
)

data class SalesSummaryRow(
  val totalBills: Int,
  val totalRevenue: Double,
  val cashTotal: Double,
  val onlineTotal: Double,
  val topItems: List<ItemSalesRow>
)


