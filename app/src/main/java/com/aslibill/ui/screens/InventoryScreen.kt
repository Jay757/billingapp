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
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Chip(
            modifier = Modifier.weight(1f),
            text = "Category",
            selected = tab == InventoryTab.Category,
            onClick = { tab = InventoryTab.Category }
          )
          Chip(
            modifier = Modifier.weight(1f),
            text = "Product",
            selected = tab == InventoryTab.Product,
            onClick = { tab = InventoryTab.Product }
          )
          Chip(
            modifier = Modifier.weight(1f),
            text = "Upload",
            selected = false,
            onClick = { /* TODO bulk upload */ }
          )
          Chip(
            modifier = Modifier.weight(1.2f),
            text = "Item Wise\nBill",
            selected = true,
            onClick = { /* TODO navigate */ }
          )
        }

        if (tab == InventoryTab.Category) {
          LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
          ) {
            items(categories, key = { it.id }) { cat ->
              DarkCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                  modifier = Modifier.padding(14.dp),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text(
                    cat.name,
                    color = AsliColors.TextPrimary,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                  )
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { editCategory = cat; showAdd = true }) {
                      Icon(Icons.Outlined.Edit, contentDescription = "Edit", tint = AsliColors.Green)
                    }
                    IconButton(onClick = { vm.deleteCategory(cat) }) {
                      Icon(Icons.Outlined.Delete, contentDescription = "Delete", tint = AsliColors.Red)
                    }
                  }
                }
              }
            }
          }
        } else {
          LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
          ) {
            items(products, key = { it.id }) { p ->
              DarkCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                  modifier = Modifier.padding(14.dp),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                  ) {
                    Text(p.name, color = AsliColors.TextPrimary, style = MaterialTheme.typography.titleMedium)
                    Text("₹${p.price.toInt()}", color = AsliColors.Orange, style = MaterialTheme.typography.titleSmall)
                    Text(p.categoryName, color = AsliColors.TextSecondary, style = MaterialTheme.typography.labelSmall)
                  }
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                      onClick = {
                        editProduct = ProductDraft(
                          id = p.id,
                          categoryId = p.categoryId,
                          name = p.name,
                          price = p.price
                        )
                        showAdd = true
                      }
                    ) {
                      Icon(Icons.Outlined.Edit, contentDescription = "Edit", tint = AsliColors.Green)
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
                      }
                    ) {
                      Icon(Icons.Outlined.Delete, contentDescription = "Delete", tint = AsliColors.Red)
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
        containerColor = AsliColors.Orange,
        contentColor = Color.Black,
        modifier = Modifier
          .padding(6.dp)
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
                vm.addProduct(draft.categoryId, draft.name, draft.price)
              } else {
                vm.updateProduct(draft.id, draft.categoryId, draft.name, draft.price, isActive = true)
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
  val price: Double
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
      OutlinedTextField(
        value = name,
        onValueChange = { name = it },
        label = { Text("Category Name") },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
          focusedBorderColor = AsliColors.Orange,
          unfocusedBorderColor = Color.Gray,
          focusedLabelColor = AsliColors.Orange
        )
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
          OutlinedTextField(
            value = selectedCategory?.name ?: "Select Category",
            onValueChange = {},
            readOnly = true,
            label = { Text("Category") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(
              focusedBorderColor = AsliColors.Orange,
              unfocusedBorderColor = Color.Gray,
              focusedLabelColor = AsliColors.Orange
            ),
            modifier = Modifier
              .menuAnchor(MenuAnchorType.PrimaryNotEditable)
              .fillMaxWidth()
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

        OutlinedTextField(
          value = name,
          onValueChange = { name = it },
          label = { Text("Product Name") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth(),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = AsliColors.Orange,
            unfocusedBorderColor = Color.Gray,
            focusedLabelColor = AsliColors.Orange
          )
        )
        OutlinedTextField(
          value = priceText,
          onValueChange = { priceText = it },
          label = { Text("Price") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth(),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = AsliColors.Orange,
            unfocusedBorderColor = Color.Gray,
            focusedLabelColor = AsliColors.Orange
          )
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
              price = price ?: 0.0
            )
          )
        },
        enabled = categoryId != 0L && name.trim().isNotEmpty() && priceText.toDoubleOrNull() != null
      ) {
        Text("SAVE", color = if (categoryId != 0L && name.trim().isNotEmpty() && priceText.toDoubleOrNull() != null) AsliColors.Orange else Color.Gray)
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("CANCEL", color = AsliColors.Orange)
      }
    }
  )
}

