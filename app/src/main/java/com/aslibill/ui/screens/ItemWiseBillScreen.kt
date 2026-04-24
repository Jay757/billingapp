package com.aslibill.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.aslibill.ui.components.Chip
import com.aslibill.ui.components.DarkCard
import com.aslibill.ui.components.GrayButton
import com.aslibill.ui.components.OrangeButton
import com.aslibill.ui.components.ScreenSurface
import com.aslibill.ui.components.GlassButton
import com.aslibill.ui.components.AsliTextField
import com.aslibill.ui.components.AsliTable
import com.aslibill.ui.components.BillingTotalCard
import com.aslibill.ui.theme.AsliColors
import com.aslibill.ui.theme.AppTypography
import com.aslibill.ui.theme.AppSpacing
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.luminance
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ItemWiseBillScreen(
  contentPadding: PaddingValues,
  vm: ItemWiseBillViewModel,
  btVm: BluetoothPrinterViewModel,
  onGoReport: (() -> Unit)? = null,
  onGoQuickBill: (() -> Unit)? = null
) {
  val categories by vm.categories.collectAsState()
  val cart by vm.cart.collectAsState()
  val subtotal by vm.cartSubtotal.collectAsState()
  val products by vm.filteredProducts.collectAsState()
  val selectedCategoryId by vm.selectedCategoryId.collectAsState()
  val searchQuery by vm.searchQuery.collectAsState()
  val isLoading by vm.isLoading.collectAsState()

  var showQtyFor by remember { mutableStateOf<CartLine?>(null) }
  var showSearch by remember { mutableStateOf(false) }
  var showSave by remember { mutableStateOf(false) }
  var saveDraft by remember { mutableStateOf(SaveBillDraft()) }
  var saveError by remember { mutableStateOf<String?>(null) }
  var printMessage by remember { mutableStateOf<String?>(null) }
  var isSaveAndPrint by remember { mutableStateOf(false) }

  ScreenSurface {
    Box(modifier = Modifier.fillMaxSize()) {
      Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(contentPadding)
        .padding(AppSpacing.lg),
      verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)
    ) {
      BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        val isWide = maxWidth > 600.dp
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(
              "Item Wise Bill", 
              color = MaterialTheme.colorScheme.onBackground, 
              style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Black)
            )
            Text(
              "Select items to add to bill", 
              color = MaterialTheme.colorScheme.onSurfaceVariant, 
              style = MaterialTheme.typography.bodyMedium
            )
          }

          Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(
              onClick = { showSearch = true },
              modifier = Modifier
                .size(40.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(20.dp))
            ) {
              Icon(Icons.Outlined.Search, contentDescription = "Search", tint = MaterialTheme.colorScheme.primary)
            }
            OrangeButton(if (isWide) "Quick Billing" else "Quick", onClick = { onGoQuickBill?.invoke() })
          }
        }
      }

      AsliTable(
        headers = listOf("SR", "DETAILS", "QTY", "RATE", "TOTAL"),
        columnWeights = listOf(0.3f, 1.5f, 0.5f, 0.6f, 1.0f),
        isEmpty = cart.isEmpty(),
        emptyIcon = Icons.Outlined.ShoppingCart,
        emptyText = "Cart is empty",
        modifier = Modifier.fillMaxWidth().weight(1f)
      ) {
        val isDark = MaterialTheme.colorScheme.surface.luminance() < 0.5f
        cart.forEachIndexed { index, line ->
          CartRowItem(
            sr = index + 1,
            line = line,
            isDark = isDark,
            onClick = { showQtyFor = line },
            onRemove = { vm.setQty(line.productId, 0.0) }
          )
          if (!isDark) HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), thickness = 0.5.dp, color = Color.Gray.copy(alpha = 0.2f))
        }
      }

      BillingTotalCard(
        totalItems = cart.sumOf { it.qty }.toInt(),
        grandTotal = subtotal
      )

      Row(
        modifier = Modifier.fillMaxWidth(), 
        horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm)
      ) {
        OrangeButton("Clear", onClick = vm::clearCart, modifier = Modifier.weight(1f))
        OrangeButton("Save", onClick = { isSaveAndPrint = false; showSave = true }, modifier = Modifier.weight(1f))
        OrangeButton("Save & Print", onClick = { isSaveAndPrint = true; showSave = true }, modifier = Modifier.weight(1.3f))
      }

      if (!printMessage.isNullOrBlank()) {
          Text(printMessage!!, color = AsliColors.Primary, style = MaterialTheme.typography.labelMedium)
      }

      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm)
      ) {
        Chip(text = "ALL", selected = selectedCategoryId == null, onClick = vm::selectAllCategories)
        categories.forEach { cat ->
          Chip(
            text = cat.name,
            selected = selectedCategoryId == cat.id,
            onClick = { vm.selectCategory(cat.id) }
          )
        }
      }

      LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 160.dp),
        modifier = Modifier
          .fillMaxWidth()
          .weight(0.7f),
        horizontalArrangement = Arrangement.spacedBy(AppSpacing.md),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.md)
      ) {
        items(products, key = { it.id }) { p ->
          DarkCard(modifier = Modifier.fillMaxWidth().clickable { vm.addProduct(p) }, alpha = 0.8f) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
              Text(
                p.name, 
                color = MaterialTheme.colorScheme.onSurface, 
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
              )
              Box(
                modifier = Modifier
                  .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f), RoundedCornerShape(6.dp))
                  .padding(horizontal = 8.dp, vertical = 4.dp)
              ) {
                Text("₹${p.price.toInt()}", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
              }
            }
          }
        }
      }
    }

    if (showQtyFor != null) {
      val line = showQtyFor!!
      var qtyText by remember(line) { mutableStateOf(line.qty.toInt().toString()) }
      val isValid = qtyText.toDoubleOrNull() != null

      com.aslibill.ui.components.AsliDialog(
        onDismissRequest = { showQtyFor = null },
        title = "Qty - ${line.name}",
        confirmButton = {
          androidx.compose.material3.Button(
            onClick = {
              val q = qtyText.toDoubleOrNull() ?: 0.0
              vm.setQty(line.productId, q)
              showQtyFor = null
            },
            enabled = isValid,
            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
              containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
              disabledContainerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f)
            ),
            shape = RoundedCornerShape(14.dp),
            contentPadding = PaddingValues(horizontal = 28.dp, vertical = 14.dp)
          ) {
            Text(
              "OK",
              color = if (isValid) AsliColors.PrimaryBlue else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
              fontWeight = FontWeight.Black,
              style = MaterialTheme.typography.labelLarge
            )
          }
        },
        dismissButton = {
          TextButton(onClick = { showQtyFor = null }) {
            Text(
              "CANCEL",
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
            )
          }
        }
      ) {
        AsliTextField(
          value = qtyText,
          onValueChange = { qtyText = it },
          label = "Quantity",
          keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number)
        )
      }
    }

    if (showSearch) {
      com.aslibill.ui.components.AsliDialog(
        onDismissRequest = { showSearch = false },
        title = "Search Product",
        confirmButton = {
          androidx.compose.material3.Button(
            onClick = { showSearch = false; vm.setSearchQuery("") },
            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
              containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
            ),
            shape = RoundedCornerShape(14.dp),
            contentPadding = PaddingValues(horizontal = 28.dp, vertical = 14.dp)
          ) {
            Text(
              "DONE",
              color = AsliColors.PrimaryBlue,
              fontWeight = FontWeight.Black,
              style = MaterialTheme.typography.labelLarge
            )
          }
        }
      ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
          AsliTextField(
            value = searchQuery,
            onValueChange = vm::setSearchQuery,
            label = "Search by name..."
          )
          DarkCard(modifier = Modifier.fillMaxWidth(), alpha = 0.9f) {
            Column(modifier = Modifier.padding(8.dp)) {
              products.take(8).forEach { p ->
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .clickable { vm.addProduct(p) }
                    .padding(vertical = 6.dp),
                  horizontalArrangement = Arrangement.SpaceBetween
                ) {
                  Text(p.name, color = MaterialTheme.colorScheme.onSurface)
                  Text("₹${p.price.toInt()}", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                }
              }
            }
          }
        }
      }
    }

    if (showSave) {
      SaveBillDialog(
        subtotal = subtotal,
        draft = saveDraft,
        onDraftChange = { saveDraft = it },
        onDismiss = { showSave = false; saveError = null },
        onSave = {
          val cartSnapshot = cart.toList()
          val subtotalSnapshot = subtotal
          val discount = subtotalSnapshot * (saveDraft.discountPercent.coerceIn(0.0, 100.0) / 100.0)
          val discounted = (subtotalSnapshot - discount).coerceAtLeast(0.0)
          vm.saveBill(
            draft = saveDraft,
            cashierName = "user",
            onSaved = { billId -> 
              showSave = false; saveError = null 
              if (isSaveAndPrint) {
                  printMessage = "Printing saved bill..."
                  val billRow = com.aslibill.data.db.BillWithItemsRow(
                      billId = billId,
                      createdAtEpochMs = System.currentTimeMillis(),
                      cashierName = "user",
                      subtotal = discounted,
                      tax = 0.0,
                      total = discounted,
                      paymentMethod = saveDraft.paymentMode.name,
                      itemCount = cartSnapshot.size
                  )
                  val items = cartSnapshot.map {
                      com.aslibill.data.db.BillItemEntity(
                          billId = billId,
                          productId = it.productId,
                          productNameSnapshot = it.name,
                          qty = it.qty,
                          rate = it.rate,
                          lineTotal = it.total
                      )
                  }
                  btVm.printBill(billRow, items) { ok, err ->
                      printMessage = if (ok) "Print sent" else (err ?: "Print failed")
                  }
              }
            },
            onError = { saveError = it.message ?: "Error" }
          )
        },
        errorText = saveError
      )
    }

    if (isLoading) {
      com.aslibill.ui.components.AsliLoader()
    }
    }
  }
}

