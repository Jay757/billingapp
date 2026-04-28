package com.billsuper.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.billsuper.data.BillingRepository
import com.billsuper.data.InventoryRepository
import com.billsuper.data.db.BillEntity
import com.billsuper.data.db.BillItemEntity
import com.billsuper.data.db.CategoryEntity
import com.billsuper.data.db.ProductWithCategory
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

data class CartLine(
  val productId: Long,
  val name: String,
  val rate: Double,
  val qty: Double
) {
  val total: Double get() = rate * qty
}

enum class PaymentMode { NONE, CASH, ONLINE, CREDIT, SPLIT }

data class SaveBillDraft(
  val includeGst: Boolean = false,
  val discountPercent: Double = 0.0,
  val paymentMode: PaymentMode = PaymentMode.NONE,
  val note: String = ""
)

class ItemWiseBillViewModel(
  private val inventory: InventoryRepository,
  private val billing: BillingRepository
) : ViewModel() {

  private val _isLoading = MutableStateFlow(true)
  val isLoading = _isLoading.asStateFlow()

  val categories: StateFlow<List<CategoryEntity>> =
    inventory.observeCategories().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

  private val allProductsFlow = inventory.observeProductsWithCategory()
    .onEach { _isLoading.value = false }

  val allProducts: StateFlow<List<ProductWithCategory>> =
    allProductsFlow.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

  private val _selectedCategoryId = kotlinx.coroutines.flow.MutableStateFlow<Long?>(null)
  val selectedCategoryId: StateFlow<Long?> = _selectedCategoryId

  private val _searchQuery = kotlinx.coroutines.flow.MutableStateFlow("")
  val searchQuery: StateFlow<String> = _searchQuery

  val filteredProducts: StateFlow<List<ProductWithCategory>> =
    combine(allProductsFlow, _selectedCategoryId, _searchQuery) { prods, catId, q ->
      prods
        .asSequence()
        .filter { catId == null || it.categoryId == catId }
        .filter { q.isBlank() || it.name.contains(q, ignoreCase = true) }
        .toList()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

  private val _cart = kotlinx.coroutines.flow.MutableStateFlow<List<CartLine>>(emptyList())
  val cart: StateFlow<List<CartLine>> = _cart

  val cartSubtotal: StateFlow<Double> =
    _cart.map { lines -> lines.sumOf { it.total } }
      .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0.0)

  fun selectAllCategories() {
    _selectedCategoryId.value = null
  }

  fun selectCategory(id: Long) {
    _selectedCategoryId.value = id
  }

  fun setSearchQuery(q: String) {
    _searchQuery.value = q
  }

  fun clearCart() {
    _cart.value = emptyList()
  }

  fun addProduct(product: ProductWithCategory) {
    val existing = _cart.value.firstOrNull { it.productId == product.id }
    _cart.value = if (existing == null) {
      _cart.value + CartLine(productId = product.id, name = product.name, rate = product.price, qty = 1.0)
    } else {
      _cart.value.map {
        if (it.productId == product.id) it.copy(qty = it.qty + 1.0) else it
      }
    }
  }

  fun setQty(productId: Long, qty: Double) {
    if (qty <= 0) {
      _cart.value = _cart.value.filterNot { it.productId == productId }
    } else {
      _cart.value = _cart.value.map { if (it.productId == productId) it.copy(qty = qty) else it }
    }
  }

  fun saveBill(draft: SaveBillDraft, cashierName: String? = "user", onSaved: (Long) -> Unit, onError: (Throwable) -> Unit) {
    val lines = _cart.value
    if (lines.isEmpty()) return

    viewModelScope.launch {
      _isLoading.value = true
      try {
        val subtotal = lines.sumOf { it.total }
        val discount = subtotal * (draft.discountPercent.coerceIn(0.0, 100.0) / 100.0)
        val discounted = (subtotal - discount).coerceAtLeast(0.0)
        val gstRate = if (draft.includeGst) 0.0 else 0.0
        val tax = discounted * gstRate
        val total = discounted + tax

        val bill = BillEntity(
          createdAtEpochMs = System.currentTimeMillis(),
          cashierName = cashierName,
          subtotal = discounted,
          tax = tax,
          total = total,
          paymentMethod = draft.paymentMode.name
        )

        val items = lines.map { line ->
          BillItemEntity(
            billId = 0,
            productId = line.productId,
            productNameSnapshot = line.name,
            qty = line.qty,
            rate = line.rate,
            lineTotal = line.total
          )
        }

        val billId = billing.saveBill(bill, items)
        clearCart()
        onSaved(billId)
      } catch (t: Throwable) {
        onError(t)
      } finally {
        _isLoading.value = false
      }
    }
  }
}


