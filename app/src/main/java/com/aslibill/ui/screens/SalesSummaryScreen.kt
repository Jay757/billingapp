package com.aslibill.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
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
fun SalesSummaryScreen(
  contentPadding: PaddingValues,
  vm: SalesSummaryViewModel
) {
  val summary by vm.summary.collectAsState()
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
        .padding(16.dp)
        .verticalScroll(rememberScrollState()),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      Text("Sales Summary", color = AsliColors.TextPrimary, style = MaterialTheme.typography.titleLarge)

      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        DateBox(label = "FROM", value = fromText, onClick = { openDatePicker(context, filters.fromEpochMs) { vm.setFrom(it) } }, modifier = Modifier.weight(1f))
        DateBox(label = "TO", value = toText, onClick = { openDatePicker(context, filters.toEpochMs) { vm.setTo(it) } }, modifier = Modifier.weight(1f))
      }

      SectionHeader("Revenue Overview")
      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        SummaryCard(label = "Total Bills", value = summary.totalBills.toString(), modifier = Modifier.weight(1f))
        SummaryCard(label = "Total Revenue", value = "₹ ${summary.totalRevenue.toInt()}", valueColor = AsliColors.Orange, modifier = Modifier.weight(1f))
      }
      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        SummaryCard(label = "Cash Total", value = "₹ ${summary.cashTotal.toInt()}", valueColor = AsliColors.Green, modifier = Modifier.weight(1f))
        SummaryCard(label = "Online Total", value = "₹ ${summary.onlineTotal.toInt()}", valueColor = AsliColors.Card2, modifier = Modifier.weight(1f))
      }

      SectionHeader("Top Selling Items")
      DarkCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
          if (summary.topItems.isEmpty()) {
            Text("No data available", color = AsliColors.TextSecondary)
          } else {
            summary.topItems.forEachIndexed { index, item ->
              Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("${index + 1}. ${item.productName}", color = Color.White, modifier = Modifier.weight(1f))
                Text("₹ ${item.totalRevenue.toInt()}", color = AsliColors.Orange)
              }
            }
          }
        }
      }
    }
  }
}

@Composable
private fun SummaryCard(label: String, value: String, valueColor: Color = Color.White, modifier: Modifier = Modifier) {
  DarkCard(modifier = modifier) {
    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
      Text(label, color = AsliColors.TextSecondary, style = MaterialTheme.typography.labelSmall)
      Text(value, color = valueColor, style = MaterialTheme.typography.titleMedium)
    }
  }
}
