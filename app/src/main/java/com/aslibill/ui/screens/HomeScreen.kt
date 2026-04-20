package com.aslibill.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.automirrored.outlined.ReceiptLong
import androidx.compose.material.icons.automirrored.outlined.ShowChart
import androidx.compose.material.icons.automirrored.outlined.TrendingUp
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aslibill.R
import com.aslibill.ui.components.*
import com.aslibill.ui.theme.AsliColors
import com.aslibill.ui.theme.AppTypography
import com.aslibill.ui.theme.AppSpacing
import java.util.Locale

private data class HomeTile(
  val label: String,
  val icon: ImageVector,
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
  onFeedback: () -> Unit,
  onContactUs: () -> Unit,
  onSubscription: () -> Unit,
  onDeleteAccount: () -> Unit,
  onLogOut: () -> Unit,
  userName: String,
  userPhone: String,
  contentPadding: PaddingValues,
  homeVm: HomeViewModel
) {
  val todaySales by homeVm.todaySalesTotal.collectAsState()
  val todayBillCount by homeVm.todayBillCount.collectAsState()
  
  ScreenSurface {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(contentPadding)
        .padding(horizontal = AppSpacing.lg)
        .verticalScroll(rememberScrollState()),
      verticalArrangement = Arrangement.spacedBy(AppSpacing.lg)
    ) {
      Spacer(modifier = Modifier.height(12.dp))
      
      // Full Background Header
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = AppSpacing.sm)
          .height(180.dp)
          .clip(RoundedCornerShape(28.dp))
      ) {
        // Illustration as Full Background Cover
        Image(
          painter = painterResource(id = R.drawable.header),
          contentDescription = null,
          contentScale = ContentScale.Crop,
          modifier = Modifier.fillMaxSize()
        )
        
        // Soft Scrim for Readability (Now limited to text area)
        Box(
          modifier = Modifier
            .fillMaxWidth(0.7f)
            .fillMaxHeight()
            .background(
              Brush.horizontalGradient(
                colors = listOf(
                  MaterialTheme.colorScheme.background.copy(alpha = 0.95f),
                  MaterialTheme.colorScheme.background.copy(alpha = 0.8f),
                  Color.Transparent
                )
              )
            )
        )
        
        // Greeting Text on Top
        Column(
          modifier = Modifier
            .fillMaxHeight()
            .padding(AppSpacing.lg),
          verticalArrangement = Arrangement.Center
        ) {
          Text(
            text = "Hello, ${userName.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}!",
            style = MaterialTheme.typography.headlineMedium.copy(
              fontWeight = FontWeight.Black,
              fontSize = 30.sp
            ),
            color = MaterialTheme.colorScheme.onBackground
          )
          Text(
            text = userPhone,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
            fontWeight = FontWeight.SemiBold
          )
        }
      }


      Spacer(modifier = Modifier.height(4.dp))

      // Stats Section
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(AppSpacing.lg)
      ) {
        StatsCard(
          label = "Today's Sales",
          value = "₹ ${todaySales.toInt()}",
          icon = Icons.AutoMirrored.Outlined.TrendingUp,
          color = AsliColors.SuccessGreen,
          modifier = Modifier.weight(1f)
        )
        StatsCard(
          label = "Total Bills",
          value = "$todayBillCount",
          icon = Icons.AutoMirrored.Outlined.ReceiptLong,
          color = MaterialTheme.colorScheme.primary,
          modifier = Modifier.weight(1f)
        )
      }


      // Quick Actions
      SectionHeader("Quick Actions")
      DarkCard(modifier = Modifier.fillMaxWidth()) {
        HomeGridSection(
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

      // Reports
      SectionHeader("Reports")
      DarkCard(modifier = Modifier.fillMaxWidth()) {
        HomeGridSection(
          tiles = listOf(
            HomeTile("Bill Report", Icons.Outlined.Description, onReports),
            HomeTile("Item Wise Sales", Icons.AutoMirrored.Outlined.ShowChart, onItemWiseSalesReport),
            HomeTile("Day Report", Icons.Outlined.Today, onDayReport),
            HomeTile("Sales Summary", Icons.Outlined.BarChart, onSalesSummary)
          )
        )
      }

      // Settings & Others
      SectionHeader("Settings & More")
      DarkCard(modifier = Modifier.fillMaxWidth()) {
        HomeGridSection(
          tiles = listOf(
            HomeTile("Printers", Icons.Outlined.Bluetooth, onBluetoothPrinter),
            HomeTile("Settings", Icons.Outlined.Print, onPrintSettings),
            HomeTile("Feedback", Icons.Outlined.Feedback, onFeedback),
            HomeTile("Contact Us", Icons.Outlined.SupportAgent, onContactUs),
            HomeTile("Log Out", Icons.AutoMirrored.Outlined.Logout, onLogOut)
          )
        )
      }

      PremiumBanner(onClick = onUpgradePremium)
      Spacer(modifier = Modifier.height(24.dp))
    }
  }
}

@Composable
private fun HomeGridSection(tiles: List<HomeTile>) {
  androidx.compose.foundation.lazy.grid.LazyVerticalGrid(
    columns = androidx.compose.foundation.lazy.grid.GridCells.Adaptive(minSize = 150.dp),
    modifier = Modifier.fillMaxWidth().heightIn(max = 1000.dp).padding(AppSpacing.sm),
    horizontalArrangement = Arrangement.spacedBy(AppSpacing.xs),
    verticalArrangement = Arrangement.spacedBy(AppSpacing.xs),
    userScrollEnabled = false
  ) {
    items(tiles.size) { index ->
      val tile = tiles[index]
      IconTile(
        label = tile.label,
        icon = tile.icon,
        onClick = tile.onClick,
        modifier = Modifier.fillMaxWidth()
      )
    }
  }
}
