package com.billsuper.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.billsuper.data.BillingRepository
import com.billsuper.data.db.BillEntity
import com.billsuper.data.db.BillItemEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

data class QuickBillLine(
  val sr: Int,
  val details: String,
  val qty: Double,
  val rate: Double
) {
  val total: Double get() = qty * rate
}

enum class QuickInputMode { QTY, RATE }

class QuickBillViewModel(
  private val billing: BillingRepository
) : ViewModel() {

  private val _isLoading = MutableStateFlow(false)
  val isLoading = _isLoading.asStateFlow()

  private val _lines = kotlinx.coroutines.flow.MutableStateFlow<List<QuickBillLine>>(emptyList())
  val lines: StateFlow<List<QuickBillLine>> = _lines

  val total: StateFlow<Double> =
    _lines.map { it.sumOf { l -> l.total } }
      .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0.0)

  private val _mode = kotlinx.coroutines.flow.MutableStateFlow(QuickInputMode.QTY)
  val mode: StateFlow<QuickInputMode> = _mode

  private val _qtyText = kotlinx.coroutines.flow.MutableStateFlow("")
  val qtyText: StateFlow<String> = _qtyText

  private val _rateText = kotlinx.coroutines.flow.MutableStateFlow("")
  val rateText: StateFlow<String> = _rateText

  fun setMode(mode: QuickInputMode) {
    _mode.value = mode
  }

  fun pressDigit(d: String) {
    val cur = if (_mode.value == QuickInputMode.QTY) _qtyText.value else _rateText.value
    val next = (cur + d).take(10)
    if (_mode.value == QuickInputMode.QTY) _qtyText.value = next else _rateText.value = next
  }

  fun backspace() {
    val cur = if (_mode.value == QuickInputMode.QTY) _qtyText.value else _rateText.value
    val next = if (cur.isEmpty()) cur else cur.dropLast(1)
    if (_mode.value == QuickInputMode.QTY) _qtyText.value = next else _rateText.value = next
  }

  fun clearInput() {
    _qtyText.value = ""
    _rateText.value = ""
  }

  fun clearAll() {
    _lines.value = emptyList()
    clearInput()
  }

  fun enterLine() {
    val qty = _qtyText.value.toDoubleOrNull() ?: 0.0
    val rate = _rateText.value.toDoubleOrNull() ?: 0.0
    if (qty <= 0 || rate <= 0) return
    val sr = _lines.value.size + 1
    _lines.value = _lines.value + QuickBillLine(sr = sr, details = "Quick", qty = qty, rate = rate)
    clearInput()
    _mode.value = QuickInputMode.QTY
  }

  fun removeItem(index: Int) {
    _lines.value = _lines.value.filterIndexed { i, _ -> i != index }
  }

  fun saveBill(
    paymentMode: PaymentMode = PaymentMode.CASH,
    cashierName: String? = "user",
    onSaved: (Long) -> Unit,
    onError: (Throwable) -> Unit
  ) {
    val lines = _lines.value
    if (lines.isEmpty()) return

    viewModelScope.launch {
      _isLoading.value = true
      try {
        val subtotal = lines.sumOf { it.total }
        val bill = BillEntity(
          createdAtEpochMs = System.currentTimeMillis(),
          cashierName = cashierName,
          subtotal = subtotal,
          tax = 0.0,
          total = subtotal,
          paymentMethod = paymentMode.name
        )
        val items = lines.map { l ->
          BillItemEntity(
            billId = 0,
            productId = null,
            productNameSnapshot = l.details,
            qty = l.qty,
            rate = l.rate,
            lineTotal = l.total
          )
        }
        val id = billing.saveBill(bill, items)
        clearAll()
        onSaved(id)
      } catch (t: Throwable) {
        onError(t)
      } finally {
        _isLoading.value = false
      }
    }
  }
}


