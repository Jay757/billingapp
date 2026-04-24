package com.aslibill.data

import com.aslibill.data.db.CategoryEntity
import com.aslibill.data.db.ProductEntity
import com.aslibill.data.db.ProductWithCategory
import com.aslibill.network.ApiHttpClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONObject

class InventoryRepository(
  private val authRepository: AuthRepository,
  private val client: ApiHttpClient
) {

  private val _categories = MutableStateFlow<List<CategoryEntity>>(emptyList())
  val categories: StateFlow<List<CategoryEntity>> = _categories.asStateFlow()

  private val _products = MutableStateFlow<List<ProductWithCategory>>(emptyList())
  val products: StateFlow<List<ProductWithCategory>> = _products.asStateFlow()

  private suspend fun getUserId(): Int = authRepository.userSession.value?.id ?: throw IllegalStateException("Not logged in")

  fun observeCategories(): StateFlow<List<CategoryEntity>> = categories
  
  fun observeProductsWithCategory(): StateFlow<List<ProductWithCategory>> = products

  suspend fun addCategory(name: String) {
    val token = authRepository.currentToken() ?: return
    val body = JSONObject().put("name", name.trim())
    client.postJson("/inventory/categories", token, body)
    refresh()
  }

  suspend fun updateCategory(id: Long, name: String) {
    val token = authRepository.currentToken() ?: return
    val body = JSONObject().put("name", name.trim())
    client.putJson("/inventory/categories/$id", token, body)
    refresh()
  }

  suspend fun deleteCategory(category: CategoryEntity) {
    val token = authRepository.currentToken() ?: return
    client.delete("/inventory/categories/${category.id}", token)
    refresh()
  }

  suspend fun addProduct(categoryId: Long, name: String, price: Double) {
    val token = authRepository.currentToken() ?: return
    val body = JSONObject()
      .put("categoryId", categoryId)
      .put("name", name.trim())
      .put("price", price)
      .put("isActive", true)
    client.postJson("/inventory/products", token, body)
    refresh()
  }

  suspend fun updateProduct(id: Long, categoryId: Long, name: String, price: Double, isActive: Boolean) {
    val token = authRepository.currentToken() ?: return
    val body = JSONObject()
      .put("categoryId", categoryId)
      .put("name", name.trim())
      .put("price", price)
      .put("isActive", isActive)
    client.putJson("/inventory/products/$id", token, body)
    refresh()
  }

  suspend fun deleteProduct(entity: ProductEntity) {
    val token = authRepository.currentToken() ?: return
    client.delete("/inventory/products/${entity.id}", token)
    refresh()
  }

  suspend fun refresh() {
    val uid = getUserId()
    val token = authRepository.currentToken() ?: return
    
    runCatching {
      // Fetch categories
      val catResp = client.getJsonArray("/inventory/categories", token)
      val catList = mutableListOf<CategoryEntity>()
      for (i in 0 until catResp.length()) {
        val obj = catResp.getJSONObject(i)
        catList.add(CategoryEntity(
          id = obj.getLong("id"),
          userId = uid,
          name = obj.getString("name")
        ))
      }
      _categories.value = catList.sortedBy { it.name }
      
      // Fetch products
      val prodResp = client.getJsonArray("/inventory/products", token)
      val prodList = mutableListOf<ProductWithCategory>()
      for (i in 0 until prodResp.length()) {
        val obj = prodResp.getJSONObject(i)
        val catId = obj.getLong("categoryId")
        val catName = catList.find { it.id == catId }?.name ?: "Unknown"
        prodList.add(ProductWithCategory(
          id = obj.getLong("id"),
          categoryId = catId,
          categoryName = catName,
          name = obj.getString("name"),
          price = obj.getDouble("price"),
          isActive = obj.getBoolean("isActive")
        ))
      }
      _products.value = prodList.sortedWith(compareBy({ it.categoryName }, { it.name }))
    }
  }

  suspend fun bulkUpload(items: List<Triple<String, String, Double>>): JSONObject {
    val token = authRepository.currentToken() ?: throw IllegalStateException("Not logged in")
    
    val itemsArray = org.json.JSONArray()
    items.forEach { (catName, prodName, price) ->
      itemsArray.put(JSONObject()
        .put("categoryName", catName)
        .put("productName", prodName)
        .put("price", price)
      )
    }
    
    val body = JSONObject().put("items", itemsArray)
    val response = client.postJson("/inventory/bulk-upload", token, body)
    refresh()
    return response
  }

  suspend fun bulkUploadFile(fileBytes: ByteArray, fileName: String): JSONObject {
    val token = authRepository.currentToken() ?: throw IllegalStateException("Not logged in")
    val response = client.uploadFile("/inventory/bulk-upload-file", token, fileBytes, fileName)
    refresh()
    return response
  }

  suspend fun syncFromRemote() = refresh()
}


