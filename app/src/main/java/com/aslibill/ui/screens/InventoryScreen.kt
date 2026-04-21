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
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.shadow
import com.aslibill.data.db.CategoryEntity
import com.aslibill.data.db.ProductEntity
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import com.aslibill.ui.components.Chip
import com.aslibill.ui.components.DarkCard
import com.aslibill.ui.components.GrayButton
import com.aslibill.ui.components.OrangeButton
import com.aslibill.ui.components.ScreenSurface
import com.aslibill.ui.components.PremiumSegmentedControl
import com.aslibill.ui.theme.AsliColors
import com.aslibill.ui.theme.AppTypography
import com.aslibill.ui.theme.AppSpacing
import com.aslibill.ui.components.GlassButton

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
        .padding(AppSpacing.lg)
    ) {
      Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.lg)
      ) {
        // Use BoxWithConstraints to adjust header design based on width
        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
          val isWide = maxWidth > 600.dp
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text(
                  "Inventory", 
                  color = MaterialTheme.colorScheme.onBackground, 
                  style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Black)
              )
              Text(
                  "Manage your stock and prices", 
                  color = MaterialTheme.colorScheme.onSurfaceVariant, 
                  style = MaterialTheme.typography.bodyMedium
              )
            }

            GlassButton(if (isWide) "Bulk Product Upload" else "Bulk Upload", onClick = { /* TODO */ })
          }
        }

        PremiumSegmentedControl(
            options = listOf("Categories", "Products"),
            selectedIndex = if (tab == InventoryTab.Category) 0 else 1,
            onOptionSelected = { tab = if (it == 0) InventoryTab.Category else InventoryTab.Product }
        )

        AnimatedContent(
            targetState = tab,
            transitionSpec = {
                if (targetState > initialState) {
                    slideInHorizontally { it } + fadeIn() togetherWith slideOutHorizontally { -it } + fadeOut()
                } else {
                    slideInHorizontally { -it } + fadeIn() togetherWith slideOutHorizontally { it } + fadeOut()
                }.using(SizeTransform(clip = false))
            },
            label = "TabContent"
        ) { currentTab ->
            if (currentTab == InventoryTab.Category) {
              LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 280.dp),
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(AppSpacing.md),
                verticalArrangement = Arrangement.spacedBy(AppSpacing.md),
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
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
                        modifier = Modifier.weight(1f)
                      )

                      Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        IconButton(
                          onClick = { editCategory = cat; showAdd = true }, 
                          modifier = Modifier.size(40.dp).background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), CircleShape)
                        ) {
                          Icon(Icons.Outlined.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(22.dp))
                        }
                        IconButton(
                          onClick = { vm.deleteCategory(cat) }, 
                          modifier = Modifier.size(40.dp).background(AsliColors.Red.copy(alpha = 0.1f), CircleShape)
                        ) {
                          Icon(Icons.Outlined.Delete, contentDescription = "Delete", tint = AsliColors.Red, modifier = Modifier.size(22.dp))
                        }
                      }

                    }
                  }
                }
              }
            } else {
              LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 320.dp),
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(AppSpacing.md),
                verticalArrangement = Arrangement.spacedBy(AppSpacing.md),
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
                          color = MaterialTheme.colorScheme.onSurface,
                          style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black)
                        )

                        Row(
                          verticalAlignment = Alignment.CenterVertically,
                          horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                          Box(
                              modifier = Modifier
                                  .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f), RoundedCornerShape(8.dp))
                                  .padding(horizontal = 10.dp, vertical = 6.dp)
                          ) {
                              Text(
                                "₹${p.price.toInt()}",
                                color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                              )
                          }
                          Text(
                            "Stock: ${p.stock.toInt()}",
                            color = if (p.stock <= 5) AsliColors.Red else MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                          )
                        }
                        Text(
                          p.categoryName.uppercase(),
                          color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                          style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                        )

                      }
                      
                      Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
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
                          modifier = Modifier.size(40.dp).background(AsliColors.PrimaryBlue.copy(alpha = 0.1f), CircleShape)
                        ) {
                          Icon(Icons.Outlined.Edit, contentDescription = "Edit", tint = AsliColors.PrimaryBlue, modifier = Modifier.size(22.dp))
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
                          modifier = Modifier.size(40.dp).background(AsliColors.AlertOrange.copy(alpha = 0.1f), CircleShape)
                        ) {
                          Icon(Icons.Outlined.Delete, contentDescription = "Delete", tint = AsliColors.AlertOrange, modifier = Modifier.size(22.dp))
                        }
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
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = Color.White,
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier
          .padding(24.dp)
          .align(Alignment.BottomEnd)
          .shadow(12.dp, RoundedCornerShape(18.dp), spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
      ) {
        Icon(Icons.Outlined.Add, contentDescription = "Add", modifier = Modifier.size(28.dp))
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
    title = { Text(if (initial == null) "Add Category" else "Edit Category", style = MaterialTheme.typography.titleLarge) },
    containerColor = MaterialTheme.colorScheme.surface,
    titleContentColor = MaterialTheme.colorScheme.onSurface,
    textContentColor = MaterialTheme.colorScheme.onSurface,
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
        Text(
          "SAVE",
          color = if (name.trim().isNotEmpty()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
          fontWeight = FontWeight.Bold
        )
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("CANCEL", color = MaterialTheme.colorScheme.onSurfaceVariant)
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
    title = { Text(if (initial == null) "Add Product" else "Edit Product", style = MaterialTheme.typography.titleLarge) },
    containerColor = MaterialTheme.colorScheme.surface,
    titleContentColor = MaterialTheme.colorScheme.onSurface,
    textContentColor = MaterialTheme.colorScheme.onSurface,
    text = {
      Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
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
            modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable),
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }
          )
          
          ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(MaterialTheme.colorScheme.surface)
          ) {
            categories.forEach { cat ->
              DropdownMenuItem(
                text = { Text(cat.name, color = MaterialTheme.colorScheme.onSurface) },
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
      val isValid = categoryId != 0L && name.trim().isNotEmpty() && priceText.toDoubleOrNull() != null && stockText.toDoubleOrNull() != null
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
        enabled = isValid
      ) {
        Text(
          "SAVE",
          color = if (isValid) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
          fontWeight = FontWeight.Bold
        )
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("CANCEL", color = MaterialTheme.colorScheme.onSurfaceVariant)
      }
    }
  )
}


