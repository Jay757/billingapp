package com.billsuper.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.billsuper.data.InventoryRepository
import com.billsuper.data.db.CategoryEntity
import com.billsuper.data.db.ProductEntity
import com.billsuper.data.db.ProductWithCategory
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONObject

class InventoryViewModel(
  private val repo: InventoryRepository
) : ViewModel() {

  private val _categoriesLoaded = MutableStateFlow(false)
  private val _productsLoaded = MutableStateFlow(false)

  private val _isLoading = MutableStateFlow(true)
  val isLoading = _isLoading.asStateFlow()

  val categories: StateFlow<List<CategoryEntity>> =
    repo.observeCategories()
      .onEach { _categoriesLoaded.value = true; checkInitialLoad() }
      .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

  val products: StateFlow<List<ProductWithCategory>> =
    repo.observeProductsWithCategory()
      .onEach { _productsLoaded.value = true; checkInitialLoad() }
      .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

  private fun checkInitialLoad() {
    if (_categoriesLoaded.value && _productsLoaded.value) {
      _isLoading.value = false
    }
  }

  init {
    // API sync is handled at the app container level on login
  }

  fun addCategory(name: String) = viewModelScope.launch {
    _isLoading.value = true
    try { repo.addCategory(name) } finally { _isLoading.value = false }
  }

  fun updateCategory(id: Long, name: String) = viewModelScope.launch {
    _isLoading.value = true
    try { repo.updateCategory(id, name) } finally { _isLoading.value = false }
  }

  fun deleteCategory(entity: CategoryEntity) = viewModelScope.launch {
    _isLoading.value = true
    try { repo.deleteCategory(entity) } finally { _isLoading.value = false }
  }

  fun addProduct(categoryId: Long, name: String, price: Double) =
    viewModelScope.launch {
      _isLoading.value = true
      try { repo.addProduct(categoryId, name, price) } finally { _isLoading.value = false }
    }

  fun addProducts(categoryId: Long, items: List<Pair<String, Double>>) = viewModelScope.launch {
    _isLoading.value = true
    try {
      items.forEach { (name, price) ->
        repo.addProduct(categoryId, name, price)
      }
    } finally {
      _isLoading.value = false
    }
  }

  fun updateProduct(id: Long, categoryId: Long, name: String, price: Double, isActive: Boolean) =
    viewModelScope.launch {
      _isLoading.value = true
      try { repo.updateProduct(id, categoryId, name, price, isActive) } finally { _isLoading.value = false }
    }

  fun deleteProduct(entity: ProductEntity) = viewModelScope.launch {
    _isLoading.value = true
    try { repo.deleteProduct(entity) } finally { _isLoading.value = false }
  }

  suspend fun bulkUpload(items: List<Triple<String, String, Double>>): JSONObject {
    _isLoading.value = true
    return try {
      repo.bulkUpload(items)
    } finally {
      _isLoading.value = false
    }
  }

  suspend fun bulkUploadFile(fileBytes: ByteArray, fileName: String): JSONObject {
    _isLoading.value = true
    return try {
      repo.bulkUploadFile(fileBytes, fileName)
    } finally {
      _isLoading.value = false
    }
  }
}


