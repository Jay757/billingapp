package com.billsuper.ui.screens

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
import com.billsuper.data.db.CashTransactionEntity
import com.billsuper.ui.components.DarkCard
import com.billsuper.ui.components.OrangeButton
import com.billsuper.ui.components.ScreenSurface
import com.billsuper.ui.components.SectionHeader
import com.billsuper.ui.components.StatsCard
import com.billsuper.ui.theme.AppSpacing
import com.billsuper.ui.theme.AppTypography
import com.billsuper.ui.theme.BillSuperColors
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
      Column(modifier = Modifier.padding(top = AppSpacing.md)) {
        Text(
          "Cash Management",
          color = MaterialTheme.colorScheme.onBackground,
          style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Black)
        )
        Text(
          "Monitor cash flow and account balance",
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          style = MaterialTheme.typography.bodyMedium
        )
      }


      // Balance Card
      StatsCard(
        label = "CURRENT BALANCE",
        value = "₹${String.format("%.2f", balance)}",
        icon = Icons.Outlined.AccountBalanceWallet,
        color = BillSuperColors.PrimaryBlue,
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
          Text("No cash transactions yet.", color = BillSuperColors.TextSecondary)
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

      val isValid = (amount.toDoubleOrNull() ?: 0.0) > 0

      com.billsuper.ui.components.BillSuperDialog(
        onDismissRequest = { showAddDialog = null },
        title = "Cash $type",
        confirmButton = {
          androidx.compose.material3.Button(
            onClick = {
              val amt = amount.toDoubleOrNull() ?: 0.0
              if (amt > 0) {
                vm.addTransaction(type, amt, note)
                showAddDialog = null
              }
            },
            enabled = isValid,
            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
              containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
              disabledContainerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f)
            ),
            shape = RoundedCornerShape(14.dp),
            contentPadding = PaddingValues(horizontal = 28.dp, vertical = 14.dp)
          ) {
            Text(
              "SAVE",
              color = if (isValid) BillSuperColors.PrimaryBlue else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
              fontWeight = FontWeight.Black,
              style = MaterialTheme.typography.labelLarge
            )
          }
        },
        dismissButton = {
          TextButton(onClick = { showAddDialog = null }) {
            Text(
              "CANCEL",
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
            )
          }
        }
      ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
          com.billsuper.ui.components.BillSuperTextField(
            value = amount,
            onValueChange = { if (it.isEmpty() || it.toDoubleOrNull() != null) amount = it },
            label = "Amount",
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number)
          )
          com.billsuper.ui.components.BillSuperTextField(
            value = note,
            onValueChange = { note = it },
            label = "Note (Optional)"
          )
        }
      }

    }

    if (isLoading) {
      com.billsuper.ui.components.BillSuperLoader()
    }
    }
   }
  }

@Composable
private fun CashTxCard(tx: CashTransactionEntity, dateStr: String) {
  val color = if (tx.type == "IN" || tx.type == "OPEN") BillSuperColors.SuccessGreen else BillSuperColors.AlertOrange
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


