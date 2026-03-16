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
        .padding(12.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text("Quick Bill", color = AsliColors.TextPrimary, style = MaterialTheme.typography.titleLarge)
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
          OrangeButton("Report", onClick = onGoReport)
          GrayButton("Item Wise Bill", onClick = onGoItemWise)
        }
      }

      Row(
        modifier = Modifier
          .fillMaxWidth()
          .background(AsliColors.Orange, RoundedCornerShape(8.dp))
          .padding(horizontal = 10.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Text("SR", color = Color.Black, modifier = Modifier.weight(0.5f))
        Text("DETAILS", color = Color.Black, modifier = Modifier.weight(1.4f))
        Text("QTY", color = Color.Black, modifier = Modifier.weight(0.7f))
        Text("RATE", color = Color.Black, modifier = Modifier.weight(0.7f))
        Text("TOTAL", color = Color.Black, modifier = Modifier.weight(0.8f))
      }

      DarkCard(modifier = Modifier.fillMaxWidth().weight(1f)) {
        if (lines.isEmpty()) {
          Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No entry", color = AsliColors.TextSecondary)
          }
        } else {
          Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            lines.forEach { l ->
              Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(l.sr.toString(), color = AsliColors.TextPrimary, modifier = Modifier.weight(0.5f))
                Text(l.details, color = AsliColors.TextPrimary, modifier = Modifier.weight(1.4f))
                Text(l.qty.toInt().toString(), color = AsliColors.TextPrimary, modifier = Modifier.weight(0.7f))
                Text(l.rate.toInt().toString(), color = AsliColors.TextPrimary, modifier = Modifier.weight(0.7f))
                Text(l.total.toInt().toString(), color = AsliColors.TextPrimary, modifier = Modifier.weight(0.8f))
              }
            }
          }
        }
      }

      DarkCard(modifier = Modifier.fillMaxWidth()) {
        Row(
          modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text(lines.size.toString(), color = AsliColors.TextPrimary)
          Text("₹ ${total.toInt()}", color = AsliColors.TextPrimary)
        }
      }

      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        DarkCard(
          modifier = Modifier
            .weight(1f)
            .clickable { vm.setMode(QuickInputMode.QTY) }
        ) {
          Box(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
            Text(
              text = if (qtyText.isBlank()) "Quantity" else qtyText,
              color = if (mode == QuickInputMode.QTY) AsliColors.Orange else AsliColors.TextSecondary
            )
          }
        }
        DarkCard(
          modifier = Modifier
            .weight(1f)
            .clickable { vm.setMode(QuickInputMode.RATE) }
        ) {
          Box(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
            Text(
              text = if (rateText.isBlank()) "Rate" else rateText,
              color = if (mode == QuickInputMode.RATE) AsliColors.Orange else AsliColors.TextSecondary
            )
          }
        }
        IconButton(onClick = { /* TODO */ }) {
          Icon(Icons.AutoMirrored.Outlined.ReceiptLong, contentDescription = "Receipt", tint = AsliColors.TextSecondary)
        }
        IconButton(onClick = {
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
        }) {
          Icon(Icons.Outlined.Print, contentDescription = "Print", tint = AsliColors.Orange)
        }
      }

      if (!printMessage.isNullOrBlank()) {
          Text(printMessage!!, color = AsliColors.TextSecondary, modifier = Modifier.padding(horizontal = 12.dp))
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
          OrangeButton(
            text = "Save",
            onClick = { vm.saveBill(onSaved = { }, onError = { }) },
            modifier = Modifier.fillMaxWidth()
          )
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

