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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.unit.dp
import com.aslibill.data.db.CashTransactionEntity
import com.aslibill.ui.components.DarkCard
import com.aslibill.ui.components.OrangeButton
import com.aslibill.ui.components.ScreenSurface
import com.aslibill.ui.components.SectionHeader
import com.aslibill.ui.theme.AppSpacing
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
  var showAddDialog by remember { mutableStateOf<String?>(null) } // "IN" or "OUT"

  val df = remember { SimpleDateFormat("dd-MM-yyyy hh:mm a", Locale.getDefault()) }

  ScreenSurface {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(contentPadding)
        .padding(AppSpacing.md),
      verticalArrangement = Arrangement.spacedBy(AppSpacing.md)
    ) {
      Text("Cash Management", color = AsliColors.TextPrimary, style = MaterialTheme.typography.titleLarge)

      // Balance Card
      DarkCard(modifier = Modifier.fillMaxWidth()) {
        Column(
          modifier = Modifier.padding(16.dp),
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Text("Current Balance", color = AsliColors.TextSecondary, style = MaterialTheme.typography.labelMedium)
          Text("₹ ${String.format("%.2f", balance)}", color = AsliColors.TextPrimary, style = MaterialTheme.typography.headlineMedium)
          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm)) {
            OrangeButton("Cash IN", onClick = { showAddDialog = "IN" }, modifier = Modifier.weight(1f))
            OrangeButton("Cash OUT", onClick = { showAddDialog = "OUT" }, modifier = Modifier.weight(1f))
          }
        }
      }

      SectionHeader("Recent Transactions")

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
        title = { Text("Cash $type", color = AsliColors.TextPrimary) },
        text = {
          Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            val colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = AsliColors.Orange,
              unfocusedBorderColor = AsliColors.TextSecondary,
            focusedTextColor = AsliColors.TextPrimary,
            unfocusedTextColor = AsliColors.TextPrimary
            )
            OutlinedTextField(
              value = amount,
              onValueChange = { if (it.isEmpty() || it.toDoubleOrNull() != null) amount = it },
              label = { Text("Amount") },
              colors = colors,
              modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
              value = note,
              onValueChange = { note = it },
              label = { Text("Note (Optional)") },
              colors = colors,
              modifier = Modifier.fillMaxWidth()
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
          }) { Text("SAVE", color = AsliColors.Orange) }
        },
        dismissButton = {
          TextButton(onClick = { showAddDialog = null }) { Text("CANCEL", color = AsliColors.TextPrimary) }
        }
      )
    }
  }
}

@Composable
private fun CashTxCard(tx: CashTransactionEntity, dateStr: String) {
  val color = if (tx.type == "IN" || tx.type == "OPEN") AsliColors.Green else AsliColors.Red
  val prefix = if (tx.type == "IN" || tx.type == "OPEN") "+" else "-"

  DarkCard(modifier = Modifier.fillMaxWidth()) {
    Row(
      modifier = Modifier.padding(12.dp).fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(tx.type, color = color, style = MaterialTheme.typography.labelLarge)
        Text(dateStr, color = AsliColors.TextSecondary, style = MaterialTheme.typography.labelSmall)
        if (!tx.note.isNullOrBlank()) {
          Text(tx.note, color = AsliColors.TextPrimary, style = MaterialTheme.typography.bodySmall)
        }
      }
      Text("$prefix ₹ ${String.format("%.2f", tx.amount)}", color = color, style = MaterialTheme.typography.titleMedium)
    }
  }
}
