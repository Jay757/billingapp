package com.aslibill.ui.screens

import androidx.compose.material3.ExperimentalMaterial3Api

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.layout.size
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.aslibill.data.db.CategoryEntity
import com.aslibill.data.db.ProductEntity
import com.aslibill.ui.components.Chip
import com.aslibill.ui.components.DarkCard
import com.aslibill.ui.components.GrayButton
import com.aslibill.ui.components.OrangeButton
import com.aslibill.ui.components.ScreenSurface
import com.aslibill.ui.theme.AsliColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryScreen(
  contentPadding: PaddingValues,
  vm: InventoryViewModel
) {
  val categories by vm.categories.collectAsState()
  val products by vm.products.collectAsState()

  var tab by remember { mutableStateOf(InventoryTab.Category) }

  var showAdd by remember { mutableStateOf(false) }
  var editCategory by remember { mutableStateOf<CategoryEntity?>(null) }
  var editProduct by remember { mutableStateOf<ProductDraft?>(null) }

  ScreenSurface {
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(contentPadding)
        .padding(16.dp)
    ) {
      Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text("Inventory", color = AsliColors.TextPrimary, style = MaterialTheme.typography.headlineSmall.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.Bold))
            Text("Manage your stock and prices", color = AsliColors.TextSecondary, style = MaterialTheme.typography.bodySmall)
          }
          GrayButton("Bulk Upload", onClick = { /* TODO */ })
        }

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Chip(
            modifier = Modifier.weight(1f),
            text = "Categories",
            selected = tab == InventoryTab.Category,
            onClick = { tab = InventoryTab.Category }
          )
          Chip(
            modifier = Modifier.weight(1f),
            text = "Products",
            selected = tab == InventoryTab.Product,
            onClick = { tab = InventoryTab.Product }
          )
        }

        if (tab == InventoryTab.Category) {
          LazyVerticalGrid(
            columns = GridCells.Fixed(1),
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 80.dp)
          ) {
            items(categories, key = { it.id }) { cat ->
              DarkCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                  modifier = Modifier.padding(16.dp),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text(
                    cat.name,
                    color = AsliColors.TextPrimary,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.Bold),
                    modifier = Modifier.weight(1f)
                  )
                  Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(onClick = { editCategory = cat; showAdd = true }, modifier = Modifier.size(32.dp)) {
                      Icon(Icons.Outlined.Edit, contentDescription = "Edit", tint = AsliColors.Primary, modifier = Modifier.size(20.dp))
                    }
                    IconButton(onClick = { vm.deleteCategory(cat) }, modifier = Modifier.size(32.dp)) {
                      Icon(Icons.Outlined.Delete, contentDescription = "Delete", tint = AsliColors.Primary, modifier = Modifier.size(20.dp))
                    }
                  }
                }
              }
            }
          }
        } else {
          LazyVerticalGrid(
            columns = GridCells.Fixed(1),
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 80.dp)
          ) {
            items(products, key = { it.id }) { p ->
              DarkCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                  modifier = Modifier.padding(16.dp),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                  ) {
                    Text(
                      p.name,
                      color = AsliColors.TextPrimary,
                      style = MaterialTheme.typography.titleLarge.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                    )
                    Row(
                      verticalAlignment = Alignment.CenterVertically,
                      horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                      Text(
                        "₹${p.price.toInt()}",
                        color = AsliColors.Primary,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                      )
                      Text(
                        "| Stock: ${p.stock.toInt()}",
                        color = if (p.stock <= 5) AsliColors.Red else AsliColors.TextSecondary,
                        style = MaterialTheme.typography.bodyMedium
                      )
                    }
                    Text(
                      p.categoryName,
                      color = AsliColors.TextSecondary,
                      style = MaterialTheme.typography.bodySmall
                    )
                  }
                  
                  Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(
                      onClick = {
                        editProduct = ProductDraft(
                          id = p.id,
                          categoryId = p.categoryId,
                          name = p.name,
                          price = p.price,
                          stock = p.stock
                        )
                        showAdd = true
                      },
                      modifier = Modifier.size(32.dp)
                    ) {
                      Icon(Icons.Outlined.Edit, contentDescription = "Edit", tint = AsliColors.Primary, modifier = Modifier.size(20.dp))
                    }
                    IconButton(
                      onClick = {
                        vm.deleteProduct(
                          ProductEntity(
                            id = p.id,
                            categoryId = p.categoryId,
                            name = p.name,
                            price = p.price,
                            isActive = p.isActive
                          )
                        )
                      },
                      modifier = Modifier.size(32.dp)
                    ) {
                      Icon(Icons.Outlined.Delete, contentDescription = "Delete", tint = AsliColors.Primary, modifier = Modifier.size(20.dp))
                    }
                  }
                }
              }
            }
          }
        }
      }

      FloatingActionButton(
        onClick = { editCategory = null; editProduct = null; showAdd = true },
        containerColor = AsliColors.Primary,
        contentColor = Color.White,
        modifier = Modifier
          .padding(16.dp)
          .align(Alignment.BottomEnd)
      ) {
        Icon(Icons.Outlined.Add, contentDescription = "Add")
      }

      if (showAdd) {
        if (tab == InventoryTab.Category) {
          CategoryDialog(
            initial = editCategory,
            onDismiss = { showAdd = false; editCategory = null },
            onSave = { id, name ->
              if (id == null) vm.addCategory(name) else vm.updateCategory(id, name)
              showAdd = false
              editCategory = null
            }
          )
        } else {
          ProductDialog(
            categories = categories,
            initial = editProduct,
            onDismiss = { showAdd = false; editProduct = null },
            onSave = { draft ->
              if (draft.id == null) {
                vm.addProduct(draft.categoryId, draft.name, draft.price, draft.stock)
              } else {
                vm.updateProduct(draft.id, draft.categoryId, draft.name, draft.price, draft.stock, isActive = true)
              }
              showAdd = false
              editProduct = null
            }
          )
        }
      }
    }
  }
}