@Composable
private fun CartRowItem(
    sr: Int,
    line: CartLine,
    isDark: Boolean,
    onClick: () -> Unit,
    onRemove: () -> Unit
) {
    val onSurface = MaterialTheme.colorScheme.onSurface
    val onSurfaceVariant = MaterialTheme.colorScheme.onSurfaceVariant
    val primary = MaterialTheme.colorScheme.primary
    val surfaceVariant = MaterialTheme.colorScheme.surfaceVariant
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(if (isDark) surfaceVariant.copy(alpha = 0.3f) else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(sr.toString(), color = onSurfaceVariant, style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(0.3f))
        Text(line.name, color = onSurface, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Black), modifier = Modifier.weight(1.5f))
        Text(line.qty.toInt().toString(), color = onSurface, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), modifier = Modifier.weight(0.5f), textAlign = androidx.compose.ui.text.style.TextAlign.Center)
        Text("₹${line.rate.toInt()}", color = onSurfaceVariant, style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(0.6f), textAlign = androidx.compose.ui.text.style.TextAlign.Center)
        
        Row(modifier = Modifier.weight(1.0f), horizontalArrangement = Arrangement.End, verticalAlignment = Alignment.CenterVertically) {
            Text("₹${line.total.toInt()}", color = primary, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Black))
            Spacer(Modifier.width(4.dp))
            IconButton(onClick = onRemove, modifier = Modifier.size(28.dp)) {
                Icon(Icons.Outlined.Delete, contentDescription = "Delete", tint = AsliColors.Red.copy(alpha = 0.8f), modifier = Modifier.size(16.dp))
            }
        }
    }
}

