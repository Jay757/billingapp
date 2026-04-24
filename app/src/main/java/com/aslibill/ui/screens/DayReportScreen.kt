package com.aslibill.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aslibill.data.db.DayReportRow
import com.aslibill.ui.components.DarkCard
import com.aslibill.ui.components.DateBox
import com.aslibill.ui.components.ScreenSurface
import com.aslibill.ui.components.SectionHeader
import com.aslibill.ui.components.openDatePicker
import com.aslibill.ui.components.UnifiedDateRangeSelector
import com.aslibill.ui.theme.AsliColors
import com.aslibill.ui.theme.AppTypography
import com.aslibill.ui.theme.AppSpacing
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
  val fromText = remember(filters.fromEpochMs) { df.format(Date(filters.fromEpochMs ?: System.currentTimeMillis())) }
  val toText = remember(filters.toEpochMs) { df.format(Date(filters.toEpochMs ?: System.currentTimeMillis())) }

  val isLoading by vm.isLoading.collectAsState()
  
  ScreenSurface {
    Box(modifier = Modifier.fillMaxSize()) {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(contentPadding)
          .padding(AppSpacing.md),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.md)
      ) {
        Column {
          Text(
            "Day Report", 
            color = MaterialTheme.colorScheme.onBackground, 
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Black)
          )
          Text(
            "Track daily sales totals and bill counts", 
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

      if (isLoading) {
        com.aslibill.ui.components.AsliLoader()
      }
    }
  }
}

@Composable
private fun DayReportCard(row: DayReportRow) {
  DarkCard(modifier = Modifier.fillMaxWidth()) {
    Column(modifier = Modifier.padding(AppSpacing.lg), verticalArrangement = Arrangement.spacedBy(AppSpacing.md)) {
      Row(
        modifier = Modifier.fillMaxWidth(), 
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          row.dateLabel, 
          color = AsliColors.PrimaryBlue, 
          style = AppTypography.h3
        )
        Box(
          modifier = Modifier
            .background(AsliColors.PrimaryBlue.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
          Text(
            "${row.billCount} Bill(s)", 
            color = AsliColors.PrimaryBlue, 
            style = AppTypography.labelCaps
          )
        }
      }
      
      Row(
        modifier = Modifier.fillMaxWidth(), 
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
      ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
          Text("Cash: ₹ ${String.format("%.0f", row.cashTotal)}", color = AsliColors.TextSecondary, style = AppTypography.bodyMedium)
          Text("Online: ₹ ${String.format("%.0f", row.onlineTotal)}", color = AsliColors.TextSecondary, style = AppTypography.bodyMedium)
        }
        Column(horizontalAlignment = Alignment.End) {
          Text("TOTAL AMOUNT", color = AsliColors.TextSecondary, style = AppTypography.labelCaps)
          Text(
            "₹ ${String.format("%.0f", row.grandTotal)}", 
            color = AsliColors.SuccessGreen, 
            style = AppTypography.h2
          )
        }
      }
    }
  }
}
