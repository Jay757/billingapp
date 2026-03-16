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
    viewModelScope.launch { repo.ensureSeedData() }
  }

  fun addCategory(name: String) = viewModelScope.launch { repo.addCategory(name) }
  fun updateCategory(id: Long, name: String) = viewModelScope.launch { repo.updateCategory(id, name) }
  fun deleteCategory(entity: CategoryEntity) = viewModelScope.launch { repo.deleteCategory(entity) }

  fun addProduct(categoryId: Long, name: String, price: Double) =
    viewModelScope.launch { repo.addProduct(categoryId, name, price) }

  fun updateProduct(id: Long, categoryId: Long, name: String, price: Double, isActive: Boolean) =
    viewModelScope.launch { repo.updateProduct(id, categoryId, name, price, isActive) }

  fun deleteProduct(entity: ProductEntity) = viewModelScope.launch { repo.deleteProduct(entity) }
}

