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
  private val authRepository: AuthRepository,
  private val client: ApiHttpClient
) {

  private suspend fun getUserId(): Int = authRepository.userSession.value?.id ?: throw IllegalStateException("Not logged in")

  fun observeCategories(): Flow<List<CategoryEntity>> {
      val session = authRepository.userSession.value ?: return kotlinx.coroutines.flow.flowOf(emptyList())
      return categoryDao.observeAll(session.id)
  }
  
  fun observeProductsWithCategory(): Flow<List<ProductWithCategory>> {
      val session = authRepository.userSession.value ?: return kotlinx.coroutines.flow.flowOf(emptyList())
      return productDao.observeActiveWithCategory(session.id)
  }

  suspend fun addCategory(name: String) {
    val uid = getUserId()
    val trimmed = name.trim()
    val tempId = categoryDao.insert(CategoryEntity(userId = uid, name = trimmed))
    runCatching {
      val token = authRepository.currentToken() ?: return@runCatching
      val body = JSONObject().put("name", trimmed)
      val resp = client.postJson("/inventory/categories", token, body)
      val serverId = resp.getLong("id")
      if (tempId != serverId) {
        categoryDao.delete(CategoryEntity(id = tempId, userId = uid, name = trimmed))
        categoryDao.insert(CategoryEntity(id = serverId, userId = uid, name = trimmed))
      }
    }
  }

  suspend fun updateCategory(id: Long, name: String) {
    val uid = getUserId()
    val trimmed = name.trim()
    categoryDao.update(CategoryEntity(id = id, userId = uid, name = trimmed))
    runCatching {
      val token = authRepository.currentToken() ?: return@runCatching
      val body = JSONObject().put("name", trimmed)
      client.putJson("/inventory/categories/$id", token, body)
    }
  }

  suspend fun deleteCategory(category: CategoryEntity) {
    categoryDao.delete(category)
    runCatching {
      val token = authRepository.currentToken() ?: return@runCatching
      client.delete("/inventory/categories/${category.id}", token)
    }
  }

  suspend fun addProduct(categoryId: Long, name: String, price: Double) {
    val uid = getUserId()
    val trimmed = name.trim()
    val tempId = productDao.insert(ProductEntity(userId = uid, categoryId = categoryId, name = trimmed, price = price))
    runCatching {
      val token = authRepository.currentToken() ?: return@runCatching
      val body = JSONObject()
        .put("categoryId", categoryId)
        .put("name", trimmed)
        .put("price", price)
        .put("isActive", true)
      val resp = client.postJson("/inventory/products", token, body)
      val serverId = resp.getLong("id")
      if (tempId != serverId) {
        productDao.delete(ProductEntity(id = tempId, userId = uid, categoryId = categoryId, name = trimmed, price = price))
        productDao.insert(ProductEntity(id = serverId, userId = uid, categoryId = categoryId, name = trimmed, price = price, isActive = true))
      }
    }
  }

  suspend fun updateProduct(id: Long, categoryId: Long, name: String, price: Double, isActive: Boolean) {
    val uid = getUserId()
    val trimmed = name.trim()
    productDao.update(ProductEntity(id = id, userId = uid, categoryId = categoryId, name = trimmed, price = price, isActive = isActive))
    runCatching {
      val token = authRepository.currentToken() ?: return@runCatching
      val body = JSONObject()
        .put("categoryId", categoryId)
        .put("name", trimmed)
        .put("price", price)
        .put("isActive", isActive)
      client.putJson("/inventory/products/$id", token, body)
    }
  }

  suspend fun deleteProduct(entity: ProductEntity) {
    productDao.delete(entity)
    runCatching {
      val token = authRepository.currentToken() ?: return@runCatching
      client.delete("/inventory/products/${entity.id}", token)
    }
  }



  suspend fun syncFromRemote() {
    val uid = getUserId()
    val token = authRepository.currentToken() ?: return
    
    runCatching {
      // Sync categories
      val catResp = client.getJsonArray("/inventory/categories", token)
      for (i in 0 until catResp.length()) {
        val obj = catResp.getJSONObject(i)
        val id = obj.getLong("id")
        val name = obj.getString("name")
        categoryDao.insert(CategoryEntity(id = id, userId = uid, name = name))
      }
      
      // Sync products
      val prodResp = client.getJsonArray("/inventory/products", token)
      for (i in 0 until prodResp.length()) {
        val obj = prodResp.getJSONObject(i)
        val id = obj.getLong("id")
        val catId = obj.getLong("categoryId")
        val name = obj.getString("name")
        val price = obj.getDouble("price")
        val isActive = obj.getBoolean("isActive")
        productDao.insert(ProductEntity(id = id, userId = uid, categoryId = catId, name = name, price = price, isActive = isActive))
      }
    }
  }
}

