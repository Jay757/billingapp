package com.aslibill.printing

import com.aslibill.data.db.BillItemEntity
import com.aslibill.data.db.BillWithItemsRow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class StoreConfig(
  val storeName: String = "ASLI BILL",
  val addressLines: List<String> = listOf("Address line 1", "Address line 2"),
  val gstNumber: String? = null,
  val phone: String? = null,
  val thankYouMessage: String? = "THANK YOU",
  // 32 chars ≈ 58mm, 42–48 chars ≈ 80mm
  val paperWidthChars: Int = 32
)

fun buildReceiptText(
  bill: BillWithItemsRow,
  items: List<BillItemEntity>,
  config: StoreConfig
): String {
  val w = config.paperWidthChars.coerceIn(24, 48)
  val dfDate = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
  val dfTime = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
  val created = Date(bill.createdAtEpochMs)
  val dateStr = dfDate.format(created)
  val timeStr = dfTime.format(created)

  // ESC/POS Commands
  val ESC = "\u001B"
  val BOLD_ON = "$ESC" + "E" + "\u0001"
  val BOLD_OFF = "$ESC" + "E" + "\u0000"

  fun center(text: String): String {
    val t = text.take(w)
    val pad = ((w - t.length) / 2).coerceAtLeast(0)
    return " ".repeat(pad) + t
  }

  fun sep(): String = "-".repeat(w)

  fun formatCurrency(value: Double): String = String.format("%.2f", value)

  fun wrapText(text: String, width: Int): List<String> {
    if (text.length <= width) return listOf(text)
    val words = text.split(" ")
    val lines = mutableListOf<String>()
    var currentLine = StringBuilder()
    for (word in words) {
        if (currentLine.length + word.length + 1 <= width) {
            if (currentLine.isNotEmpty()) currentLine.append(" ")
            currentLine.append(word)
        } else {
            lines.add(currentLine.toString())
            currentLine = StringBuilder(word)
        }
    }
    if (currentLine.isNotEmpty()) lines.add(currentLine.toString())
    return lines
  }

  fun twoCol(left: String, right: String): String {
    val r = right.take(12)
    val spaceForLeft = (w - r.length - 1).coerceAtLeast(0)
    val l = left.take(spaceForLeft)
    val spaces = (w - l.length - r.length).coerceAtLeast(1)
    return l + " ".repeat(spaces) + r
  }

  fun itemLines(name: String, qty: Double, rate: Double, total: Double): List<String> {
    val totalStr = "₹${formatCurrency(total)}"
    val qtyRate = "${qty.toInt()} x ₹${formatCurrency(rate)}"
    
    // Wrap name if it's too long
    // Space for price column is roughly 12 chars
    val nameWidth = w - 1 - totalStr.length
    val wrappedName = wrapText(name, nameWidth)
    
    val result = mutableListOf<String>()
    // First line: First part of name + Price
    result.add(twoCol(wrappedName[0], totalStr))
    // Subsequent lines: rest of name
    for (i in 1 until wrappedName.size) {
        result.add(wrappedName[i])
    }
    // Qty x Rate line
    result.add("  $qtyRate")
    return result
  }

  return buildString {
    // Header - Bold Store Name
    append(BOLD_ON)
    appendLine(center(config.storeName))
    append(BOLD_OFF)
    
    config.addressLines.forEach { appendLine(center(it)) }
    config.phone?.let { appendLine(center("Ph: $it")) }
    config.gstNumber?.let { appendLine(center("GST: $it")) }
    appendLine(sep())
    appendLine(twoCol("Invoice: ${bill.billId}", ""))
    appendLine(twoCol("Date: $dateStr", timeStr))
    appendLine(twoCol("Cashier: ${bill.cashierName ?: "user"}", bill.paymentMethod))
    appendLine(sep())
    appendLine(twoCol("ITEM", "TOTAL"))
    appendLine(sep())

    // Items
    items.forEach { li ->
      itemLines(
        name = li.productNameSnapshot,
        qty = li.qty,
        rate = li.rate,
        total = li.lineTotal
      ).forEach { appendLine(it) }
    }

    appendLine(sep())
    appendLine(twoCol("Subtotal", "₹${formatCurrency(bill.subtotal)}"))
    appendLine(twoCol("Tax", "₹${formatCurrency(bill.tax)}"))
    appendLine(twoCol("Grand Total", "₹${formatCurrency(bill.total)}"))
    appendLine(sep())

    config.thankYouMessage?.let {
      appendLine()
      appendLine(center(it))
    }
    appendLine()
    appendLine()
  }
}

