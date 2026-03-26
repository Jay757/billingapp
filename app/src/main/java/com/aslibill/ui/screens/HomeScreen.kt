package com.aslibill.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.ui.res.painterResource
import com.aslibill.R
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.Bluetooth
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.DeleteForever
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Feedback
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.PlayCircle
import androidx.compose.material.icons.outlined.Print
import androidx.compose.material.icons.automirrored.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.Sell
import androidx.compose.material.icons.automirrored.outlined.ShowChart
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material.icons.outlined.Summarize
import androidx.compose.material.icons.outlined.SupportAgent
import androidx.compose.material.icons.outlined.Today
import androidx.compose.material.icons.outlined.WorkspacePremium
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.aslibill.ui.components.DarkCard
import com.aslibill.ui.components.IconTile
import com.aslibill.ui.components.PremiumBanner
import com.aslibill.ui.components.ScreenSurface
import com.aslibill.ui.components.SectionHeader
import com.aslibill.ui.theme.AsliColors
import com.aslibill.ui.theme.Brand

private data class HomeTile(
  val label: String,
  val icon: androidx.compose.ui.graphics.vector.ImageVector,
  val onClick: () -> Unit
)

@Composable
fun HomeScreen(
  onQuickBill: () -> Unit,
  onItemWiseBill: () -> Unit,
  onInventory: () -> Unit,
  onReports: () -> Unit,
  onBluetoothPrinter: () -> Unit,
  onPrintSettings: () -> Unit,
  onStaffManagement: () -> Unit,
  onCustomerManagement: () -> Unit,
  onCreditDetails: () -> Unit,
  onCashManagement: () -> Unit,
  onItemWiseSalesReport: () -> Unit,
  onDayReport: () -> Unit,
  onSalesSummary: () -> Unit,
  onUpgradePremium: () -> Unit,
  onTrainingVideo: () -> Unit,
  onBuyPrinters: () -> Unit,
  onFeedback: () -> Unit,
  onContactUs: () -> Unit,
  onSubscription: () -> Unit,
  onDeleteAccount: () -> Unit,
  onLogOut: () -> Unit,
  userName: String,
  userPhone: String,
  contentPadding: PaddingValues
) {
  ScreenSurface {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(contentPadding)
        .padding(16.dp)
        .verticalScroll(rememberScrollState()),
      verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Column {
          Text("Welcome, $userName", color = AsliColors.TextPrimary, style = MaterialTheme.typography.titleLarge)
          Text(userPhone, color = AsliColors.TextSecondary, style = MaterialTheme.typography.labelLarge)
        }
        DarkCard(modifier = Modifier.padding(top = 2.dp)) {
          Row(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
            Image(painter = painterResource(id = R.drawable.billing_icon), contentDescription = null, modifier = Modifier.size(24.dp))
            Text("NOVA\nBILL", color = AsliColors.TextPrimary, style = MaterialTheme.typography.labelMedium)
          }
        }
      }

      Text(
        text = Brand.AppTagline,
        color = AsliColors.TextSecondary,
        style = MaterialTheme.typography.bodyMedium
      )

      SectionHeader("Billing")
      DarkCard {
        HomeTileSection(
          tiles = listOf(
            HomeTile("Quick Bill", Icons.AutoMirrored.Outlined.ReceiptLong, onQuickBill),
            HomeTile("Item Wise Bill", Icons.AutoMirrored.Outlined.List, onItemWiseBill),
            HomeTile("Category / Product", Icons.Outlined.Inventory2, onInventory),
            HomeTile("Staff Management", Icons.Outlined.Groups, onStaffManagement),
            HomeTile("Customer Management", Icons.Outlined.Person, onCustomerManagement),
            HomeTile("Credit Details", Icons.Outlined.CreditCard, onCreditDetails),
            HomeTile("Cash Management", Icons.Outlined.AccountBalanceWallet, onCashManagement),
            HomeTile("Training Video", Icons.Outlined.PlayCircle, onTrainingVideo)
          )
        )
      }

      SectionHeader("Reports")
      DarkCard {
        HomeTileSection(
          tiles = listOf(
            HomeTile("Bill Report", Icons.Outlined.Description, onReports),
            HomeTile("Item Wise Sales Report", Icons.AutoMirrored.Outlined.ShowChart, onItemWiseSalesReport),
            HomeTile("Day Report", Icons.Outlined.Today, onDayReport),
            HomeTile("Sales Summary", Icons.Outlined.BarChart, onSalesSummary)
          )
        )
      }

      SectionHeader("Settings")
      DarkCard {
        HomeTileSection(
          tiles = listOf(
            HomeTile("Bluetooth Devices", Icons.Outlined.Bluetooth, onBluetoothPrinter),
            HomeTile("App Settings", Icons.Outlined.Print, onPrintSettings)
          )
        )
      }

      SectionHeader("Others")
      DarkCard {
        HomeTileSection(
          tiles = listOf(
            HomeTile("Buy Printers Here", Icons.Outlined.Sell, onBuyPrinters),
            HomeTile("Feedback", Icons.Outlined.Feedback, onFeedback),
            HomeTile("Contact Us", Icons.Outlined.SupportAgent, onContactUs)
          )
        )
      }

      SectionHeader("Account")
      DarkCard {
        HomeTileSection(
          tiles = listOf(
            HomeTile("Subscription", Icons.Outlined.WorkspacePremium, onSubscription),
            HomeTile("Delete Account", Icons.Outlined.DeleteForever, onDeleteAccount),
            HomeTile("Log Out", Icons.AutoMirrored.Outlined.Logout, onLogOut)
          )
        )
      }

      PremiumBanner(onClick = onUpgradePremium)
    }
  }
}

@Composable
private fun HomeTileSection(tiles: List<HomeTile>) {
  BoxWithConstraints(modifier = Modifier.padding(12.dp)) {
    val columns = if (maxWidth < 520.dp) 2 else 3
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
      tiles.chunked(columns).forEach { rowTiles ->
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
          rowTiles.forEach { tile ->
            IconTile(
              label = tile.label,
              icon = tile.icon,
              onClick = tile.onClick,
              modifier = Modifier.weight(1f)
            )
          }
          repeat(columns - rowTiles.size) {
            Spacer(modifier = Modifier.weight(1f))
          }
        }
      }
    }
  }
}

