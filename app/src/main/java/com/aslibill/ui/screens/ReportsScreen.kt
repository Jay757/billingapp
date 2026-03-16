package com.aslibill.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.FilterAlt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.aslibill.data.db.BillItemEntity
import com.aslibill.data.db.BillWithItemsRow
import com.aslibill.ui.components.Chip
import com.aslibill.ui.components.DarkCard
import com.aslibill.ui.components.GrayButton
import com.aslibill.ui.components.OrangeButton
import com.aslibill.ui.components.ScreenSurface
import com.aslibill.ui.theme.AsliColors
import com.aslibill.ui.screens.BluetoothPrinterViewModel
import com.aslibill.ui.components.DateBox
import com.aslibill.ui.components.openDatePicker
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ReportsScreen(
  contentPadding: PaddingValues,
  vm: ReportsViewModel,
  btVm: BluetoothPrinterViewModel,
  onGoQuickBill: (() -> Unit)? = null,
  onGoItemWise: (() -> Unit)? = null
) {
  val filters by vm.filters.collectAsState()
  val bills by vm.bills.collectAsState()
  val totalAmount by vm.totalAmount.collectAsState()

  var showItemsFor by remember { mutableStateOf<BillWithItemsRow?>(null) }
  var items by remember { mutableStateOf<List<BillItemEntity>>(emptyList()) }
  var loadErr by remember { mutableStateOf<String?>(null) }

  var printMessage by remember { mutableStateOf<String?>(null) }

  var showDeleteAll by remember { mutableStateOf(false) }

  val context = LocalContext.current
  val dfDay = remember { SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()) }
  val dfTime = remember { SimpleDateFormat("dd-MM-yyyy hh:mm:ss a", Locale.getDefault()) }
  val fromText = remember(filters.fromEpochMs) { dfDay.format(Date(filters.fromEpochMs)) }
  val toText = remember(filters.toEpochMs) { dfDay.format(Date(filters.toEpochMs)) }

  ScreenSurface {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(contentPadding)
        .padding(12.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text("Report", color = AsliColors.TextPrimary, style = MaterialTheme.typography.titleLarge)
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
          OrangeButton("Quick Bill", onClick = { onGoQuickBill?.invoke() })
          GrayButton("Item Wise Bill", onClick = { onGoItemWise?.invoke() })
        }
      }

      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        DateBox(
          label = "FROM",
          value = fromText,
          onClick = {
            openDatePicker(context, filters.fromEpochMs) { vm.setFrom(it) }
          },
          modifier = Modifier.weight(1f)
        )
        DateBox(
          label = "TO",
          value = toText,
          onClick = {
            openDatePicker(context, filters.toEpochMs) { vm.setTo(it) }
          },
          modifier = Modifier.weight(1f)
        )
        IconButton(onClick = { /* TODO filter */ }) {
          Icon(Icons.Outlined.FilterAlt, contentDescription = "Filter", tint = AsliColors.TextSecondary)
        }
      }

      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text("Total record : ${bills.size}", color = AsliColors.TextSecondary)
        Text(
          if (bills.isEmpty()) "0 - 0" else "1 - ${bills.size}",
          color = AsliColors.TextSecondary
        )
      }

      LazyColumn(
        modifier = Modifier.weight(1f),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        items(bills, key = { it.billId }) { row ->
          BillCard(
            row = row,
            dateTime = dfTime.format(Date(row.createdAtEpochMs)),
            onDelete = { vm.deleteBill(row.billId) },
            onView = {
              showItemsFor = row
              items = emptyList()
              loadErr = null
              vm.loadItems(
                billId = row.billId,
                onLoaded = { items = it },
                onError = { loadErr = it.message ?: "Failed to load" }
              )
            },
            onPrint = {
              printMessage = null
              vm.loadItems(
                billId = row.billId,
                onLoaded = { loadedItems ->
                  btVm.printBill(
                    bill = row,
                    items = loadedItems,
                    onDone = { ok, err ->
                      printMessage = if (ok) "Print sent to printer" else (err ?: "Print failed")
                    }
                  )
                },
                onError = { t ->
                  printMessage = t.message ?: "Failed to load items for print"
                }
              )
            }
          )
        }
      }

      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text("Total Amount : ₹ ${totalAmount.toInt()}", color = AsliColors.TextPrimary)
        GrayButton("Delete All", onClick = { showDeleteAll = true })
      }

      if (!printMessage.isNullOrBlank()) {
        Text(printMessage!!, color = AsliColors.TextSecondary)
      }
    }

    if (showDeleteAll) {
      AlertDialog(
        onDismissRequest = { showDeleteAll = false },
        title = { Text("Delete All Bills") },
        text = { Text("This will delete all bills permanently.") },
        confirmButton = {
          TextButton(onClick = { vm.deleteAll(); showDeleteAll = false }) { Text("DELETE") }
        },
        dismissButton = { TextButton(onClick = { showDeleteAll = false }) { Text("CANCEL") } }
      )
    }

    if (showItemsFor != null) {
      val row = showItemsFor!!
      AlertDialog(
        onDismissRequest = { showItemsFor = null },
        title = { Text("Invoice : ${row.billId}") },
        text = {
          Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            if (!loadErr.isNullOrBlank()) Text(loadErr!!, color = AsliColors.Red)
            if (items.isEmpty() && loadErr.isNullOrBlank()) {
              Text("Loading...", color = AsliColors.TextSecondary)
            } else {
              DarkCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                  items.forEach {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                      Text(it.productNameSnapshot, color = AsliColors.TextPrimary)
                      Text("${it.qty.toInt()} x ₹${it.rate.toInt()} = ₹${it.lineTotal.toInt()}", color = AsliColors.TextSecondary)
                    }
                  }
                }
              }
              Text("Total: ₹ ${row.total.toInt()}", color = AsliColors.Orange)
            }
          }
        },
        confirmButton = { TextButton(onClick = { showItemsFor = null }) { Text("CLOSE") } }
      )
    }
  }
}@Composable
private fun BillCard(
  row: BillWithItemsRow,
  dateTime: String,
  onDelete: () -> Unit,
  onView: () -> Unit,
  onPrint: () -> Unit
) {
  val paymentBg = when (row.paymentMethod.uppercase(Locale.getDefault())) {
    "CASH" -> AsliColors.Orange
    "NONE" -> AsliColors.Card2
    else -> AsliColors.Green
  }
  val cardBg = if (row.billId % 3L == 1L) Color(0xFF0B4F1E) else AsliColors.Card

  DarkCard(modifier = Modifier.fillMaxWidth()) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .background(cardBg, RoundedCornerShape(14.dp))
    ) {
      IconButton(onClick = onDelete, modifier = Modifier.align(Alignment.TopEnd)) {
        Icon(Icons.Outlined.Delete, contentDescription = "Delete", tint = AsliColors.Red)
      }
      Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
          Column {
            Text("Invoice : ${row.billId}", color = AsliColors.TextPrimary)
            Text("Date : $dateTime", color = AsliColors.TextSecondary)
            Text("Qty : ${row.itemCount}", color = AsliColors.TextSecondary)
          }
          Column(horizontalAlignment = Alignment.End, modifier = Modifier.padding(end = 24.dp)) {
            Text("Biller : ${row.cashierName ?: "user"}", color = AsliColors.TextSecondary)
            Box(
              modifier = Modifier
                .background(paymentBg, RoundedCornerShape(8.dp))
                .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
              Text(row.paymentMethod, color = Color.Black)
            }
            Text("Total : ${row.total}", color = AsliColors.TextPrimary)
          }
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
          Chip("EDIT", selected = false, onClick = { /* TODO */ })
          Chip("VIEW", selected = false, onClick = onView)
          Chip("SHARE", selected = false, onClick = { /* TODO */ })
          Chip("PRINT", selected = false, onClick = onPrint)
        }
      }
    }
  }
}