@Composable
private fun SaveBillDialog(
  subtotal: Double,
  draft: SaveBillDraft,
  onDraftChange: (SaveBillDraft) -> Unit,
  onDismiss: () -> Unit,
  onSave: () -> Unit,
  errorText: String?
) {
  val df = remember { SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()) }
  val now = remember { df.format(Date()) }

  val isValid = subtotal > 0

  com.aslibill.ui.components.AsliDialog(
    onDismissRequest = onDismiss,
    title = "Save Bill",
    confirmButton = {
      androidx.compose.material3.Button(
        onClick = onSave,
        enabled = isValid,
        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
          containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
          disabledContainerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f)
        ),
        shape = RoundedCornerShape(14.dp),
        contentPadding = PaddingValues(horizontal = 28.dp, vertical = 14.dp)
      ) {
        Text(
          "SAVE",
          color = if (isValid) AsliColors.PrimaryBlue else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
          fontWeight = FontWeight.Black,
          style = MaterialTheme.typography.labelLarge
        )
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text(
          "CANCEL",
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
        )
      }
    }
  ) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
      AsliTextField(value = "", onValueChange = {}, label = "Select Customer...", enabled = false)
      AsliTextField(
        value = if (draft.discountPercent == 0.0) "" else draft.discountPercent.toString(),
        onValueChange = { onDraftChange(draft.copy(discountPercent = it.toDoubleOrNull() ?: 0.0)) },
        label = "% DISCOUNT",
        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number)
      )
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f), RoundedCornerShape(12.dp))
          .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text("Include GST", color = MaterialTheme.colorScheme.onSurface, style = MaterialTheme.typography.bodyLarge)
        TextButton(onClick = { onDraftChange(draft.copy(includeGst = !draft.includeGst)) }) {
          Text(if (draft.includeGst) "ON" else "OFF", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
        }
      }

      DarkCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Total", color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodyMedium)
            Text("₹ ${subtotal.toInt()}", color = MaterialTheme.colorScheme.onSurface, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold))
          }
          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            val discounted = subtotal * (1 - draft.discountPercent / 100)
            Text("Grand Total", color = MaterialTheme.colorScheme.onSurface, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold))
            Text("₹ ${discounted.toInt()}", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black))
          }
        }
      }

      Text("Payment Mode :", color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.labelSmall)
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          PaymentChip("NONE", draft.paymentMode == PaymentMode.NONE) { onDraftChange(draft.copy(paymentMode = PaymentMode.NONE)) }
          PaymentChip("CASH", draft.paymentMode == PaymentMode.CASH) { onDraftChange(draft.copy(paymentMode = PaymentMode.CASH)) }
          PaymentChip("ONLINE", draft.paymentMode == PaymentMode.ONLINE) { onDraftChange(draft.copy(paymentMode = PaymentMode.ONLINE)) }
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          PaymentChip("CREDIT", draft.paymentMode == PaymentMode.CREDIT) { onDraftChange(draft.copy(paymentMode = PaymentMode.CREDIT)) }
          PaymentChip("SPLIT", draft.paymentMode == PaymentMode.SPLIT) { onDraftChange(draft.copy(paymentMode = PaymentMode.SPLIT)) }
        }
      }

      AsliTextField(
        value = draft.note,
        onValueChange = { onDraftChange(draft.copy(note = it)) },
        label = "Note"
      )
      Text(now, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.labelSmall)
      if (!errorText.isNullOrBlank()) {
        Text(errorText, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
      }
    }
  }
}


@Composable
private fun PaymentChip(text: String, selected: Boolean, onClick: () -> Unit) {
  Chip(text = text, selected = selected, onClick = onClick)
}

