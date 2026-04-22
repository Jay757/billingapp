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
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.Stroke
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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Outline

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
            onSave = { categoryId, items ->
                vm.addProducts(categoryId, items)
                showAdd = false
                editProduct = null
            },
            onUpdate = { draft ->
              vm.updateProduct(draft.id!!, draft.categoryId, draft.name, draft.price, draft.stock, isActive = true)
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

private data class ProductItemDraft(
    val id: Long? = null,
    val name: String = "",
    val price: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProductDialog(
  categories: List<CategoryEntity>,
  initial: ProductDraft?,
  onDismiss: () -> Unit,
  onSave: (Long, List<Pair<String, Double>>) -> Unit,
  onUpdate: (ProductDraft) -> Unit
) {
  var categoryId by remember(initial, categories) {
    mutableStateOf(initial?.categoryId ?: categories.firstOrNull()?.id ?: 0L)
  }
  
  // Use a state list for multiple products
  val productItems = remember { 
    mutableStateListOf<ProductItemDraft>().apply {
        if (initial != null) {
            add(ProductItemDraft(id = initial.id, name = initial.name, price = initial.price.toString()))
        } else {
            add(ProductItemDraft())
        }
    }
  }

  var expanded by remember { mutableStateOf(false) }
  val selectedCategory = categories.find { it.id == categoryId }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = { 
        Text(
            if (initial == null) "Add Product" else "Edit Product", 
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black)
        ) 
    },
    containerColor = Color(0xFF1A1C1E), // Match dark theme in image
    titleContentColor = Color.White,
    textContentColor = Color.White,
    text = {
      Column(
          modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
          verticalArrangement = Arrangement.spacedBy(16.dp)
      ) {
        // Dropdown for Category
        Text("Category", style = MaterialTheme.typography.labelMedium, color = Color.Gray)
        ExposedDropdownMenuBox(
          expanded = expanded,
          onExpandedChange = { expanded = !expanded },
          modifier = Modifier.fillMaxWidth()
        ) {
          OutlinedTextField(
            value = selectedCategory?.name ?: "Select Category",
            onValueChange = {},
            readOnly = true,
            modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable),
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f),
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedTextColor = Color.White,
                focusedTextColor = Color.White
            ),
            shape = RoundedCornerShape(12.dp)
          )
          
          ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(Color(0xFF2A2D31))
          ) {
            categories.forEach { cat ->
              DropdownMenuItem(
                text = { Text(cat.name, color = Color.White) },
                onClick = {
                  categoryId = cat.id
                  expanded = false
                }
              )
            }
          }
        }

        Text("Products", style = MaterialTheme.typography.labelMedium, color = Color.Gray)

        productItems.forEachIndexed { index, item ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = item.name,
                    onValueChange = { productItems[index] = item.copy(name = it) },
                    label = { Text("Product Name", fontSize = 12.sp) },
                    modifier = Modifier.weight(2f),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f),
                        unfocusedTextColor = Color.White,
                        focusedTextColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
                OutlinedTextField(
                    value = item.price,
                    onValueChange = { productItems[index] = item.copy(price = it) },
                    label = { Text("Price", fontSize = 12.sp) },
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f),
                        unfocusedTextColor = Color.White,
                        focusedTextColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
                    singleLine = true
                )
                
                if (initial == null && productItems.size > 1) {
                    IconButton(onClick = { productItems.removeAt(index) }) {
                        Icon(Icons.Outlined.Delete, contentDescription = "Remove", tint = AsliColors.Red)
                    }
                } else if (initial == null && index == 0) {
                     // Empty space to align
                     Spacer(Modifier.width(40.dp))
                } else if (initial != null) {
                    // No delete for edit in this view
                    Spacer(Modifier.width(40.dp))
                } else {
                     IconButton(onClick = { productItems.removeAt(index) }) {
                        Icon(Icons.Outlined.Delete, contentDescription = "Remove", tint = AsliColors.Red)
                    }
                }
            }
        }

        if (initial == null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .dashedBorder(Color.Gray.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                    .clickable { productItems.add(ProductItemDraft()) },
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.Add, contentDescription = null, tint = AsliColors.PrimaryBlue, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Add Product", color = AsliColors.PrimaryBlue, fontWeight = FontWeight.Bold)
                }
            }
        }
      }
    },
    confirmButton = {
      val isValid = categoryId != 0L && productItems.all { it.name.isNotBlank() && it.price.toDoubleOrNull() != null }
      TextButton(
        onClick = {
            if (initial != null) {
                val item = productItems.first()
                onUpdate(initial.copy(
                    name = item.name,
                    price = item.price.toDoubleOrNull() ?: 0.0
                ))
            } else {
                val items = productItems.map { it.name to (it.price.toDoubleOrNull() ?: 0.0) }
                onSave(categoryId, items)
            }
        },
        enabled = isValid
      ) {
        Text(
          "SAVE",
          color = if (isValid) AsliColors.PrimaryBlue else Color.Gray,
          fontWeight = FontWeight.Bold
        )
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("CANCEL", color = Color.Gray)
      }
    }
  )
}

// Helper for dashed border
fun Modifier.dashedBorder(color: Color, shape: Shape): Modifier = this.drawBehind {
    val stroke = Stroke(width = 2f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f))
    val outline = shape.createOutline(size, layoutDirection, this)
    when (outline) {
        is Outline.Rectangle -> {
            drawRect(color = color, style = stroke)
        }
        is Outline.Rounded -> {
            drawRoundRect(
                color = color, 
                cornerRadius = CornerRadius(outline.roundRect.topLeftCornerRadius.x),
                style = stroke
            )
        }
        is Outline.Generic -> {
            drawPath(path = outline.path, color = color, style = stroke)
        }
    }
}
