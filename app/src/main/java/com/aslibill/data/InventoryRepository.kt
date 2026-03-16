package com.aslibill.data

import com.aslibill.data.db.CategoryDao
import com.aslibill.data.db.CategoryEntity
import com.aslibill.data.db.ProductDao
import com.aslibill.data.db.ProductEntity
import com.aslibill.data.db.ProductWithCategory
import kotlinx.coroutines.flow.Flow

class InventoryRepository(
  private val categoryDao: CategoryDao,
  private val productDao: ProductDao
) {
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

  suspend fun addCategory(name: String) = categoryDao.insert(CategoryEntity(name = name.trim()))
  suspend fun updateCategory(id: Long, name: String) = categoryDao.update(CategoryEntity(id = id, name = name.trim()))
  suspend fun deleteCategory(category: CategoryEntity) = categoryDao.delete(category)

  suspend fun addProduct(categoryId: Long, name: String, price: Double) =
    productDao.insert(ProductEntity(categoryId = categoryId, name = name.trim(), price = price))

  suspend fun updateProduct(id: Long, categoryId: Long, name: String, price: Double, isActive: Boolean) =
    productDao.update(ProductEntity(id = id, categoryId = categoryId, name = name.trim(), price = price, isActive = isActive))

  suspend fun deleteProduct(entity: ProductEntity) = productDao.delete(entity)
}

