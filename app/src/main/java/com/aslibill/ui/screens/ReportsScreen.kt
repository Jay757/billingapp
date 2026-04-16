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
import androidx.compose.material.icons.outlined.Print
import androidx.compose.material.icons.outlined.Receipt
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import com.aslibill.ui.components.StatsCard
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
import com.aslibill.ui.theme.AppSpacing
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
        .padding(AppSpacing.md),
      verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text("Reports", color = AsliColors.TextPrimary, style = MaterialTheme.typography.headlineSmall.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.Bold))
          Text("Track your sales and performance", color = AsliColors.TextSecondary, style = MaterialTheme.typography.bodySmall)
        }
        Icon(Icons.Outlined.FilterAlt, contentDescription = "Filter", tint = AsliColors.Primary)
      }

      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm)) {
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

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        StatsCard(
          label = "Total Sales",
          value = "₹ ${totalAmount.toInt()}",
          icon = Icons.Outlined.CalendarMonth,
          color = AsliColors.Primary,
          modifier = Modifier.weight(1.5f)
        )
        StatsCard(
          label = "Record count",
          value = "${bills.size}",
          icon = Icons.Outlined.Receipt,
          color = AsliColors.Orange,
          modifier = Modifier.weight(1f)
        )
      }

      LazyColumn(
        modifier = Modifier.weight(1f),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)
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

      if (bills.isNotEmpty()) {
        GrayButton(
          "Clear All Reports",
          onClick = { showDeleteAll = true },
          modifier = Modifier.fillMaxWidth()
        )
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
}

@Composable
private fun BillCard(
  row: BillWithItemsRow,
  dateTime: String,
  onDelete: () -> Unit,
  onView: () -> Unit,
  onPrint: () -> Unit
) {
  val paymentColor = when (row.paymentMethod.uppercase(Locale.getDefault())) {
    "CASH" -> AsliColors.Primary
    "UP" -> AsliColors.Green
    else -> AsliColors.Orange
  }

  DarkCard(
    modifier = Modifier
      .fillMaxWidth()
      .clickable(onClick = onView)
  ) {
    Column(
      modifier = Modifier.padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            "Invoice #${row.billId}",
            color = AsliColors.TextPrimary,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
          )
          Text(
            dateTime,
            color = AsliColors.TextSecondary,
            style = MaterialTheme.typography.bodySmall
          )
        }
        
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(paymentColor.copy(alpha = 0.15f))
            .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
          Text(
            row.paymentMethod,
            color = paymentColor,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
          )
        }
      }

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
      ) {
        Column {
          Text(
            "Items: ${row.itemCount}",
            color = AsliColors.TextSecondary,
            style = MaterialTheme.typography.bodySmall
          )
          Text(
            "₹${row.total.toInt()}",
            color = AsliColors.TextPrimary,
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.ExtraBold)
          )
        }
        
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          IconButton(
            onClick = onPrint, 
            modifier = Modifier.size(36.dp).background(AsliColors.Card2, CircleShape)
          ) {
            Icon(Icons.Outlined.Print, contentDescription = "Print", tint = AsliColors.TextPrimary, modifier = Modifier.size(18.dp))
          }
          IconButton(
            onClick = onDelete, 
            modifier = Modifier.size(36.dp).background(AsliColors.Card2, CircleShape)
          ) {
            Icon(Icons.Outlined.Delete, contentDescription = "Delete", tint = AsliColors.Orange, modifier = Modifier.size(18.dp))
          }
        }
      }
    }
  }
}
