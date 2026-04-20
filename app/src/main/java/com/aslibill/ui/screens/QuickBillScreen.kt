package com.aslibill.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Backspace
import androidx.compose.material.icons.automirrored.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.Print
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aslibill.ui.components.CircularKey
import com.aslibill.ui.components.DarkCard
import com.aslibill.ui.components.GrayButton
import com.aslibill.ui.components.OrangeButton
import com.aslibill.ui.components.ScreenSurface
import com.aslibill.ui.components.BillingTotalCard
import com.aslibill.ui.theme.AsliColors
import com.aslibill.ui.theme.AppTypography
import com.aslibill.ui.theme.AppSpacing
import androidx.compose.material3.Surface
import androidx.compose.foundation.layout.aspectRatio

@Composable
fun QuickBillScreen(
  contentPadding: PaddingValues,
  vm: QuickBillViewModel,
  btVm: BluetoothPrinterViewModel,
  onGoReport: () -> Unit,
  onGoItemWise: () -> Unit
) {
  val lines by vm.lines.collectAsState()
  val total by vm.total.collectAsState()
  val mode by vm.mode.collectAsState()
  val qtyText by vm.qtyText.collectAsState()
  val rateText by vm.rateText.collectAsState()

  var printMessage by remember { mutableStateOf<String?>(null) }

  ScreenSurface {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(contentPadding)
        .padding(AppSpacing.lg),
      verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            "Quick Bill", 
            color = MaterialTheme.colorScheme.onBackground, 
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Black)
          )
          Text(
            "Fast generic billing", 
            color = MaterialTheme.colorScheme.onSurfaceVariant, 
            style = MaterialTheme.typography.bodyMedium
          )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          GrayButton("Item Wise", onClick = onGoItemWise)
        }
      }


      // Modern Table Header
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(12.dp))
          .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f))
          .padding(horizontal = 12.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Text("SR", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), modifier = Modifier.weight(0.4f))
        Text("DETAILS", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), modifier = Modifier.weight(1.2f))
        Text("QTY", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), modifier = Modifier.weight(0.6f))
        Text("RATE", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), modifier = Modifier.weight(0.6f))
        Text("TOTAL", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), modifier = Modifier.weight(0.7f), textAlign = androidx.compose.ui.text.style.TextAlign.End)
        Spacer(modifier = Modifier.width(44.dp)) // Increased to match row icon button + padding
      }


      DarkCard(modifier = Modifier.fillMaxWidth().weight(1f)) {
        if (lines.isEmpty()) {
          Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)) {
              Icon(Icons.AutoMirrored.Outlined.ReceiptLong, contentDescription = null, tint = AsliColors.TextSecondary, modifier = Modifier.size(48.dp))
              Text("No entry added yet", color = AsliColors.TextSecondary, style = MaterialTheme.typography.bodyMedium)
            }
          }
        } else {
          Column(
            modifier = Modifier
              .padding(AppSpacing.md)
              .verticalScroll(androidx.compose.foundation.rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)
          ) {
            lines.forEachIndexed { idx, l ->
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                    .padding(horizontal = 12.dp, vertical = 14.dp),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text(l.sr.toString(), color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(0.4f))
                  Text(l.details, color = MaterialTheme.colorScheme.onSurface, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold), modifier = Modifier.weight(1.2f))
                  Text(l.qty.toInt().toString(), color = MaterialTheme.colorScheme.onSurface, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(0.6f))
                  Text("₹${l.rate.toInt()}", color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(0.6f))
                  Text("₹${l.total.toInt()}", color = MaterialTheme.colorScheme.onSurface, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold), modifier = Modifier.weight(0.7f), textAlign = androidx.compose.ui.text.style.TextAlign.End)
                  IconButton(onClick = { vm.removeItem(idx) }, modifier = Modifier.size(36.dp)) {
                    Icon(Icons.Outlined.Delete, contentDescription = "Delete", tint = AsliColors.Red, modifier = Modifier.size(20.dp))
                  }
                }

            }
          }
        }
      }

      BillingTotalCard(
        totalItems = lines.size,
        grandTotal = total
      )


      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        DarkCard(
          modifier = Modifier
            .weight(1f)
            .clickable { vm.setMode(QuickInputMode.QTY) }
        ) {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .background(if (mode == QuickInputMode.QTY) AsliColors.PrimaryBlue.copy(alpha = 0.1f) else Color.Transparent)
              .padding(16.dp),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = if (qtyText.isBlank()) "Quantity" else qtyText,
              color = if (mode == QuickInputMode.QTY) AsliColors.PrimaryBlue else AsliColors.TextSecondary,
              style = AppTypography.bodyBold
            )
          }
        }
        DarkCard(
          modifier = Modifier
            .weight(1f)
            .clickable { vm.setMode(QuickInputMode.RATE) }
        ) {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .background(if (mode == QuickInputMode.RATE) AsliColors.PrimaryBlue.copy(alpha = 0.1f) else Color.Transparent)
              .padding(16.dp),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = if (rateText.isBlank()) "Rate" else rateText,
              color = if (mode == QuickInputMode.RATE) AsliColors.PrimaryBlue else AsliColors.TextSecondary,
              style = AppTypography.bodyBold
            )
          }
        }
        IconButton(
          onClick = { /* TODO */ },
          modifier = Modifier.background(AsliColors.Card2, RoundedCornerShape(10.dp)).padding(4.dp)
        ) {
          Icon(Icons.AutoMirrored.Outlined.ReceiptLong, contentDescription = "Receipt", tint = AsliColors.PrimaryBlue)
        }
        IconButton(
          onClick = {
            if (lines.isEmpty()) {
                printMessage = "Add items first"
                return@IconButton
            }
            printMessage = "Printing..."
            val billRow = com.aslibill.data.db.BillWithItemsRow(
                billId = 0,
                createdAtEpochMs = System.currentTimeMillis(),
                cashierName = "user",
                subtotal = total,
                tax = 0.0,
                total = total,
                paymentMethod = "CASH",
                itemCount = lines.size
            )
            val items = lines.map {
                com.aslibill.data.db.BillItemEntity(
                    billId = 0,
                    productId = null,
                    productNameSnapshot = it.details,
                    qty = it.qty,
                    rate = it.rate,
                    lineTotal = it.total
                )
            }
            btVm.printBill(billRow, items) { ok, err ->
                printMessage = if (ok) "Print sent" else (err ?: "Print failed")
            }
          },
          modifier = Modifier.background(AsliColors.PrimaryBlue.copy(alpha = 0.1f), RoundedCornerShape(10.dp)).padding(4.dp)
        ) {
          Icon(Icons.Outlined.Print, contentDescription = "Print", tint = AsliColors.PrimaryBlue)
        }
      }

      if (!printMessage.isNullOrBlank()) {
          Text(printMessage!!, color = AsliColors.PrimaryBlue, style = AppTypography.bodySmall, modifier = Modifier.padding(horizontal = 12.dp))
      }

      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(AppSpacing.lg)) {
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(AppSpacing.md)) {
          KeypadRow(listOf("7", "8", "9")) { vm.pressDigit(it) }
          KeypadRow(listOf("4", "5", "6")) { vm.pressDigit(it) }
          KeypadRow(listOf("1", "2", "3")) { vm.pressDigit(it) }
          Row(horizontalArrangement = Arrangement.spacedBy(AppSpacing.md)) {
            Box(modifier = Modifier.weight(1f)) { CircularKey("0", onClick = { vm.pressDigit("0") }, modifier = Modifier.fillMaxWidth()) }
            Box(modifier = Modifier.weight(1f)) { CircularKey(".", onClick = { vm.pressDigit(".") }, modifier = Modifier.fillMaxWidth()) }
            Box(modifier = Modifier.weight(1f)) {
              Surface(
                modifier = Modifier
                  .fillMaxWidth()
                  .aspectRatio(1f)
                  .clickable { vm.backspace() },
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f)
              ) {
                Box(contentAlignment = Alignment.Center) {
                  Icon(Icons.AutoMirrored.Outlined.Backspace, contentDescription = "Backspace", tint = MaterialTheme.colorScheme.onSurface)
                }
              }
            }
          }
        }

        androidx.compose.foundation.layout.BoxWithConstraints(modifier = Modifier.weight(0.5f).heightIn(max = 280.dp)) {
          val buttonWidth = maxWidth
          Column(modifier = Modifier.width(buttonWidth), verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)) {
             OrangeButton(text = "Save", onClick = {
                 printMessage = "Saving..."
                 vm.saveBill(onSaved = { printMessage = "Saved!" }, onError = { printMessage = "Save Error" }) 
             }, modifier = Modifier.fillMaxWidth())

             OrangeButton(text = "Save & Print", onClick = {
               val linesSnapshot = lines.toList()
               val totalSnapshot = total
               printMessage = "Saving..."
               vm.saveBill(onSaved = { billId ->
                   printMessage = "Printing saved bill..."
                   val billRow = com.aslibill.data.db.BillWithItemsRow(
                       billId = billId,
                       createdAtEpochMs = System.currentTimeMillis(),
                       cashierName = "user",
                       subtotal = totalSnapshot,
                       tax = 0.0,
                       total = totalSnapshot,
                       paymentMethod = "CASH",
                       itemCount = linesSnapshot.size
                   )
                   val items = linesSnapshot.map {
                       com.aslibill.data.db.BillItemEntity(
                           billId = billId,
                           productId = null,
                           productNameSnapshot = it.details,
                           qty = it.qty,
                           rate = it.rate,
                           lineTotal = it.total
                       )
                   }
                   btVm.printBill(billRow, items) { ok, err ->
                       printMessage = if (ok) "Print sent" else (err ?: "Print failed")
                   }
               }, onError = { printMessage = "Save Error" })
             }, modifier = Modifier.fillMaxWidth())
             
            OrangeButton(text = "Enter", onClick = vm::enterLine, modifier = Modifier.fillMaxWidth())
            GrayButton(text = "Clear All", onClick = vm::clearAll, modifier = Modifier.fillMaxWidth())
          }
        }
      }    }
  }
}

@Composable
private fun KeypadRow(keys: List<String>, onKey: (String) -> Unit) {
  Row(horizontalArrangement = Arrangement.spacedBy(AppSpacing.md)) {
    keys.forEach { k ->
      Box(modifier = Modifier.weight(1f)) {
        CircularKey(label = k, onClick = { onKey(k) }, modifier = Modifier.fillMaxWidth())
      }
    }
  }
}

