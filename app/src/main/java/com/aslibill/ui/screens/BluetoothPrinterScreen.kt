package com.aslibill.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Bluetooth
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.aslibill.bluetooth.BtConnectionState
import com.aslibill.bluetooth.BtDeviceUi
import com.aslibill.ui.components.DarkCard
import com.aslibill.ui.components.GrayButton
import com.aslibill.ui.components.OrangeButton
import com.aslibill.ui.components.ScreenSurface
import com.aslibill.ui.theme.AppSpacing
import com.aslibill.ui.theme.AsliColors

@Composable
fun BluetoothPrinterScreen(
  contentPadding: PaddingValues,
  vm: BluetoothPrinterViewModel
) {
  val ctx = LocalContext.current
  val devices by vm.devices.collectAsState()
  val state by vm.state.collectAsState()

  var toastText by remember { mutableStateOf<String?>(null) }

  val permissions = remember {
    if (Build.VERSION.SDK_INT >= 31) {
      arrayOf(
        Manifest.permission.BLUETOOTH_SCAN,
        Manifest.permission.BLUETOOTH_CONNECT
      )
    } else {
      arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
    }
  }

  val permLauncher = rememberLauncherForActivityResult(
    ActivityResultContracts.RequestMultiplePermissions()
  ) { _ -> }

  fun hasAllPerms(): Boolean {
    return permissions.all { p ->
      ContextCompat.checkSelfPermission(ctx, p) == PackageManager.PERMISSION_GRANTED
    }
  }

  ScreenSurface {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(contentPadding)
        .padding(AppSpacing.md),
      verticalArrangement = Arrangement.spacedBy(AppSpacing.md)
    ) {
      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text("Bluetooth Devices", color = AsliColors.TextPrimary)
        IconButton(onClick = {
          if (!hasAllPerms()) permLauncher.launch(permissions) else vm.startScan()
        }) {
          Icon(Icons.Outlined.Refresh, contentDescription = "Refresh", tint = AsliColors.TextSecondary)
        }
      }

      DarkCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(AppSpacing.sm), verticalArrangement = Arrangement.spacedBy(AppSpacing.xs)) {
          val msg = when {
            !vm.isAvailable() -> "Bluetooth not available on this device"
            !vm.isEnabled() -> "Bluetooth is OFF. Turn it ON to scan printers."
            !hasAllPerms() -> "Permission required. Tap Scan to allow."
            else -> state.message ?: state.status.name
          }
          Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm)) {
            val icon = when (state.status) {
              BtConnectionState.Status.CONNECTED -> Icons.Outlined.CheckCircle
              BtConnectionState.Status.ERROR -> Icons.Outlined.Warning
              else -> Icons.Outlined.Bluetooth
            }
            Icon(icon, contentDescription = null, tint = AsliColors.Orange)
            Text(msg, color = AsliColors.TextPrimary)
          }

          Row(horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm)) {
            OrangeButton(
              text = if (state.status == BtConnectionState.Status.SCANNING) "Scanning..." else "Scan",
              onClick = {
                if (!hasAllPerms()) permLauncher.launch(permissions) else vm.startScan()
              },
              modifier = Modifier.weight(1f)
            )
            GrayButton(
              text = if (state.status == BtConnectionState.Status.CONNECTED) "Disconnect" else "Stop",
              onClick = {
                if (state.status == BtConnectionState.Status.CONNECTED) {
                  if (!hasAllPerms()) permLauncher.launch(permissions) else vm.disconnect()
                } else {
                  if (!hasAllPerms()) permLauncher.launch(permissions) else vm.stopScan()
                }
              },
              modifier = Modifier.weight(1f)
            )
            GrayButton(
              text = "Test Print",
              onClick = {
                if (!hasAllPerms()) {
                  permLauncher.launch(permissions)
                } else {
                  vm.testPrint { ok, err ->
                    toastText = if (ok) "Test print sent" else (err ?: "Test print failed")
                  }
                }
              },
              modifier = Modifier.weight(1f)
            )
          }

          if (!toastText.isNullOrBlank()) {
            Text(toastText!!, color = AsliColors.TextSecondary)
          }
        }
      }

      Text("Available devices", color = AsliColors.TextSecondary)
      LazyColumn(
        modifier = Modifier.weight(1f).fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)
      ) {
        items(devices, key = { it.address }) { d ->
          DeviceRow(
            device = d,
            connected = state.status == BtConnectionState.Status.CONNECTED && state.connectedAddress == d.address,
            onConnect = {
              if (!hasAllPerms()) permLauncher.launch(permissions) else vm.connect(d.address)
            }
          )
        }
      }
    }
  }
}

@Composable
private fun DeviceRow(
  device: BtDeviceUi,
  connected: Boolean,
  onConnect: () -> Unit
) {
  DarkCard(modifier = Modifier.fillMaxWidth().clickable { onConnect() }) {
    Row(
      modifier = Modifier.padding(12.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(device.name, color = AsliColors.TextPrimary)
        Text(device.address, color = AsliColors.TextSecondary)
        val meta = buildString {
          append(if (device.bonded) "Paired" else "Unpaired")
          device.rssi?.let { append(" • RSSI $it") }
        }
        Text(meta, color = AsliColors.TextSecondary)
      }
      val bg = if (connected) AsliColors.Green else AsliColors.Card2
      val fg = if (connected) Color.Black else AsliColors.TextPrimary
      Box(
        modifier = Modifier
          .background(bg, RoundedCornerShape(10.dp))
          .padding(horizontal = 12.dp, vertical = 6.dp)
      ) {
        Text(if (connected) "Connected" else "Connect", color = fg)
      }
    }
  }
}

