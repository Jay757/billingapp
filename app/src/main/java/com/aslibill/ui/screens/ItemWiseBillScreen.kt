package com.aslibill.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import com.aslibill.ui.theme.AsliColors
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

  var showQtyFor by remember { mutableStateOf<CartLine?>(null) }
  var showSearch by remember { mutableStateOf(false) }
  var showSave by remember { mutableStateOf(false) }
  var saveDraft by remember { mutableStateOf(SaveBillDraft()) }
  var saveError by remember { mutableStateOf<String?>(null) }
  var printMessage by remember { mutableStateOf<String?>(null) }

  ScreenSurface {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(contentPadding)
        .padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text("Item Wise Bill", color = AsliColors.TextPrimary, style = MaterialTheme.typography.titleLarge)
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
          OrangeButton("Report", onClick = { onGoReport?.invoke() })
          GrayButton("Quick Bill", onClick = { onGoQuickBill?.invoke() })
        }
      }

      Row(
        modifier = Modifier
          .fillMaxWidth()
          .background(AsliColors.Orange, RoundedCornerShape(8.dp))
          .padding(horizontal = 10.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Text("ITEM", color = Color.Black, modifier = Modifier.weight(1.6f))
        Text("QTY", color = Color.Black, modifier = Modifier.weight(0.6f))
        Text("RATE", color = Color.Black, modifier = Modifier.weight(0.8f))
        Text("TOTAL", color = Color.Black, modifier = Modifier.weight(0.8f))
      }

      DarkCard(modifier = Modifier.fillMaxWidth().weight(1f)) {
        if (cart.isEmpty()) {
          Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No items", color = AsliColors.TextSecondary)
          }
        } else {
          Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            cart.forEach { line ->
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .clickable { showQtyFor = line }
                  .padding(vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(line.name, color = AsliColors.TextPrimary, modifier = Modifier.weight(1.6f))
                Text(line.qty.toInt().toString(), color = AsliColors.TextPrimary, modifier = Modifier.weight(0.6f))
                Text("₹${line.rate.toInt()}", color = AsliColors.TextPrimary, modifier = Modifier.weight(0.8f))
                Text("₹${line.total.toInt()}", color = AsliColors.TextPrimary, modifier = Modifier.weight(0.8f))
              }
            }
          }
        }
      }

      DarkCard(modifier = Modifier.fillMaxWidth()) {
        Row(
          modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text(cart.sumOf { it.qty }.toInt().toString(), color = AsliColors.TextPrimary)
          Text("₹ ${subtotal.toInt()}", color = AsliColors.TextPrimary)
        }
      }

      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        GrayButton("Clear", onClick = vm::clearCart, modifier = Modifier.weight(1f))
        GrayButton("Add Product", onClick = { showSearch = true }, modifier = Modifier.weight(1.3f))
        GrayButton("Print", onClick = {
            if (cart.isEmpty()) {
                printMessage = "Cart is empty"
                return@GrayButton
            }
            printMessage = "Printing..."
            val billRow = com.aslibill.data.db.BillWithItemsRow(
                billId = 0,
                createdAtEpochMs = System.currentTimeMillis(),
                cashierName = "user",
                subtotal = subtotal,
                tax = 0.0,
                total = subtotal,
                paymentMethod = "CASH",
                itemCount = cart.size
            )
            val items = cart.map {
                com.aslibill.data.db.BillItemEntity(
                    billId = 0,
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
        }, modifier = Modifier.weight(1f))
        OrangeButton("Save", onClick = { showSave = true }, modifier = Modifier.weight(1f))
        IconButton(onClick = { showSearch = true }) {
          Icon(Icons.Outlined.Search, contentDescription = "Search", tint = AsliColors.Orange)
        }
      }

      if (!printMessage.isNullOrBlank()) {
          Text(printMessage!!, color = AsliColors.TextSecondary)
      }

      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
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
        columns = GridCells.Fixed(2),
        modifier = Modifier
          .fillMaxWidth()
          .height(220.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        items(products, key = { it.id }) { p ->
          DarkCard(modifier = Modifier.fillMaxWidth().clickable { vm.addProduct(p) }) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
              Text(p.name, color = AsliColors.TextPrimary, style = MaterialTheme.typography.titleMedium)
              Text("₹${p.price.toInt()}", color = AsliColors.Orange, style = MaterialTheme.typography.titleSmall)
            }
          }
        }
      }
    }

    if (showQtyFor != null) {
      val line = showQtyFor!!
      var qtyText by remember(line) { mutableStateOf(line.qty.toInt().toString()) }
      AlertDialog(
        onDismissRequest = { showQtyFor = null },
        title = { Text("Qty - ${line.name}") },
        text = {
          OutlinedTextField(
            value = qtyText,
            onValueChange = { qtyText = it },
            label = { Text("Quantity") },
            singleLine = true
          )
        },
        confirmButton = {
          TextButton(
            onClick = {
              val q = qtyText.toDoubleOrNull() ?: 0.0
              vm.setQty(line.productId, q)
              showQtyFor = null
            }
          ) { Text("OK") }
        },
        dismissButton = { TextButton(onClick = { showQtyFor = null }) { Text("CANCEL") } }
      )
    }

    if (showSearch) {
      AlertDialog(
        onDismissRequest = { showSearch = false },
        title = { Text("Search Product") },
        text = {
          Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedTextField(
              value = searchQuery,
              onValueChange = vm::setSearchQuery,
              label = { Text("Search") },
              singleLine = true
            )
            DarkCard(modifier = Modifier.fillMaxWidth()) {
              Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                products.take(8).forEach { p ->
                  Row(
                    modifier = Modifier
                      .fillMaxWidth()
                      .clickable { vm.addProduct(p) }
                      .padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                  ) {
                    Text(p.name, color = AsliColors.TextPrimary)
                    Text("₹${p.price.toInt()}", color = AsliColors.Orange)
                  }
                }
              }
            }
          }
        },
        confirmButton = { TextButton(onClick = { showSearch = false; vm.setSearchQuery("") }) { Text("DONE") } }
      )
    }

    if (showSave) {
      SaveBillDialog(
        subtotal = subtotal,
        draft = saveDraft,
        onDraftChange = { saveDraft = it },
        onDismiss = { showSave = false; saveError = null },
        onSave = {
          vm.saveBill(
            draft = saveDraft,
            cashierName = "user",
            onSaved = { showSave = false; saveError = null },
            onError = { saveError = it.message ?: "Error" }
          )
        },
        errorText = saveError
      )
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
  val df = remember { SimpleDateFormat("dd-MM-yyyy hh:mm a", Locale.getDefault()) }
  val now = remember { df.format(Date()) }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text("Save Bill") },
    text = {
      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        OutlinedTextField(value = "", onValueChange = {}, label = { Text("Select Customer...") }, singleLine = true, enabled = false)
        OutlinedTextField(
          value = if (draft.discountPercent == 0.0) "" else draft.discountPercent.toString(),
          onValueChange = { onDraftChange(draft.copy(discountPercent = it.toDoubleOrNull() ?: 0.0)) },
          label = { Text("% DISCOUNT") },
          singleLine = true
        )
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .background(AsliColors.Card2, RoundedCornerShape(10.dp))
            .padding(10.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text("Include GST", color = AsliColors.TextPrimary)
          TextButton(onClick = { onDraftChange(draft.copy(includeGst = !draft.includeGst)) }) {
            Text(if (draft.includeGst) "ON" else "OFF", color = AsliColors.Orange)
          }
        }

        DarkCard(modifier = Modifier.fillMaxWidth()) {
          Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
              Text("Total", color = AsliColors.TextSecondary)
              Text("₹ ${subtotal.toInt()}", color = AsliColors.TextPrimary)
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
              Text("Sub Total", color = AsliColors.TextSecondary)
              Text("₹ ${subtotal.toInt()}", color = AsliColors.TextPrimary)
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
              Text("Grand Total", color = AsliColors.TextPrimary)
              Text("₹ ${subtotal.toInt()}", color = AsliColors.Orange)
            }
          }
        }

        Text("Payment Mode :", color = AsliColors.TextSecondary)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          PaymentChip("NONE", draft.paymentMode == PaymentMode.NONE) { onDraftChange(draft.copy(paymentMode = PaymentMode.NONE)) }
          PaymentChip("CASH", draft.paymentMode == PaymentMode.CASH) { onDraftChange(draft.copy(paymentMode = PaymentMode.CASH)) }
          PaymentChip("ONLINE", draft.paymentMode == PaymentMode.ONLINE) { onDraftChange(draft.copy(paymentMode = PaymentMode.ONLINE)) }
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          PaymentChip("CREDIT", draft.paymentMode == PaymentMode.CREDIT) { onDraftChange(draft.copy(paymentMode = PaymentMode.CREDIT)) }
          PaymentChip("SPLIT", draft.paymentMode == PaymentMode.SPLIT) { onDraftChange(draft.copy(paymentMode = PaymentMode.SPLIT)) }
        }

        OutlinedTextField(
          value = draft.note,
          onValueChange = { onDraftChange(draft.copy(note = it)) },
          label = { Text("Note") },
          singleLine = true
        )
        Text(now, color = AsliColors.TextSecondary)
        if (!errorText.isNullOrBlank()) {
          Text(errorText, color = AsliColors.Red)
        }
      }
    },
    confirmButton = { TextButton(onClick = onSave, enabled = subtotal > 0) { Text("SAVE") } },
    dismissButton = { TextButton(onClick = onDismiss) { Text("CANCEL") } }
  )
}

@Composable
private fun PaymentChip(text: String, selected: Boolean, onClick: () -> Unit) {
  Chip(text = text, selected = selected, onClick = onClick)
}

