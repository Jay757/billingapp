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
import com.aslibill.ui.components.GlassButton
import com.aslibill.ui.components.PremiumSegmentedControl
import com.aslibill.ui.components.AsliTextField
import com.aslibill.ui.theme.AsliColors
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
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

  var showQtyFor by remember { mutableStateOf<CartLine?>(null) }
  var showSearch by remember { mutableStateOf(false) }
  var showSave by remember { mutableStateOf(false) }
  var saveDraft by remember { mutableStateOf(SaveBillDraft()) }
  var saveError by remember { mutableStateOf<String?>(null) }
  var printMessage by remember { mutableStateOf<String?>(null) }
  var isSaveAndPrint by remember { mutableStateOf(false) }

  ScreenSurface {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(contentPadding)
        .padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp)
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
              color = AsliColors.TextPrimary, 
              style = (if (isWide) MaterialTheme.typography.displaySmall else MaterialTheme.typography.headlineMedium).copy(
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = (-1).sp
              )
            )
            Text("Select items to add to bill", color = AsliColors.TextSecondary, style = MaterialTheme.typography.bodySmall)
          }
          Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            GlassButton(if (isWide) "View Reports" else "Report", onClick = { onGoReport?.invoke() })
            OrangeButton(if (isWide) "Quick Billing" else "Quick", onClick = { onGoQuickBill?.invoke() })
          }
        }
      }

      DarkCard(modifier = Modifier.fillMaxWidth().weight(1f), alpha = 0.95f) {
        Column(modifier = Modifier.fillMaxSize()) {
          // Integrated Header
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .background(AsliColors.Card2.copy(alpha = 0.6f))
              .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.Start
          ) {
            Text("ITEM", color = AsliColors.Primary, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), modifier = Modifier.weight(1.4f))
            Text("QTY", color = AsliColors.Primary, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), modifier = Modifier.weight(0.5f))
            Text("RATE", color = AsliColors.Primary, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), modifier = Modifier.weight(0.7f))
            Text("TOTAL", color = AsliColors.Primary, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), modifier = Modifier.weight(0.7f), textAlign = androidx.compose.ui.text.style.TextAlign.End)
            Spacer(modifier = Modifier.width(48.dp))
          }
          
          HorizontalDivider(thickness = 1.dp, color = AsliColors.Primary.copy(alpha = 0.2f))

          if (cart.isEmpty()) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
              Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Outlined.ShoppingCart, contentDescription = null, tint = AsliColors.TextSecondary, modifier = Modifier.size(48.dp))
                Text("Cart is empty", color = AsliColors.TextSecondary, style = MaterialTheme.typography.bodyMedium)
              }
            }
          } else {
            Column(
              modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
              verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
              cart.forEachIndexed { index, line ->
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .clickable { showQtyFor = line }
                  .padding(horizontal = 12.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(line.name, color = AsliColors.TextPrimary, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold), modifier = Modifier.weight(1.4f))
                Text(line.qty.toInt().toString(), color = AsliColors.TextPrimary, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(0.5f))
                Text("₹${line.rate.toInt()}", color = AsliColors.TextSecondary, style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(0.7f))
                Text("₹${line.total.toInt()}", color = AsliColors.TextPrimary, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.ExtraBold), modifier = Modifier.weight(0.7f), textAlign = androidx.compose.ui.text.style.TextAlign.End)
                Box(modifier = Modifier.width(48.dp), contentAlignment = Alignment.Center) {
                  IconButton(onClick = { vm.setQty(line.productId, 0.0) }, modifier = Modifier.size(36.dp)) {
                    Icon(Icons.Outlined.Delete, contentDescription = "Delete", tint = AsliColors.Red.copy(alpha = 0.8f), modifier = Modifier.size(20.dp))
                  }
                }
              }
              if (index < cart.size - 1) {
                HorizontalDivider(
                  modifier = Modifier.padding(horizontal = 8.dp),
                  thickness = 0.5.dp,
                  color = AsliColors.Primary.copy(alpha = 0.1f)
                )
              }
            }
          }
        }
      }
    }

      DarkCard(modifier = Modifier.fillMaxWidth(), alpha = 0.9f) {
        Row(
          modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text("Total Items", color = AsliColors.TextSecondary, style = MaterialTheme.typography.labelSmall)
            Text(cart.sumOf { it.qty }.toInt().toString(), color = AsliColors.TextPrimary, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold))
          }
          Column(horizontalAlignment = Alignment.End) {
            Text("Grand Total", color = AsliColors.TextSecondary, style = MaterialTheme.typography.labelSmall)
            Text("₹ ${subtotal.toInt()}", color = AsliColors.Primary, style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Black))
          }
        }
      }

      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        GlassButton("Clear", onClick = vm::clearCart, modifier = Modifier.weight(1f))
        GlassButton("Search", onClick = { showSearch = true }, modifier = Modifier.weight(1.2f))
        OrangeButton("Save", onClick = { isSaveAndPrint = false; showSave = true }, modifier = Modifier.weight(1f))
        OrangeButton("Save & Print", onClick = { isSaveAndPrint = true; showSave = true }, modifier = Modifier.weight(1.6f))
      }

      if (!printMessage.isNullOrBlank()) {
          Text(printMessage!!, color = AsliColors.Primary, style = MaterialTheme.typography.labelMedium)
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
        columns = GridCells.Adaptive(minSize = 160.dp),
        modifier = Modifier
          .fillMaxWidth()
          .height(240.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
      ) {
        items(products, key = { it.id }) { p ->
          DarkCard(modifier = Modifier.fillMaxWidth().clickable { vm.addProduct(p) }, alpha = 0.8f) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
              Text(
                p.name, 
                color = AsliColors.TextPrimary, 
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
              )
              Box(
                modifier = Modifier
                  .background(AsliColors.PrimaryLight.copy(alpha = 0.6f), RoundedCornerShape(6.dp))
                  .padding(horizontal = 6.dp, vertical = 2.dp)
              ) {
                Text("₹${p.price.toInt()}", color = AsliColors.Primary, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.ExtraBold))
              }
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

