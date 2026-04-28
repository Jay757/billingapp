package com.billsuper.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.OutputStream
import java.util.UUID
import com.billsuper.data.db.BillItemEntity
import com.billsuper.data.db.BillWithItemsRow
import com.billsuper.printing.StoreConfig
import com.billsuper.printing.buildReceiptText

data class BtDeviceUi(
  val name: String,
  val address: String,
  val bonded: Boolean,
  val rssi: Int? = null
)

data class BtConnectionState(
  val status: Status,
  val connectedAddress: String? = null,
  val message: String? = null
) {
  enum class Status { DISCONNECTED, SCANNING, CONNECTING, CONNECTED, ERROR }
}

class BluetoothPrinterManager(
  private val context: Context,
  private val scope: CoroutineScope
) {
  @Suppress("DEPRECATION")
  private val adapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
  private var socket: BluetoothSocket? = null
  private var out: OutputStream? = null
  private var receiver: BroadcastReceiver? = null

  private val _devices = MutableStateFlow<List<BtDeviceUi>>(emptyList())
  val devices: StateFlow<List<BtDeviceUi>> = _devices

  private val _state = MutableStateFlow(BtConnectionState(BtConnectionState.Status.DISCONNECTED))
  val state: StateFlow<BtConnectionState> = _state

  fun isBluetoothAvailable(): Boolean = adapter != null
  fun isBluetoothEnabled(): Boolean = adapter?.isEnabled == true

  @SuppressLint("MissingPermission")
  fun startScan() {
    val bt = adapter ?: run {
      _state.value = BtConnectionState(BtConnectionState.Status.ERROR, message = "Bluetooth not available")
      return
    }
    if (!bt.isEnabled) {
      _state.value = BtConnectionState(BtConnectionState.Status.ERROR, message = "Bluetooth is OFF")
      return
    }

    stopScan()
    _state.value = BtConnectionState(BtConnectionState.Status.SCANNING, message = "Scanning...")
    _devices.value = bondedDevicesSnapshot()

    val filter = IntentFilter().apply {
      addAction(BluetoothDevice.ACTION_FOUND)
      addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
    }
    receiver = object : BroadcastReceiver() {
      @SuppressLint("MissingPermission")
      @Suppress("DEPRECATION")
      override fun onReceive(ctx: Context, intent: Intent) {
        when (intent.action) {
          BluetoothDevice.ACTION_FOUND -> {
            val device: BluetoothDevice? = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
            val rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE).toInt()
            if (device != null) {
              val item = BtDeviceUi(
                name = device.name ?: "Bluetooth Device",
                address = device.address ?: "",
                bonded = device.bondState == BluetoothDevice.BOND_BONDED,
                rssi = if (rssi == Short.MIN_VALUE.toInt()) null else rssi
              )
              _devices.value = mergeDevice(_devices.value, item)
            }
          }
          BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
            if (_state.value.status == BtConnectionState.Status.SCANNING) {
              _state.value = BtConnectionState(BtConnectionState.Status.DISCONNECTED, message = "Scan finished")
            }
          }
        }
      }
    }
    context.registerReceiver(receiver, filter)
    bt.startDiscovery()
  }

  @SuppressLint("MissingPermission")
  fun stopScan() {
    val bt = adapter ?: return
    try {
      if (bt.isDiscovering) bt.cancelDiscovery()
    } catch (_: Throwable) { }
    receiver?.let {
      try { context.unregisterReceiver(it) } catch (_: Throwable) { }
    }
    receiver = null
    if (_state.value.status == BtConnectionState.Status.SCANNING) {
      _state.value = BtConnectionState(BtConnectionState.Status.DISCONNECTED, message = "Scan stopped")
    }
  }

  @SuppressLint("MissingPermission")
  private fun bondedDevicesSnapshot(): List<BtDeviceUi> {
    val bt = adapter ?: return emptyList()
    return bt.bondedDevices
      ?.map {
        BtDeviceUi(
          name = it.name ?: "Paired device",
          address = it.address ?: "",
          bonded = true
        )
      }
      ?.sortedBy { it.name }
      ?: emptyList()
  }

  @SuppressLint("MissingPermission")
  fun connect(address: String) {
    val bt = adapter ?: run {
      _state.value = BtConnectionState(BtConnectionState.Status.ERROR, message = "Bluetooth not available")
      return
    }
    if (!bt.isEnabled) {
      _state.value = BtConnectionState(BtConnectionState.Status.ERROR, message = "Bluetooth is OFF")
      return
    }
    stopScan()
    disconnect()

    _state.value = BtConnectionState(BtConnectionState.Status.CONNECTING, message = "Connecting...")
    scope.launch(Dispatchers.IO) {
      try {
        val device = bt.getRemoteDevice(address)
        // Classic SPP UUID (most thermal printers)
        val spp = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
        val s = device.createRfcommSocketToServiceRecord(spp)
        bt.cancelDiscovery()
        s.connect()
        socket = s
        out = s.outputStream
        _state.value = BtConnectionState(BtConnectionState.Status.CONNECTED, connectedAddress = address, message = "Connected")
      } catch (t: Throwable) {
        _state.value = BtConnectionState(BtConnectionState.Status.ERROR, message = t.message ?: "Connection failed")
        disconnect()
      }
    }
  }

  fun disconnect() {
    try { out?.close() } catch (_: Throwable) { }
    try { socket?.close() } catch (_: Throwable) { }
    out = null
    socket = null
    if (_state.value.status == BtConnectionState.Status.CONNECTED || _state.value.status == BtConnectionState.Status.CONNECTING) {
      _state.value = BtConnectionState(BtConnectionState.Status.DISCONNECTED, message = "Disconnected")
    }
  }

  suspend fun testPrint(title: String = "CASH RECEIPT"): Result<Unit> = withContext(Dispatchers.IO) {
    val stream = out ?: return@withContext Result.failure(IllegalStateException("Not connected"))
    try {
      val now = java.text.SimpleDateFormat("dd-MM-yyyy", java.util.Locale.getDefault()).format(java.util.Date())
      val time = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault()).format(java.util.Date())

      val text = buildString {
        appendLine("        $title")
        appendLine("Adress: 123 Lorem Ipsum, Dolor")
        appendLine("Tel: 123-456-7890")
        appendLine("--------------------------------")
        appendLine("Date: $now           $time")
        appendLine("--------------------------------")
        appendLine("Lorem                 6.50")
        appendLine("Ipsum                 7.50")
        appendLine("Dolor Sit            48.00")
        appendLine("Amet                  9.30")
        appendLine("Consectetur          11.90")
        appendLine("Adipiscing Elit       1.20")
        appendLine("Sed Do                0.40")
        appendLine("--------------------------------")
        appendLine("Total                84.80")
        appendLine("Sub-total            76.80")
        appendLine("Sales Tax             8.00")
        appendLine("Balance              84.80")
        appendLine()
        appendLine("        THANK YOU")
        appendLine()
        appendLine()
      }
      writeWithChunking(text.toByteArray(Charsets.UTF_8))
      // Feed & cut
      writeWithChunking(byteArrayOf(0x1D, 0x56, 0x41, 0x10))
      Result.success(Unit)
    } catch (t: Throwable) {
      Result.failure(t)
    }
  }

  suspend fun printBill(
    bill: BillWithItemsRow,
    items: List<BillItemEntity>,
    config: StoreConfig
  ): Result<Unit> = withContext(Dispatchers.IO) {
    val stream = out ?: return@withContext Result.failure(
      IllegalStateException("Not connected")
    )
    try {
      val text = buildReceiptText(bill, items, config)
      writeWithChunking(text.toByteArray(Charsets.UTF_8))
      // Feed & cut
      writeWithChunking(byteArrayOf(0x1D, 0x56, 0x41, 0x10))
      Result.success(Unit)
    } catch (t: Throwable) {
      Result.failure(t)
    }
  }

  private fun writeWithChunking(data: ByteArray) {
    val stream = out ?: throw IllegalStateException("Not connected")
    val chunkSize = 1024
    var offset = 0
    while (offset < data.size) {
        val count = (data.size - offset).coerceAtMost(chunkSize)
        try {
            stream.write(data, offset, count)
            stream.flush()
        } catch (t: Throwable) {
            _state.value = BtConnectionState(BtConnectionState.Status.ERROR, message = "Print failed: ${t.message}")
            disconnect()
            throw t
        }
        offset += count
        if (offset < data.size) {
            // Tiny delay between chunks to let the printer buffer breathe
            Thread.sleep(50)
        }
    }
  }

  fun close() {
    stopScan()
    disconnect()
  }
}

private fun mergeDevice(list: List<BtDeviceUi>, item: BtDeviceUi): List<BtDeviceUi> {
  if (item.address.isBlank()) return list
  val existingIdx = list.indexOfFirst { it.address == item.address }
  val next = if (existingIdx == -1) list + item else list.toMutableList().also { it[existingIdx] = item }
  // Show bonded first, then stronger RSSI
  return next.sortedWith(compareByDescending<BtDeviceUi> { it.bonded }.thenByDescending { it.rssi ?: Int.MIN_VALUE }.thenBy { it.name })
}


