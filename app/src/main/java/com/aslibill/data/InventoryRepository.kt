package com.aslibill.data

import com.aslibill.BuildConfig
import com.aslibill.data.db.CategoryDao
import com.aslibill.data.db.CategoryEntity
import com.aslibill.data.db.ProductDao
import com.aslibill.data.db.ProductEntity
import com.aslibill.data.db.ProductWithCategory
import com.aslibill.network.ApiHttpClient
import kotlinx.coroutines.flow.Flow
import org.json.JSONObject

class InventoryRepository(
  private val categoryDao: CategoryDao,
  private val productDao: ProductDao,
  private val authRepository: AuthRepository
) {
  private val client = ApiHttpClient(BuildConfig.API_BASE_URL)

  fun observeCategories(): Flow<List<CategoryEntity>> = categoryDao.observeAll()
  fun observeProductsWithCategory(): Flow<List<ProductWithCategory>> = productDao.observeActiveWithCategory()

  suspend fun ensureSeedData() {
    if (categoryDao.count() > 0) return

    val milkId = categoryDao.insert(CategoryEntity(name = "Milk"))
    val riceId = categoryDao.insert(CategoryEntity(name = "Rice"))
    val soapId = categoryDao.insert(CategoryEntity(name = "Soap"))
    val cat2Id = categoryDao.insert(CategoryEntity(name = "Category 2"))

    if (productDao.count() == 0L) {
      productDao.insert(ProductEntity(categoryId = milkId, name = "Amul milk", price = 28.0))
      productDao.insert(ProductEntity(categoryId = riceId, name = "Basmati rice", price = 50.0))
      productDao.insert(ProductEntity(categoryId = soapId, name = "Dove soap", price = 50.0))
      productDao.insert(ProductEntity(categoryId = soapId, name = "Lux soap", price = 26.0))
      productDao.insert(ProductEntity(categoryId = milkId, name = "Motherdairy milk", price = 30.0))
      productDao.insert(ProductEntity(categoryId = riceId, name = "Sonabhog rice", price = 80.0))
      productDao.insert(ProductEntity(categoryId = cat2Id, name = "Sample item", price = 10.0))
    }

  }

  suspend fun addCategory(name: String) {
    val trimmed = name.trim()
    categoryDao.insert(CategoryEntity(name = trimmed))
    runCatching {
      val token = authRepository.currentToken() ?: return@runCatching
      val body = JSONObject().put("name", trimmed)
      client.postJson("/categories", token, body)
    }
  }

  suspend fun updateCategory(id: Long, name: String) {
    val trimmed = name.trim()
    categoryDao.update(CategoryEntity(id = id, name = trimmed))
    runCatching {
      val token = authRepository.currentToken() ?: return@runCatching
      val body = JSONObject().put("name", trimmed)
      client.putJson("/categories/$id", token, body)
    }
  }

  suspend fun deleteCategory(category: CategoryEntity) {
    categoryDao.delete(category)
    runCatching {
      val token = authRepository.currentToken() ?: return@runCatching
      client.delete("/categories/${category.id}", token)
    }
  }

  suspend fun addProduct(categoryId: Long, name: String, price: Double) {
    val trimmed = name.trim()
    productDao.insert(ProductEntity(categoryId = categoryId, name = trimmed, price = price))
    runCatching {
      val token = authRepository.currentToken() ?: return@runCatching
      val body = JSONObject()
        .put("categoryId", categoryId)
        .put("name", trimmed)
        .put("price", price)
        .put("isActive", true)
      client.postJson("/products", token, body)
    }
  }

  suspend fun updateProduct(id: Long, categoryId: Long, name: String, price: Double, isActive: Boolean) {
    val trimmed = name.trim()
    productDao.update(ProductEntity(id = id, categoryId = categoryId, name = trimmed, price = price, isActive = isActive))
    runCatching {
      val token = authRepository.currentToken() ?: return@runCatching
      val body = JSONObject()
        .put("categoryId", categoryId)
        .put("name", trimmed)
        .put("price", price)
        .put("isActive", isActive)
      client.putJson("/products/$id", token, body)
    }
  }

  suspend fun deleteProduct(entity: ProductEntity) {
    productDao.delete(entity)
    runCatching {
      val token = authRepository.currentToken() ?: return@runCatching
      client.delete("/products/${entity.id}", token)
    }
  }

  suspend fun syncFromRemote() {
    // Reserved for future full two-way sync.
  }
}

