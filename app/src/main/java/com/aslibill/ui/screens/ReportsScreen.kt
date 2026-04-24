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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
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
import androidx.compose.material3.Surface
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.Alignment
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.aslibill.ui.theme.AppTypography
import com.aslibill.ui.theme.AppSpacing
import com.aslibill.ui.screens.BluetoothPrinterViewModel
import com.aslibill.ui.components.DateBox
import com.aslibill.ui.components.openDatePicker
import com.aslibill.ui.components.UnifiedDateRangeSelector
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
  val isLoading by vm.isLoading.collectAsState()

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
    Box(modifier = Modifier.fillMaxSize()) {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(contentPadding)
          .padding(AppSpacing.md),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)
      ) {
      // Header
      Row(
        modifier = Modifier.fillMaxWidth().padding(bottom = AppSpacing.sm),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            "Reports",
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Black)
          )
          Text(
            "Track your sales and performance",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium
          )
        }

        Box(
          modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceVariant),
          contentAlignment = Alignment.Center
        ) {
          Icon(Icons.Outlined.FilterAlt, contentDescription = "Filter", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(22.dp))
        }

      }

      // Unified Date Range Selector
      UnifiedDateRangeSelector(
        fromText = fromText,
        toText = toText,
        onFromClick = { openDatePicker(context, filters.fromEpochMs) { vm.setFrom(it) } },
        onToClick = { openDatePicker(context, filters.toEpochMs) { vm.setTo(it) } }
      )

      // High-Contrast Sales Stats
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(AppSpacing.md)
      ) {
        StatsCard(
          label = "Total Sales",
          value = "₹ ${totalAmount.toInt()}",
          icon = Icons.Outlined.CalendarMonth,
          color = AsliColors.SuccessGreen,
          modifier = Modifier.weight(1.3f)
        )
        StatsCard(
          label = "Records",
          value = "${bills.size}",
          icon = Icons.Outlined.Receipt,
          color = MaterialTheme.colorScheme.primary,
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
            dateTime = dfDay.format(Date(row.createdAtEpochMs)),
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
      com.aslibill.ui.components.AsliDialog(
        onDismissRequest = { showDeleteAll = false },
        title = "Delete All Bills",
        confirmButton = {
          androidx.compose.material3.Button(
            onClick = { vm.deleteAll(); showDeleteAll = false },
            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
              containerColor = AsliColors.Red.copy(alpha = 0.1f)
            ),
            shape = RoundedCornerShape(14.dp),
            contentPadding = PaddingValues(horizontal = 28.dp, vertical = 14.dp)
          ) {
            Text(
              "DELETE ALL",
              color = AsliColors.Red,
              fontWeight = FontWeight.Black,
              style = MaterialTheme.typography.labelLarge
            )
          }
        },
        dismissButton = {
          TextButton(onClick = { showDeleteAll = false }) {
            Text(
              "CANCEL",
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
            )
          }
        }
      ) {
        Text(
          "This will permanently delete all bills and cannot be undone.",
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          style = MaterialTheme.typography.bodyMedium
        )
      }
    }

    if (showItemsFor != null) {
      val row = showItemsFor!!
      com.aslibill.ui.components.AsliDialog(
        onDismissRequest = { showItemsFor = null },
        title = "Invoice #${row.billId}",
        confirmButton = {
          androidx.compose.material3.Button(
            onClick = { showItemsFor = null },
            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
              containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
            ),
            shape = RoundedCornerShape(14.dp),
            contentPadding = PaddingValues(horizontal = 28.dp, vertical = 14.dp)
          ) {
            Text(
              "CLOSE",
              color = AsliColors.PrimaryBlue,
              fontWeight = FontWeight.Black,
              style = MaterialTheme.typography.labelLarge
            )
          }
        }
      ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          if (!loadErr.isNullOrBlank()) {
            Text(loadErr!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
          }
          if (items.isEmpty() && loadErr.isNullOrBlank()) {
            Text("Loading...", color = MaterialTheme.colorScheme.onSurfaceVariant)
          } else {
            DarkCard(modifier = Modifier.fillMaxWidth()) {
              Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                items.forEach {
                  Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(it.productNameSnapshot, color = MaterialTheme.colorScheme.onSurface, style = MaterialTheme.typography.bodyMedium)
                    Text("${it.qty.toInt()} x ₹${it.rate.toInt()} = ₹${it.lineTotal.toInt()}", color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall)
                  }
                }
              }
            }
            Text(
              "Total: ₹ ${row.total.toInt()}",
              color = MaterialTheme.colorScheme.primary,
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
          }
        }
      }
    }

    if (isLoading) {
      com.aslibill.ui.components.AsliLoader()
    }
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
    "CASH" -> AsliColors.PrimaryBlue
    "ONLINE" -> AsliColors.SuccessGreen
    "UPI" -> AsliColors.SuccessGreen
    "UP" -> AsliColors.SuccessGreen
    "CREDIT" -> AsliColors.AlertOrange
    else -> AsliColors.TextSecondary
  }

  DarkCard(
    modifier = Modifier
      .fillMaxWidth()
      .clickable(onClick = onView),
    alpha = 0.8f
  ) {
    Column(
      modifier = Modifier.padding(AppSpacing.lg),
      verticalArrangement = Arrangement.spacedBy(AppSpacing.lg)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            "Invoice #${row.billId}",
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black)
          )
          Spacer(Modifier.height(4.dp))
          Text(
            dateTime,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodySmall
          )
        }
        
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(paymentColor.copy(alpha = 0.1f))
            .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
          Text(
            row.paymentMethod.uppercase(Locale.getDefault()),
            color = paymentColor,
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
          )
        }
      }


      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            "ITEMS: ${row.itemCount}",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
          )
          Text(
            "₹${row.total.toInt()}",
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Black)
          )
        }
        
        Row(horizontalArrangement = Arrangement.spacedBy(AppSpacing.md)) {
          Box(
            modifier = Modifier
              .size(44.dp)
              .clip(CircleShape)
              .background(MaterialTheme.colorScheme.surfaceVariant)
              .clickable(onClick = onPrint),
            contentAlignment = Alignment.Center
          ) {
            Icon(Icons.Outlined.Print, contentDescription = "Print", tint = MaterialTheme.colorScheme.onSurface, modifier = Modifier.size(20.dp))
          }
          Box(
            modifier = Modifier
              .size(44.dp)
              .clip(CircleShape)
              .background(AsliColors.Red.copy(alpha = 0.1f))
              .clickable(onClick = onDelete),
            contentAlignment = Alignment.Center
          ) {
            Icon(Icons.Outlined.Delete, contentDescription = "Delete", tint = AsliColors.Red, modifier = Modifier.size(20.dp))
          }
        }
      }

    }
  }
}

