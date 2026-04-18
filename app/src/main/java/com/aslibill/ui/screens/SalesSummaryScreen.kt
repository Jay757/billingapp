package com.aslibill.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.AttachMoney
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.aslibill.ui.components.DarkCard
import com.aslibill.ui.components.DateBox
import com.aslibill.ui.components.ScreenSurface
import com.aslibill.ui.components.SectionHeader
import com.aslibill.ui.components.StatsCard
import com.aslibill.ui.components.openDatePicker
import com.aslibill.ui.theme.AppSpacing
import com.aslibill.ui.theme.AppTypography
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
        .padding(horizontal = AppSpacing.md)
        .verticalScroll(rememberScrollState()),
      verticalArrangement = Arrangement.spacedBy(AppSpacing.md)
    ) {
      Text(
        "Sales Summary",
        color = AsliColors.TextPrimary,
        style = AppTypography.h2,
        modifier = Modifier.padding(top = AppSpacing.md)
      )

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(AppSpacing.md)
      ) {
        DateBox(
          label = "FROM",
          date = fromText,
          onClick = { openDatePicker(context, filters.fromEpochMs) { vm.setFrom(it) } },
          modifier = Modifier.weight(1f)
        )
        DateBox(
          label = "TO",
          date = toText,
          onClick = { openDatePicker(context, filters.toEpochMs) { vm.setTo(it) } },
          modifier = Modifier.weight(1f)
        )
      }

      SectionHeader("REVENUE OVERVIEW")
      
      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(AppSpacing.md)) {
        StatsCard(
          label = "TOTAL BILLS",
          value = summary.totalBills.toString(),
          icon = Icons.Outlined.ReceiptLong,
          color = AsliColors.PrimaryBlue,
          modifier = Modifier.weight(1f)
        )
        StatsCard(
          label = "REVENUE",
          value = "₹${summary.totalRevenue.toInt()}",
          icon = Icons.Outlined.AttachMoney,
          color = AsliColors.SuccessGreen,
          modifier = Modifier.weight(1f)
        )
      }
      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(AppSpacing.md)) {
        StatsCard(
          label = "CASH",
          value = "₹${summary.cashTotal.toInt()}",
          icon = Icons.Outlined.Payments,
          color = AsliColors.AlertOrange,
          modifier = Modifier.weight(1f)
        )
        StatsCard(
          label = "ONLINE",
          value = "₹${summary.onlineTotal.toInt()}",
          icon = Icons.Outlined.AccountBalance,
          color = Color(0xFF8B5CF6), // Purple for online
          modifier = Modifier.weight(1f)
        )
      }

      SectionHeader("TOP SELLING ITEMS")
      DarkCard(modifier = Modifier.fillMaxWidth()) {
        Column(
          modifier = Modifier.padding(AppSpacing.lg),
          verticalArrangement = Arrangement.spacedBy(AppSpacing.md)
        ) {
          if (summary.topItems.isEmpty()) {
            Text(
              "No data available",
              color = AsliColors.TextSecondary,
              style = AppTypography.bodyMedium
            )
          } else {
            summary.topItems.forEachIndexed { index, item ->
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Text(
                  "${index + 1}. ${item.productName.uppercase()}",
                  color = AsliColors.TextPrimary,
                  style = AppTypography.bodyBold,
                  modifier = Modifier.weight(1f)
                )
                Surface(
                  color = AsliColors.PrimaryBlue.copy(alpha = 0.1f),
                  shape = RoundedCornerShape(4.dp)
                ) {
                  Text(
                    "₹${item.totalRevenue.toInt()}",
                    color = AsliColors.PrimaryBlue,
                    style = AppTypography.bodyBold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                    fontWeight = FontWeight.Black
                  )
                }
              }
            }
          }
        }
      }
      Spacer(Modifier.height(AppSpacing.md))
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
