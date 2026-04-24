package com.aslibill.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.aslibill.data.db.ItemSalesRow
import com.aslibill.ui.components.DarkCard
import com.aslibill.ui.components.DateBox
import com.aslibill.ui.components.ScreenSurface
import com.aslibill.ui.components.SectionHeader
import com.aslibill.ui.components.openDatePicker
import com.aslibill.ui.components.UnifiedDateRangeSelector
import com.aslibill.ui.theme.AppSpacing
import com.aslibill.ui.theme.AppTypography
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
  val fromText = remember(filters.fromEpochMs) { df.format(Date(filters.fromEpochMs ?: System.currentTimeMillis())) }
  val toText = remember(filters.toEpochMs) { df.format(Date(filters.toEpochMs ?: System.currentTimeMillis())) }

  val isLoading by vm.isLoading.collectAsState()
  
  ScreenSurface {
    Box(modifier = Modifier.fillMaxSize()) {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(contentPadding)
          .padding(horizontal = AppSpacing.md),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.md)
      ) {
        Column(modifier = Modifier.padding(top = AppSpacing.md)) {
          Text(
            "Item Sales Report",
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Black)
          )
          Text(
            "Analysis of sales performance by product",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium
          )
        }

        UnifiedDateRangeSelector(
          fromText = fromText,
          toText = toText,
          onFromClick = { openDatePicker(context, filters.fromEpochMs ?: System.currentTimeMillis()) { vm.setFrom(it) } },
          onToClick = { openDatePicker(context, filters.toEpochMs ?: System.currentTimeMillis()) { vm.setTo(it) } }
        )

        SectionHeader("SALES BREAKDOWN")

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

      if (isLoading) {
        com.aslibill.ui.components.AsliLoader()
      }
    }
  }
}

@Composable
private fun ItemSaleCard(row: ItemSalesRow) {
  DarkCard(modifier = Modifier.fillMaxWidth()) {
    Row(
      modifier = Modifier
        .padding(16.dp)
        .fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column(modifier = Modifier.weight(1f)) {
        Text(
          row.productName.uppercase(),
          color = AsliColors.TextPrimary,
          style = AppTypography.bodyBold,
          fontWeight = FontWeight.Black
        )
        Text(
          "QTY SOLD: ${row.totalQty.toInt()}",
          color = AsliColors.PrimaryBlue,
          style = AppTypography.labelCaps,
          modifier = Modifier.padding(top = 4.dp)
        )
      }
      
      Surface(
        color = AsliColors.SuccessGreen.copy(alpha = 0.1f),
        shape = RoundedCornerShape(4.dp)
      ) {
        Text(
          "₹${String.format("%.2f", row.totalRevenue)}",
          color = AsliColors.SuccessGreen,
          style = AppTypography.bodyBold,
          modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
          fontWeight = FontWeight.Black
        )
      }
    }
  }
}
