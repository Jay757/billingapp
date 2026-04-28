package com.billsuper.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.Bluetooth
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.billsuper.bluetooth.BtConnectionState
import com.billsuper.bluetooth.BtDeviceUi
import com.billsuper.ui.components.BillSuperIconButton
import com.billsuper.ui.components.DarkCard
import com.billsuper.ui.components.GrayButton
import com.billsuper.ui.components.OrangeButton
import com.billsuper.ui.components.ScreenSurface
import com.billsuper.ui.components.SectionHeader
import com.billsuper.ui.theme.AppSpacing
import com.billsuper.ui.theme.AppTypography
import com.billsuper.ui.theme.BillSuperColors

@Composable
fun BluetoothPrinterScreen(
  contentPadding: PaddingValues,
  vm: BluetoothPrinterViewModel
) {
  val ctx = LocalContext.current
  val devices by vm.devices.collectAsState()
  val state by vm.state.collectAsState()
  val isLoading by vm.isLoading.collectAsState()

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
    Box(modifier = Modifier.fillMaxSize()) {
      Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(contentPadding)
        .padding(horizontal = AppSpacing.md),
      verticalArrangement = Arrangement.spacedBy(AppSpacing.md)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth().padding(top = AppSpacing.md),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            "Printer Setup",
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Black)
          )
          Text(
            "Configure your bluetooth thermal printer",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium
          )
        }

        BillSuperIconButton(
          icon = Icons.Default.Refresh,
          onClick = {
            if (!hasAllPerms()) permLauncher.launch(permissions) else vm.startScan()
          }
        )
      }

      DarkCard(modifier = Modifier.fillMaxWidth()) {
        Column(
          modifier = Modifier.padding(AppSpacing.lg),
          verticalArrangement = Arrangement.spacedBy(AppSpacing.lg)
        ) {
          val msg = when {
            !vm.isAvailable() -> "Bluetooth not available on this device"
            !vm.isEnabled() -> "Bluetooth is OFF. Turn it ON to scan printers."
            !hasAllPerms() -> "Permission required. Tap Scan to allow."
            else -> state.message ?: state.status.name
          }
          
          Surface(
            color = if (state.status == BtConnectionState.Status.CONNECTED) BillSuperColors.SuccessGreen.copy(alpha = 0.1f) else BillSuperColors.PrimaryBlue.copy(alpha = 0.1f),
            shape = RoundedCornerShape(12.dp)
          ) {
            Row(
              modifier = Modifier.fillMaxWidth().padding(AppSpacing.md),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(AppSpacing.md)
            ) {
              val icon = when (state.status) {
                BtConnectionState.Status.CONNECTED -> Icons.Outlined.CheckCircle
                BtConnectionState.Status.ERROR -> Icons.Outlined.Warning
                else -> Icons.Outlined.Bluetooth
              }
              val iconColor = if (state.status == BtConnectionState.Status.CONNECTED) BillSuperColors.SuccessGreen else MaterialTheme.colorScheme.primary
              Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(24.dp))
              Text(
                msg,
                color = iconColor,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
              )

            }
          }

          Row(horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm)) {
            OrangeButton(
              text = if (state.status == BtConnectionState.Status.SCANNING) "SCANNING..." else "SCAN",
              onClick = {
                if (!hasAllPerms()) permLauncher.launch(permissions) else vm.startScan()
              },
              modifier = Modifier.weight(1f)
            )
            GrayButton(
              text = if (state.status == BtConnectionState.Status.CONNECTED) "DISCONNECT" else "STOP",
              onClick = {
                if (state.status == BtConnectionState.Status.CONNECTED) {
                  if (!hasAllPerms()) permLauncher.launch(permissions) else vm.disconnect()
                } else {
                  if (!hasAllPerms()) permLauncher.launch(permissions) else vm.stopScan()
                }
              },
              modifier = Modifier.weight(1f)
            )
          }
          
          OrangeButton(
            text = "TEST PRINT",
            onClick = {
              if (!hasAllPerms()) {
                permLauncher.launch(permissions)
              } else {
                vm.testPrint { ok, err ->
                  toastText = if (ok) "Test print sent" else (err ?: "Test print failed")
                }
              }
            },
            modifier = Modifier.fillMaxWidth()
          )

          if (!toastText.isNullOrBlank()) {
            Text(
              toastText!!,
              color = BillSuperColors.AlertOrange,
              style = AppTypography.bodySmall,
              fontWeight = FontWeight.Bold
            )
          }
        }
      }

      SectionHeader("AVAILABLE DEVICES")
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

    if (isLoading || state.status == com.billsuper.bluetooth.BtConnectionState.Status.CONNECTING) {
      com.billsuper.ui.components.BillSuperLoader()
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
      modifier = Modifier.padding(AppSpacing.lg).fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column(modifier = Modifier.weight(1f)) {
        Text(
          device.name.uppercase(),
          color = MaterialTheme.colorScheme.onSurface,
          style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Black)
        )
        Text(
          device.address,
          color = MaterialTheme.colorScheme.primary,
          style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
          modifier = Modifier.padding(top = 2.dp)
        )

        val meta = buildString {
          append(if (device.bonded) "PAIRED" else "UNPAIRED")
          device.rssi?.let { append(" • RSSI $it") }
        }
        Text(
          meta,
          color = BillSuperColors.TextSecondary,
          style = AppTypography.bodySmall,
          modifier = Modifier.padding(top = 4.dp)
        )
      }
      
      Surface(
        color = if (connected) BillSuperColors.SuccessGreen.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(6.dp)
      ) {
        Text(
          if (connected) "CONNECTED" else "CONNECT",
          color = if (connected) BillSuperColors.SuccessGreen else MaterialTheme.colorScheme.onSurfaceVariant,
          style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
          modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
        )
      }

    }
  }
}



