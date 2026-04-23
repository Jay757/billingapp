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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.aslibill.data.db.CreditSummaryRow
import com.aslibill.ui.components.DarkCard
import com.aslibill.ui.components.ScreenSurface
import com.aslibill.ui.components.SectionHeader
import com.aslibill.ui.components.StatsCard
import com.aslibill.ui.theme.AppSpacing
import com.aslibill.ui.theme.AppTypography
import com.aslibill.ui.theme.AsliColors

@Composable
fun CreditDetailsScreen(
  contentPadding: PaddingValues,
  vm: CreditDetailsViewModel
) {
  val credits by vm.credits.collectAsState()
  val totalOutstanding by vm.totalOutstanding.collectAsState()

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
        Text(
          "Credit Details",
          color = MaterialTheme.colorScheme.onBackground,
          style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Black),
          modifier = Modifier.padding(top = AppSpacing.md)
        )


        // Summary Stats
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(AppSpacing.md)
        ) {
          StatsCard(
            label = "OUTSTANDING",
            value = "₹${String.format("%.0f", totalOutstanding)}",
            icon = Icons.Outlined.AccountBalanceWallet,
            color = AsliColors.AlertOrange,
            modifier = Modifier.weight(1f)
          )
          StatsCard(
            label = "CUSTOMERS",
            value = "${credits.size}",
            icon = Icons.Outlined.Groups,
            color = AsliColors.PrimaryBlue,
            modifier = Modifier.weight(1f)
          )
        }

        SectionHeader("CREDIT BY CUSTOMER")

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

      if (isLoading) {
        com.aslibill.ui.components.AsliLoader()
      }
    }
  }
}

@Composable
private fun CreditCard(row: CreditSummaryRow) {
  DarkCard(modifier = Modifier.fillMaxWidth()) {
    Row(
      modifier = Modifier
        .padding(AppSpacing.lg)
        .fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column(modifier = Modifier.weight(1f)) {
        Text(
          row.customerName.uppercase(),
          color = MaterialTheme.colorScheme.onSurface,
          style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Black)
        )
        Text(
          row.customerMobile,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          style = MaterialTheme.typography.bodySmall,
          modifier = Modifier.padding(top = 2.dp)
        )
        Text(
          "${row.billCount} BILLS",
          color = MaterialTheme.colorScheme.primary,
          style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
          modifier = Modifier.padding(top = 4.dp)
        )

      }
      
      Column(horizontalAlignment = Alignment.End) {
        Surface(
          color = AsliColors.AlertOrange.copy(alpha = 0.1f),
          shape = RoundedCornerShape(4.dp)
        ) {
          Text(
            "₹${String.format("%.2f", row.totalCredit)}",
            color = AsliColors.AlertOrange,
            style = AppTypography.bodyBold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            fontWeight = FontWeight.Black
          )
        }
      }
    }
  }
}
