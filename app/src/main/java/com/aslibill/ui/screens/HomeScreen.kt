package com.aslibill.ui.screens

import androidx.compose.foundation.layout.Arrangement
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
          Text("Welcome, $userName !", color = AsliColors.TextPrimary, style = MaterialTheme.typography.titleLarge)
          Text(userPhone, color = AsliColors.TextSecondary, style = MaterialTheme.typography.labelLarge)
        }
        DarkCard(modifier = Modifier.padding(top = 2.dp)) {
          Row(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
            Image(painter = painterResource(id = R.drawable.billing_icon), contentDescription = null, modifier = Modifier.size(24.dp))
            Text("ASLI\nBILL", color = Color.White, style = MaterialTheme.typography.labelMedium)
          }
        }
      }

      SectionHeader("Billing")
      DarkCard {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            IconTile("Quick\nBill", Icons.AutoMirrored.Outlined.ReceiptLong, onQuickBill, modifier = Modifier.weight(1f))
            IconTile("Item\nWise Bill", Icons.AutoMirrored.Outlined.List, onItemWiseBill, modifier = Modifier.weight(1f))
            IconTile("Category/\nProduct", Icons.Outlined.Inventory2, onInventory, modifier = Modifier.weight(1f))
            IconTile("Staff\nManagement", Icons.Outlined.Groups, onClick = onStaffManagement, modifier = Modifier.weight(1f))
          }
          Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            IconTile("Customer\nManagement", Icons.Outlined.Person, onClick = onCustomerManagement, modifier = Modifier.weight(1f))
            IconTile("Credit\nDetails", Icons.Outlined.CreditCard, onClick = onCreditDetails, modifier = Modifier.weight(1f))
            IconTile("Cash\nManagement", Icons.Outlined.AccountBalanceWallet, onClick = onCashManagement, modifier = Modifier.weight(1f))
            IconTile("Training\nVideo", Icons.Outlined.PlayCircle, onClick = onTrainingVideo, modifier = Modifier.weight(1f))
          }
        }
      }

      SectionHeader("Reports")
      DarkCard {
        Row(modifier = Modifier.padding(12.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
          IconTile("Bill\nReport", Icons.Outlined.Description, onReports, modifier = Modifier.weight(1f))
          IconTile("Item Wise\nSales Report", Icons.AutoMirrored.Outlined.ShowChart, onClick = onItemWiseSalesReport, modifier = Modifier.weight(1f))
          IconTile("Day\nReport", Icons.Outlined.Today, onClick = onDayReport, modifier = Modifier.weight(1f))
          IconTile("Sales\nSummary", Icons.Outlined.BarChart, onClick = onSalesSummary, modifier = Modifier.weight(1f))
        }
      }

      SectionHeader("Print")
      DarkCard {
        Row(modifier = Modifier.padding(12.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
          IconTile("Bluetooth", Icons.Outlined.Bluetooth, onBluetoothPrinter, modifier = Modifier.weight(1f))
          IconTile("Print\nSetting", Icons.Outlined.Print, onPrintSettings, modifier = Modifier.weight(1f))
          Spacer(modifier = Modifier.weight(1f))
          Spacer(modifier = Modifier.weight(1f))
        }
      }

      SectionHeader("Others")
      DarkCard {
        Row(modifier = Modifier.padding(12.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
          IconTile("Buy Printers\nHere", Icons.Outlined.Sell, onClick = onBuyPrinters, modifier = Modifier.weight(1f))
          IconTile("Feedback", Icons.Outlined.Feedback, onClick = onFeedback, modifier = Modifier.weight(1f))
          IconTile("Contact Us", Icons.Outlined.SupportAgent, onClick = onContactUs, modifier = Modifier.weight(1f))
          Spacer(modifier = Modifier.weight(1f))
        }
      }

      SectionHeader("Account")
      DarkCard {
        Row(modifier = Modifier.padding(12.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
          IconTile("Subscription", Icons.Outlined.WorkspacePremium, onClick = onSubscription, modifier = Modifier.weight(1f))
          IconTile("Delete\nAccount", Icons.Outlined.DeleteForever, onClick = onDeleteAccount, modifier = Modifier.weight(1f))
          IconTile("Log Out", Icons.AutoMirrored.Outlined.Logout, onClick = onLogOut, modifier = Modifier.weight(1f))
          Spacer(modifier = Modifier.weight(1f))
        }
      }

      PremiumBanner(onClick = onUpgradePremium)
    }
  }
}

