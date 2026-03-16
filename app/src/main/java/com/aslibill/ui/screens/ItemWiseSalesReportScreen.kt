package com.aslibill.ui.screens

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.aslibill.data.db.ItemSalesRow
import com.aslibill.ui.components.DarkCard
import com.aslibill.ui.components.DateBox
import com.aslibill.ui.components.ScreenSurface
import com.aslibill.ui.components.SectionHeader
import com.aslibill.ui.components.openDatePicker
import com.aslibill.ui.theme.AsliColors
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ItemWiseSalesReportScreen(
  contentPadding: PaddingValues,
  vm: ItemWiseSalesReportViewModel
) {
  val items by vm.items.collectAsState()
  val filters by vm.filters.collectAsState()
  val context = LocalContext.current

  val df = remember { SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()) }
  val fromText = remember(filters.fromEpochMs) { df.format(Date(filters.fromEpochMs)) }
  val toText = remember(filters.toEpochMs) { df.format(Date(filters.toEpochMs)) }

  ScreenSurface {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(contentPadding)
        .padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      Text("Item Wise Sales Report", color = AsliColors.TextPrimary, style = MaterialTheme.typography.titleLarge)

      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        DateBox(label = "FROM", value = fromText, onClick = { openDatePicker(context, filters.fromEpochMs) { vm.setFrom(it) } }, modifier = Modifier.weight(1f))
        DateBox(label = "TO", value = toText, onClick = { openDatePicker(context, filters.toEpochMs) { vm.setTo(it) } }, modifier = Modifier.weight(1f))
      }

      SectionHeader("Sales Breakdown")

      if (items.isEmpty()) {
        Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
          Text("No sales for selected period.", color = AsliColors.TextSecondary)
        }
      } else {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          items(items) { row -> ItemSaleCard(row = row) }
        }
      }
    }
  }
}

@Composable
private fun ItemSaleCard(row: ItemSalesRow) {
  DarkCard(modifier = Modifier.fillMaxWidth()) {
    Row(
      modifier = Modifier.padding(14.dp).fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(row.productName, color = AsliColors.TextPrimary, style = MaterialTheme.typography.bodyLarge)
        Text("Qty Sold: ${row.totalQty.toInt()}", color = AsliColors.TextSecondary, style = MaterialTheme.typography.labelMedium)
      }
      Text("₹ ${String.format("%.2f", row.totalRevenue)}", color = AsliColors.Orange, style = MaterialTheme.typography.titleMedium)
    }
  }
}
