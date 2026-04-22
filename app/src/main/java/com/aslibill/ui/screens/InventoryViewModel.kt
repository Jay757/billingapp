package com.aslibill.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aslibill.data.InventoryRepository
import com.aslibill.data.db.CategoryEntity
import com.aslibill.data.db.ProductEntity
import com.aslibill.data.db.ProductWithCategory
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class InventoryViewModel(
  private val repo: InventoryRepository
) : ViewModel() {

  val categories: StateFlow<List<CategoryEntity>> =
    repo.observeCategories().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

  val products: StateFlow<List<ProductWithCategory>> =
    repo.observeProductsWithCategory().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

  init {
    // API sync is handled at the app container level on login
  }

  fun addCategory(name: String) = viewModelScope.launch { repo.addCategory(name) }
  fun updateCategory(id: Long, name: String) = viewModelScope.launch { repo.updateCategory(id, name) }
  fun deleteCategory(entity: CategoryEntity) = viewModelScope.launch { repo.deleteCategory(entity) }

  fun addProduct(categoryId: Long, name: String, price: Double, stock: Double) =
    viewModelScope.launch { repo.addProduct(categoryId, name, price, stock) }

  fun addProducts(categoryId: Long, items: List<Pair<String, Double>>) = viewModelScope.launch {
    items.forEach { (name, price) ->
      repo.addProduct(categoryId, name, price, 0.0)
    }
  }

  fun updateProduct(id: Long, categoryId: Long, name: String, price: Double, stock: Double, isActive: Boolean) =
    viewModelScope.launch { repo.updateProduct(id, categoryId, name, price, stock, isActive) }

  fun deleteProduct(entity: ProductEntity) = viewModelScope.launch { repo.deleteProduct(entity) }
}

