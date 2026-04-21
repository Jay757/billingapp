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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.ui.text.style.TextAlign
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
import com.aslibill.ui.components.AsliTable
import com.aslibill.ui.theme.AsliColors
import com.aslibill.ui.theme.AppTypography
import com.aslibill.ui.theme.AppSpacing
import androidx.compose.material3.Surface
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.ui.graphics.luminance

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

      AsliTable(
        headers = listOf("ITEM", "QTY", "RATE", "TOTAL"),
        columnWeights = listOf(1.4f, 0.6f, 0.7f, 1.0f),
        isEmpty = lines.isEmpty(),
        modifier = Modifier.fillMaxWidth().weight(1f)
      ) {
        val isDark = MaterialTheme.colorScheme.surface.luminance() < 0.5f
        val onSurface = MaterialTheme.colorScheme.onSurface
        val onSurfaceVariant = MaterialTheme.colorScheme.onSurfaceVariant
        val primary = MaterialTheme.colorScheme.primary
        val surfaceVariant = MaterialTheme.colorScheme.surfaceVariant
        
        lines.forEachIndexed { idx, l ->
          val rowBg = if (isDark) surfaceVariant.copy(alpha = 0.3f) else Color.Transparent
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .background(rowBg)
              .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(l.details, color = onSurface, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Black), modifier = Modifier.weight(1.4f), maxLines = 1, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
            Text(l.qty.toInt().toString(), color = onSurface, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), modifier = Modifier.weight(0.6f), textAlign = TextAlign.Center)
            Text("₹${l.rate.toInt()}", color = onSurfaceVariant, style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(0.7f), textAlign = TextAlign.Center)
            
            Row(modifier = Modifier.weight(1.0f), horizontalArrangement = Arrangement.End, verticalAlignment = Alignment.CenterVertically) {
              Text("₹${l.total.toInt()}", color = primary, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Black))
              Spacer(Modifier.width(4.dp))
              IconButton(onClick = { vm.removeItem(idx) }, modifier = Modifier.size(28.dp)) {
                Icon(Icons.Outlined.Delete, contentDescription = "Delete", tint = AsliColors.Red.copy(alpha = 0.8f), modifier = Modifier.size(16.dp))
              }
            }
          }
          if (!isDark) androidx.compose.material3.HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), thickness = 0.5.dp, color = onSurface.copy(alpha = 0.1f))
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
          onClick = onGoReport,
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
              CircularKey(icon = Icons.AutoMirrored.Outlined.Backspace, onClick = { vm.backspace() }, modifier = Modifier.fillMaxWidth())
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

