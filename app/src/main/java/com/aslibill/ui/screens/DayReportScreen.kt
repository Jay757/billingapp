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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.aslibill.data.db.DayReportRow
import com.aslibill.ui.components.DarkCard
import com.aslibill.ui.components.DateBox
import com.aslibill.ui.components.ScreenSurface
import com.aslibill.ui.components.SectionHeader
import com.aslibill.ui.components.openDatePicker
import com.aslibill.ui.theme.AppSpacing
import com.aslibill.ui.theme.AsliColors
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DayReportScreen(
  contentPadding: PaddingValues,
  vm: DayReportViewModel
) {
  val rows by vm.rows.collectAsState()
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
        .padding(AppSpacing.md),
      verticalArrangement = Arrangement.spacedBy(AppSpacing.md)
    ) {
      Text("Day Report", color = AsliColors.TextPrimary, style = MaterialTheme.typography.titleLarge)

      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm)) {
        DateBox(label = "FROM", value = fromText, onClick = { openDatePicker(context, filters.fromEpochMs) { vm.setFrom(it) } }, modifier = Modifier.weight(1f))
        DateBox(label = "TO", value = toText, onClick = { openDatePicker(context, filters.toEpochMs) { vm.setTo(it) } }, modifier = Modifier.weight(1f))
      }

      SectionHeader("Daily Totals")

      if (rows.isEmpty()) {
        Box(modifier = Modifier.fillMaxWidth().padding(AppSpacing.xl), contentAlignment = Alignment.Center) {
          Text("No data for selected period.", color = AsliColors.TextSecondary)
        }
      } else {
        LazyColumn(
          modifier = Modifier.weight(1f),
          verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)
        ) {
          items(rows) { row -> DayReportCard(row = row) }
        }
      }
    }
  }
}

@Composable
private fun DayReportCard(row: DayReportRow) {
  DarkCard(modifier = Modifier.fillMaxWidth()) {
    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(row.dateLabel, color = AsliColors.Orange, style = MaterialTheme.typography.titleMedium)
        Text("${row.billCount} Bill(s)", color = AsliColors.TextSecondary, style = MaterialTheme.typography.labelLarge)
      }
      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Column {
          Text("Cash: ₹ ${String.format("%.2f", row.cashTotal)}", color = AsliColors.TextPrimary, style = MaterialTheme.typography.bodyMedium)
          Text("Online: ₹ ${String.format("%.2f", row.onlineTotal)}", color = AsliColors.TextPrimary, style = MaterialTheme.typography.bodyMedium)
        }
        Text("Total: ₹ ${String.format("%.2f", row.grandTotal)}", color = AsliColors.Green, style = MaterialTheme.typography.titleLarge)
      }
    }
  }
}
