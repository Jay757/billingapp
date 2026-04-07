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
import androidx.compose.ui.unit.dp
import com.aslibill.ui.components.CircularKey
import com.aslibill.ui.components.DarkCard
import com.aslibill.ui.components.GrayButton
import com.aslibill.ui.components.OrangeButton
import com.aslibill.ui.components.ScreenSurface
import com.aslibill.ui.theme.AsliColors

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
        .padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text("Quick Bill", color = AsliColors.TextPrimary, style = MaterialTheme.typography.headlineSmall.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.Bold))
          Text("Fast generic billing", color = AsliColors.TextSecondary, style = MaterialTheme.typography.bodySmall)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          GrayButton("Item Wise", onClick = onGoItemWise)
          OrangeButton("Report", onClick = onGoReport)
        }
      }

      Row(
        modifier = Modifier
          .fillMaxWidth()
          .background(AsliColors.PrimaryLight, RoundedCornerShape(8.dp))
          .padding(horizontal = 12.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Text("SR", color = AsliColors.Primary, style = MaterialTheme.typography.labelMedium.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.Bold), modifier = Modifier.weight(0.4f))
        Text("DETAILS", color = AsliColors.Primary, style = MaterialTheme.typography.labelMedium.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.Bold), modifier = Modifier.weight(1.2f))
        Text("QTY", color = AsliColors.Primary, style = MaterialTheme.typography.labelMedium.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.Bold), modifier = Modifier.weight(0.6f))
        Text("RATE", color = AsliColors.Primary, style = MaterialTheme.typography.labelMedium.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.Bold), modifier = Modifier.weight(0.6f))
        Text("TOTAL", color = AsliColors.Primary, style = MaterialTheme.typography.labelMedium.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.Bold), modifier = Modifier.weight(0.7f), textAlign = androidx.compose.ui.text.style.TextAlign.End)
        Spacer(modifier = Modifier.width(36.dp))
      }

      DarkCard(modifier = Modifier.fillMaxWidth().weight(1f)) {
        if (lines.isEmpty()) {
          Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
              Icon(Icons.AutoMirrored.Outlined.ReceiptLong, contentDescription = null, tint = AsliColors.TextSecondary, modifier = Modifier.size(48.dp))
              Text("No entry added yet", color = AsliColors.TextSecondary, style = MaterialTheme.typography.bodyMedium)
            }
          }
        } else {
          Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            lines.forEachIndexed { idx, l ->
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .background(AsliColors.Bg, RoundedCornerShape(8.dp))
                  .padding(horizontal = 10.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(l.sr.toString(), color = AsliColors.TextSecondary, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(0.4f))
                Text(l.details, color = AsliColors.TextPrimary, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold), modifier = Modifier.weight(1.2f))
                Text(l.qty.toInt().toString(), color = AsliColors.TextPrimary, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(0.6f))
                Text("₹${l.rate.toInt()}", color = AsliColors.TextSecondary, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(0.6f))
                Text("₹${l.total.toInt()}", color = AsliColors.TextPrimary, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.Bold), modifier = Modifier.weight(0.7f), textAlign = androidx.compose.ui.text.style.TextAlign.End)
                IconButton(onClick = { vm.removeItem(idx) }, modifier = Modifier.size(36.dp)) {
                  Icon(Icons.Outlined.Delete, contentDescription = "Delete", tint = AsliColors.Orange, modifier = Modifier.size(20.dp))
                }
              }
            }
          }
        }
      }

      DarkCard(modifier = Modifier.fillMaxWidth()) {
        Row(
          modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text("Total Items", color = AsliColors.TextSecondary, style = MaterialTheme.typography.labelMedium)
            Text(lines.size.toString(), color = AsliColors.TextPrimary, style = MaterialTheme.typography.titleMedium.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.Bold))
          }
          Column(horizontalAlignment = Alignment.End) {
            Text("Grand Total", color = AsliColors.TextSecondary, style = MaterialTheme.typography.labelMedium)
            Text("₹ ${total.toInt()}", color = AsliColors.Primary, style = MaterialTheme.typography.titleLarge.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.Bold))
          }
        }
      }

      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        DarkCard(
          modifier = Modifier
            .weight(1f)
            .clickable { vm.setMode(QuickInputMode.QTY) }
        ) {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .background(if (mode == QuickInputMode.QTY) AsliColors.PrimaryLight else Color.Transparent)
              .padding(16.dp),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = if (qtyText.isBlank()) "Quantity" else qtyText,
              color = if (mode == QuickInputMode.QTY) AsliColors.Primary else AsliColors.TextSecondary,
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
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
              .background(if (mode == QuickInputMode.RATE) AsliColors.PrimaryLight else Color.Transparent)
              .padding(16.dp),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = if (rateText.isBlank()) "Rate" else rateText,
              color = if (mode == QuickInputMode.RATE) AsliColors.Primary else AsliColors.TextSecondary,
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
            )
          }
        }
        IconButton(
          onClick = { /* TODO */ },
          modifier = Modifier.background(AsliColors.Card2, RoundedCornerShape(10.dp)).padding(4.dp)
        ) {
          Icon(Icons.AutoMirrored.Outlined.ReceiptLong, contentDescription = "Receipt", tint = AsliColors.Primary)
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
          modifier = Modifier.background(AsliColors.PrimaryLight, RoundedCornerShape(10.dp)).padding(4.dp)
        ) {
          Icon(Icons.Outlined.Print, contentDescription = "Print", tint = AsliColors.Primary)
        }
      }

      if (!printMessage.isNullOrBlank()) {
          Text(printMessage!!, color = AsliColors.Primary, style = MaterialTheme.typography.labelMedium, modifier = Modifier.padding(horizontal = 12.dp))
      }

      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
          KeypadRow(listOf("7", "8", "9")) { vm.pressDigit(it) }
          KeypadRow(listOf("4", "5", "6")) { vm.pressDigit(it) }
          KeypadRow(listOf("1", "2", "3")) { vm.pressDigit(it) }
          Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(modifier = Modifier.weight(1f)) { CircularKey("0", onClick = { vm.pressDigit("0") }, modifier = Modifier.fillMaxWidth()) }
            Box(modifier = Modifier.weight(1f)) { CircularKey(".", onClick = { vm.pressDigit(".") }, modifier = Modifier.fillMaxWidth()) }
            Box(modifier = Modifier.weight(1f)) {
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .background(AsliColors.Card2, CircleShape)
                  .clickable { vm.backspace() }
                  .padding(18.dp),
                contentAlignment = Alignment.Center
              ) {
                Icon(Icons.AutoMirrored.Outlined.Backspace, contentDescription = "Backspace", tint = AsliColors.TextPrimary)
              }
            }
          }
        }

        Column(modifier = Modifier.width(120.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
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
          GrayButton(text = "Clear", onClick = vm::clearAll, modifier = Modifier.fillMaxWidth())
        }
      }
    }
  }
}

@Composable
private fun KeypadRow(keys: List<String>, onKey: (String) -> Unit) {
  Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
    keys.forEach { k ->
      Box(modifier = Modifier.weight(1f)) {
        CircularKey(label = k, onClick = { onKey(k) }, modifier = Modifier.fillMaxWidth())
      }
    }
  }
}