private enum class InventoryTab { Category, Product }

private data class ProductDraft(
  val id: Long? = null,
  val categoryId: Long,
  val name: String,
  val price: Double,
  val stock: Double = 0.0
)

@Composable
private fun CategoryDialog(
  initial: CategoryEntity?,
  onDismiss: () -> Unit,
  onSave: (id: Long?, name: String) -> Unit
) {
  var name by remember(initial) { mutableStateOf(initial?.name.orEmpty()) }
  AlertDialog(
    onDismissRequest = onDismiss,
    containerColor = AsliColors.Card,
    titleContentColor = AsliColors.TextPrimary,
    textContentColor = AsliColors.TextSecondary,
    title = { Text(if (initial == null) "Add Category" else "Edit Category") },
    text = {
      com.aslibill.ui.components.AsliTextField(
        value = name,
        onValueChange = { name = it },
        label = "Category Name"
      )
    },
    confirmButton = {
      TextButton(
        onClick = { onSave(initial?.id, name) },
        enabled = name.trim().isNotEmpty()
      ) {
        Text("SAVE", color = if (name.trim().isNotEmpty()) AsliColors.Orange else Color.Gray)
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("CANCEL", color = AsliColors.Orange)
      }
    }
  )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProductDialog(
  categories: List<CategoryEntity>,
  initial: ProductDraft?,
  onDismiss: () -> Unit,
  onSave: (ProductDraft) -> Unit
) {
  var categoryId by remember(initial, categories) {
    mutableStateOf(initial?.categoryId ?: categories.firstOrNull()?.id ?: 0L)
  }
  var name by remember(initial) { mutableStateOf(initial?.name.orEmpty()) }
  var priceText by remember(initial) { mutableStateOf(if (initial == null) "" else initial.price.toString()) }
  var stockText by remember(initial) { mutableStateOf(if (initial == null) "" else initial.stock.toString()) }

  var expanded by remember { mutableStateOf(false) }
  val selectedCategory = categories.find { it.id == categoryId }

  AlertDialog(
    onDismissRequest = onDismiss,
    containerColor = AsliColors.Card,
    titleContentColor = AsliColors.TextPrimary,
    textContentColor = AsliColors.TextSecondary,
    title = { Text(if (initial == null) "Add Product" else "Edit Product") },
    text = {
      Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Dropdown for Category
        ExposedDropdownMenuBox(
          expanded = expanded,
          onExpandedChange = { expanded = !expanded },
          modifier = Modifier.fillMaxWidth()
        ) {
          com.aslibill.ui.components.AsliTextField(
            value = selectedCategory?.name ?: "Select Category",
            onValueChange = {},
            label = "Category",
            modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable)
          )
          
          ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(AsliColors.Card)
          ) {
            categories.forEach { cat ->
              DropdownMenuItem(
                text = { Text(cat.name, color = AsliColors.TextPrimary) },
                onClick = {
                  categoryId = cat.id
                  expanded = false
                }
              )
            }
          }
        }

        com.aslibill.ui.components.AsliTextField(
          value = name,
          onValueChange = { name = it },
          label = "Product Name"
        )
        
        com.aslibill.ui.components.AsliTextField(
          value = priceText,
          onValueChange = { priceText = it },
          label = "Price",
          keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number)
        )
        
        com.aslibill.ui.components.AsliTextField(
          value = stockText,
          onValueChange = { stockText = it },
          label = "Initial Stock",
          keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number)
        )
      }
    },
    confirmButton = {
      val price = priceText.toDoubleOrNull()
      TextButton(
        onClick = {
          onSave(
            ProductDraft(
              id = initial?.id,
              categoryId = categoryId,
              name = name,
              price = price ?: 0.0,
              stock = stockText.toDoubleOrNull() ?: 0.0
            )
          )
        },
        enabled = categoryId != 0L && name.trim().isNotEmpty() && priceText.toDoubleOrNull() != null && stockText.toDoubleOrNull() != null
      ) {
        Text("SAVE", color = if (categoryId != 0L && name.trim().isNotEmpty() && priceText.toDoubleOrNull() != null && stockText.toDoubleOrNull() != null) AsliColors.Orange else Color.Gray)
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("CANCEL", color = AsliColors.Orange)
      }
    }
  )
}

