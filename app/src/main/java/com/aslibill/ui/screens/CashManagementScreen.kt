package com.aslibill.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.aslibill.data.db.CashTransactionEntity
import com.aslibill.ui.components.DarkCard
import com.aslibill.ui.components.OrangeButton
import com.aslibill.ui.components.ScreenSurface
import com.aslibill.ui.components.SectionHeader
import com.aslibill.ui.components.StatsCard
import com.aslibill.ui.theme.AppSpacing
import com.aslibill.ui.theme.AppTypography
import com.aslibill.ui.theme.AsliColors
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CashManagementScreen(
  contentPadding: PaddingValues,
  vm: CashManagementViewModel
) {
  val transactions by vm.transactions.collectAsState()
  val balance by vm.balance.collectAsState()
  val isLoading by vm.isLoading.collectAsState()
  var showAddDialog by remember { mutableStateOf<String?>(null) } // "IN" or "OUT"

  val df = remember { SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()) }

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
        "Cash Management",
        color = MaterialTheme.colorScheme.onBackground,
        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Black),
        modifier = Modifier.padding(top = AppSpacing.md)
      )


      // Balance Card
      StatsCard(
        label = "CURRENT BALANCE",
        value = "₹${String.format("%.2f", balance)}",
        icon = Icons.Outlined.AccountBalanceWallet,
        color = AsliColors.PrimaryBlue,
        modifier = Modifier.fillMaxWidth()
      )

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(AppSpacing.md)
      ) {
        OrangeButton(
          text = "CASH IN",
          icon = Icons.Default.ArrowUpward,
          onClick = { showAddDialog = "IN" },
          modifier = Modifier.weight(1f)
        )
        OrangeButton(
          text = "CASH OUT",
          icon = Icons.Default.ArrowDownward,
          onClick = { showAddDialog = "OUT" },
          modifier = Modifier.weight(1f)
        )
      }

      SectionHeader("RECENT TRANSACTIONS")

      if (transactions.isEmpty()) {
        Box(modifier = Modifier.fillMaxWidth().padding(AppSpacing.xl), contentAlignment = Alignment.Center) {
          Text("No cash transactions yet.", color = AsliColors.TextSecondary)
        }
      } else {
        LazyColumn(
          modifier = Modifier.weight(1f),
          verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)
        ) {
          items(transactions) { tx ->
            CashTxCard(tx = tx, dateStr = df.format(Date(tx.createdAtEpochMs)))
          }
        }
      }
    }

    showAddDialog?.let { type ->
      var amount by remember { mutableStateOf("") }
      var note by remember { mutableStateOf("") }

      AlertDialog(
        onDismissRequest = { showAddDialog = null },
        title = { Text("Cash $type", style = MaterialTheme.typography.titleLarge) },
        containerColor = MaterialTheme.colorScheme.surface,
        titleContentColor = MaterialTheme.colorScheme.onSurface,
        textContentColor = MaterialTheme.colorScheme.onSurface,
        text = {
          Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            com.aslibill.ui.components.AsliTextField(
              value = amount,
              onValueChange = { if (it.isEmpty() || it.toDoubleOrNull() != null) amount = it },
              label = "Amount",
              keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number)
            )
            com.aslibill.ui.components.AsliTextField(
              value = note,
              onValueChange = { note = it },
              label = "Note (Optional)"
            )
          }
        },
        confirmButton = {
          TextButton(onClick = {
            val amt = amount.toDoubleOrNull() ?: 0.0
            if (amt > 0) {
              vm.addTransaction(type, amt, note)
              showAddDialog = null
            }
          }) { Text("SAVE", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold) }
        },
        dismissButton = {
          TextButton(onClick = { showAddDialog = null }) {
            Text("CANCEL", color = MaterialTheme.colorScheme.onSurfaceVariant)
          }
        }
      )

    }

    if (isLoading) {
      com.aslibill.ui.components.AsliLoader()
    }
    }
   }
  }

@Composable
private fun CashTxCard(tx: CashTransactionEntity, dateStr: String) {
  val color = if (tx.type == "IN" || tx.type == "OPEN") AsliColors.SuccessGreen else AsliColors.AlertOrange
  val prefix = if (tx.type == "IN" || tx.type == "OPEN") "+" else "-"

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
          tx.type,
          color = color,
          style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
          fontWeight = FontWeight.Black
        )
        Text(
          dateStr,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          style = MaterialTheme.typography.bodySmall,
          modifier = Modifier.padding(top = 2.dp)
        )
        if (!tx.note.isNullOrBlank()) {
          Text(
            tx.note,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 4.dp)
          )
        }
      }
      
      Surface(
        color = color.copy(alpha = 0.1f),
        shape = RoundedCornerShape(6.dp)
      ) {
        Text(
          "$prefix ₹${String.format("%.2f", tx.amount)}",
          color = color,
          style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Black),
          modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
        )
      }
    }

  }
}
