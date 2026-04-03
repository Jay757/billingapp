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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.aslibill.data.db.CreditSummaryRow
import com.aslibill.ui.components.DarkCard
import com.aslibill.ui.components.ScreenSurface
import com.aslibill.ui.components.SectionHeader
import com.aslibill.ui.theme.AppSpacing
import com.aslibill.ui.theme.AsliColors

@Composable
fun CreditDetailsScreen(
  contentPadding: PaddingValues,
  vm: CreditDetailsViewModel
) {
  val credits by vm.credits.collectAsState()
  val totalOutstanding by vm.totalOutstanding.collectAsState()

  ScreenSurface {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(contentPadding)
        .padding(AppSpacing.md),
      verticalArrangement = Arrangement.spacedBy(AppSpacing.md)
    ) {
      Text("Credit Details", color = AsliColors.TextPrimary, style = MaterialTheme.typography.titleLarge)

      // Summary card
      DarkCard(modifier = Modifier.fillMaxWidth()) {
        Row(
          modifier = Modifier.fillMaxWidth().padding(16.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text("Total Outstanding", color = AsliColors.TextSecondary, style = MaterialTheme.typography.labelMedium)
            Text("₹ ${String.format("%.2f", totalOutstanding)}", color = AsliColors.Orange, style = MaterialTheme.typography.headlineSmall)
          }
          Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text("Customers", color = AsliColors.TextSecondary, style = MaterialTheme.typography.labelMedium)
            Text("${credits.size}", color = AsliColors.TextPrimary, style = MaterialTheme.typography.headlineSmall)
          }
        }
      }

      SectionHeader("Credit by Customer")

      if (credits.isEmpty()) {
        Box(modifier = Modifier.fillMaxWidth().padding(AppSpacing.xl), contentAlignment = Alignment.Center) {
          Text("No credit bills found. Create a bill with CREDIT payment to see it here.", color = AsliColors.TextSecondary)
        }
      } else {
        LazyColumn(
          modifier = Modifier.weight(1f),
          verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)
        ) {
          items(credits) { row -> CreditCard(row = row) }
        }
      }
    }
  }
}

@Composable
private fun CreditCard(row: CreditSummaryRow) {
  DarkCard(modifier = Modifier.fillMaxWidth()) {
    Row(
      modifier = Modifier.padding(14.dp).fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(row.customerName, color = AsliColors.TextPrimary, style = MaterialTheme.typography.bodyLarge)
        Text(row.customerMobile, color = AsliColors.TextSecondary, style = MaterialTheme.typography.labelMedium)
        Text("${row.billCount} bill(s)", color = AsliColors.TextSecondary, style = MaterialTheme.typography.labelSmall)
      }
      Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Box(
          modifier = Modifier
            .background(AsliColors.Red.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
          Text("CREDIT", color = AsliColors.Red, style = MaterialTheme.typography.labelSmall)
        }
        Text("₹ ${String.format("%.2f", row.totalCredit)}", color = AsliColors.Orange, style = MaterialTheme.typography.bodyLarge)
      }
    }
  }
}
