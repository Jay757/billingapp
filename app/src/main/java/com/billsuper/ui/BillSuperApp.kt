package com.billsuper.ui

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.platform.LocalContext
import com.billsuper.ui.screens.BluetoothPrinterScreen
import com.billsuper.ui.screens.HomeScreen
import com.billsuper.ui.screens.InventoryScreen
import com.billsuper.ui.screens.ItemWiseBillScreen
import com.billsuper.ui.screens.PrintSettingsScreen
import com.billsuper.ui.screens.UpgradePremiumScreen
import com.billsuper.ui.screens.UpgradePremiumViewModel
import com.billsuper.ui.screens.PrintSettingsViewModel
import com.billsuper.ui.screens.PrintSettingsViewModelFactory
import com.billsuper.ui.screens.QuickBillScreen
import com.billsuper.ui.screens.ReportsScreen
import com.billsuper.BillSuperApplication
import com.billsuper.ui.screens.InventoryViewModelFactory
import com.billsuper.ui.screens.ItemWiseBillViewModelFactory
import com.billsuper.ui.screens.ReportsViewModelFactory
import com.billsuper.ui.screens.QuickBillViewModelFactory
import androidx.lifecycle.viewmodel.compose.viewModel
import com.billsuper.ui.screens.HomeViewModel
import com.billsuper.ui.screens.HomeViewModelFactory
import com.billsuper.ui.screens.BluetoothPrinterViewModel
import com.billsuper.ui.screens.BluetoothPrinterViewModelFactory
import com.billsuper.ui.screens.StaffManagementScreen
import com.billsuper.ui.screens.StaffManagementViewModel
import com.billsuper.ui.screens.StaffManagementViewModelFactory
import com.billsuper.ui.screens.CustomerManagementScreen
import com.billsuper.ui.screens.CustomerManagementViewModel
import com.billsuper.ui.screens.CustomerManagementViewModelFactory
import com.billsuper.ui.screens.CreditDetailsScreen
import com.billsuper.ui.screens.CreditDetailsViewModel
import com.billsuper.ui.screens.CreditDetailsViewModelFactory
import com.billsuper.ui.screens.CashManagementScreen
import com.billsuper.ui.screens.CashManagementViewModel
import com.billsuper.ui.screens.CashManagementViewModelFactory
import com.billsuper.ui.screens.ItemWiseSalesReportScreen
import com.billsuper.ui.screens.ItemWiseSalesReportViewModel
import com.billsuper.ui.screens.ItemWiseSalesReportViewModelFactory
import com.billsuper.ui.screens.DayReportScreen
import com.billsuper.ui.screens.DayReportViewModel
import com.billsuper.ui.screens.DayReportViewModelFactory
import com.billsuper.ui.screens.SalesSummaryScreen
import com.billsuper.ui.screens.SalesSummaryViewModel
import com.billsuper.ui.screens.SalesSummaryViewModelFactory
import com.billsuper.ui.screens.FeedbackScreen
import com.billsuper.ui.screens.FeedbackViewModel
import com.billsuper.ui.screens.FeedbackViewModelFactory
import com.billsuper.ui.screens.TrainingVideoScreen
import com.billsuper.ui.screens.TrainingVideoViewModel
import com.billsuper.ui.screens.ContactUsScreen
import com.billsuper.ui.screens.ContactUsViewModel
import com.billsuper.ui.screens.SubscriptionScreen
import com.billsuper.ui.screens.SubscriptionViewModel
import com.billsuper.ui.screens.SubscriptionViewModelFactory
import com.billsuper.ui.screens.DeleteAccountScreen
import com.billsuper.ui.screens.DeleteAccountViewModel
import com.billsuper.ui.screens.DeleteAccountViewModelFactory
import com.billsuper.ui.screens.BuyPrintersScreen
import com.billsuper.ui.screens.BuyPrintersViewModel
import com.billsuper.ui.screens.LoginScreen
import com.billsuper.ui.screens.LoginViewModel
import com.billsuper.ui.screens.LoginViewModelFactory
import com.billsuper.ui.screens.SignupScreen
import com.billsuper.ui.screens.SignupViewModel
import com.billsuper.ui.screens.SignupViewModelFactory
import com.billsuper.ui.screens.OTPScreen
import com.billsuper.ui.screens.OTPSuccessScreen
import com.billsuper.ui.screens.OTPViewModel
import com.billsuper.ui.screens.OTPViewModelFactory
import com.billsuper.ui.screens.PrivacyPolicyScreen
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import kotlinx.coroutines.launch

@Composable
fun BillSuperApp() {
  val navController = rememberNavController()
  val context = LocalContext.current
  val app = context.applicationContext as BillSuperApplication
  val userSession = app.container.authRepository.userSession.collectAsState(initial = app.container.authRepository.userSession.value).value
  val scope = rememberCoroutineScope()

  // Stabilize start destination to prevent NavHost resets when session state changes mid-flow
  val startDestination = remember {
    if (app.container.authRepository.userSession.value != null) Routes.Home else Routes.Welcome
  }

  val isOnline = app.container.networkStatusRepository.isOnline.collectAsState().value

  // Monitor for Logout and redirect to Welcome
  LaunchedEffect(userSession) {
    if (userSession == null) {
      val currentRoute = navController.currentDestination?.route
      if (currentRoute != Routes.Login && currentRoute != Routes.Signup && currentRoute != Routes.Welcome) {
        navController.navigate(Routes.Welcome) {
          popUpTo(0) { inclusive = true }
        }
      }
    }
  }

  Scaffold(
    topBar = { }
  ) { padding ->
    NavHost(
      navController = navController,
      startDestination = startDestination,
      modifier = Modifier
    ) {
      composable(Routes.Home) {
        val homeVm: HomeViewModel = viewModel(factory = HomeViewModelFactory(app.container.billingRepository))
        HomeScreen(
          onQuickBill = { navController.navigate(Routes.QuickBill) { launchSingleTop = true } },
          onItemWiseBill = { navController.navigate(Routes.ItemWiseBill) { launchSingleTop = true } },
          onInventory = { navController.navigate(Routes.Inventory) { launchSingleTop = true } },
          onReports = { navController.navigate(Routes.Reports) { launchSingleTop = true } },
          onBluetoothPrinter = { navController.navigate(Routes.BluetoothPrinter) { launchSingleTop = true } },
          onPrintSettings = { navController.navigate(Routes.PrintSettings) { launchSingleTop = true } },
          onStaffManagement = { navController.navigate(Routes.StaffManagement) { launchSingleTop = true } },
          onCustomerManagement = { navController.navigate(Routes.CustomerManagement) { launchSingleTop = true } },
          onCreditDetails = { navController.navigate(Routes.CreditDetails) { launchSingleTop = true } },
          onCashManagement = { navController.navigate(Routes.CashManagement) { launchSingleTop = true } },
          onItemWiseSalesReport = { navController.navigate(Routes.ItemWiseSalesReport) { launchSingleTop = true } },
          onDayReport = { navController.navigate(Routes.DayReport) { launchSingleTop = true } },
          onSalesSummary = { navController.navigate(Routes.SalesSummary) { launchSingleTop = true } },
          onUpgradePremium = { navController.navigate(Routes.UpgradePremium) { launchSingleTop = true } },
          onTrainingVideo = { navController.navigate(Routes.TrainingVideo) { launchSingleTop = true } },
          onFeedback = { navController.navigate(Routes.Feedback) { launchSingleTop = true } },
          onContactUs = { navController.navigate(Routes.ContactUs) { launchSingleTop = true } },
          onSubscription = { navController.navigate(Routes.Subscription) { launchSingleTop = true } },
          onDeleteAccount = { navController.navigate(Routes.DeleteAccount) { launchSingleTop = true } },
          onPrivacyPolicy = { navController.navigate(Routes.PrivacyPolicy) { launchSingleTop = true } },
          onLogOut = {
            scope.launch {
                app.container.performLogout()
                navController.navigate(Routes.Login) {
                    popUpTo(0) { inclusive = true }
                    launchSingleTop = true
                }
            }
          },
          userName = userSession?.name ?: "User",
          userPhone = userSession?.phone ?: "No Phone",
          contentPadding = padding,
          homeVm = homeVm
        )
      }
      composable(Routes.Login) {
        val vm: LoginViewModel = viewModel(factory = LoginViewModelFactory(app.container.authRepository))
        LoginScreen(
          vm = vm,
          onLoginSuccess = {
            navController.navigate(Routes.Home) {
              popUpTo(Routes.Login) { inclusive = true }
            }
          },
          onGoToSignup = { navController.navigate(Routes.Signup) { launchSingleTop = true } },
          contentPadding = padding
        )
      }
      composable(Routes.Signup) {
        val vm: SignupViewModel = viewModel(factory = SignupViewModelFactory(app.container.authRepository))
        SignupScreen(
          vm = vm,
          onSignupSuccess = { code: String? ->
            navController.navigate(Routes.OTP + "/${vm.phone}?code=$code")
          },
          onGoToLogin = { navController.navigate(Routes.Login) { launchSingleTop = true } },
          onPrivacyPolicy = { navController.navigate(Routes.PrivacyPolicy) { launchSingleTop = true } },
          contentPadding = padding
        )
      }
      composable(
          route = Routes.OTP + "/{phone}?code={code}",
          arguments = listOf(
              androidx.navigation.navArgument("phone") { type = androidx.navigation.NavType.StringType },
              androidx.navigation.navArgument("code") { 
                  type = androidx.navigation.NavType.StringType
                  nullable = true
                  defaultValue = null
              }
          )
      ) { backStackEntry ->
        val phone = backStackEntry.arguments?.getString("phone") ?: ""
        val initialCode = backStackEntry.arguments?.getString("code")
        val vm: OTPViewModel = viewModel(factory = OTPViewModelFactory(app.container.authRepository))
        
        // Initialize with code from signup if present
        LaunchedEffect(initialCode) {
            if (initialCode != null) {
                vm.generatedOtp = initialCode
            }
        }

        OTPScreen(
          vm = vm,
          phone = phone,
          onVerifySuccess = {
            navController.navigate(Routes.OTPSuccess) {
              // Pop up to current screen to clear it, but dont reset the whole graph
              popUpTo(backStackEntry.destination.id) { inclusive = true }
            }
          },
          onBack = { navController.popBackStack() },
          contentPadding = padding
        )
      }
      composable(Routes.OTPSuccess) {
        OTPSuccessScreen(
          onContinue = {
            navController.navigate(Routes.Home) {
              popUpTo(0) { inclusive = true }
              launchSingleTop = true
            }
          },
          contentPadding = padding
        )
      }
      composable(Routes.QuickBill) {
        val vm: com.billsuper.ui.screens.QuickBillViewModel = viewModel(
          factory = QuickBillViewModelFactory(app.container.billingRepository)
        )
        val btVm: BluetoothPrinterViewModel = viewModel(factory = BluetoothPrinterViewModelFactory(app))
        QuickBillScreen(
          contentPadding = padding,
          vm = vm,
          btVm = btVm,
          onGoReport = { 
            navController.navigate(Routes.Reports) {
                popUpTo(Routes.Home)
                launchSingleTop = true
            }
          },
          onGoItemWise = { 
            navController.navigate(Routes.ItemWiseBill) {
                popUpTo(Routes.Home)
                launchSingleTop = true
            }
          }
        )
      }
      composable(Routes.ItemWiseBill) {
        val vm: com.billsuper.ui.screens.ItemWiseBillViewModel = viewModel(
          factory = ItemWiseBillViewModelFactory(
            inventory = app.container.inventoryRepository,
            billing = app.container.billingRepository
          )
        )
        val btVm: BluetoothPrinterViewModel = viewModel(factory = BluetoothPrinterViewModelFactory(app))
        ItemWiseBillScreen(
          contentPadding = padding,
          vm = vm,
          btVm = btVm,
          onGoReport = { 
            navController.navigate(Routes.Reports) {
                popUpTo(Routes.Home)
                launchSingleTop = true
            }
          },
          onGoQuickBill = { 
            navController.navigate(Routes.QuickBill) {
                popUpTo(Routes.Home)
                launchSingleTop = true
            }
          }
        )
      }
      composable(Routes.Inventory) {
        val vm: com.billsuper.ui.screens.InventoryViewModel = viewModel(
          factory = InventoryViewModelFactory(app.container.inventoryRepository)
        )
        InventoryScreen(contentPadding = padding, vm = vm)
      }
      composable(Routes.Reports) {
        val vm: com.billsuper.ui.screens.ReportsViewModel = viewModel(
          factory = ReportsViewModelFactory(app.container.billingRepository)
        )
        val btVm: BluetoothPrinterViewModel = viewModel(factory = BluetoothPrinterViewModelFactory(app))
        ReportsScreen(
          contentPadding = padding,
          vm = vm,
          btVm = btVm,
          onGoQuickBill = { 
            navController.navigate(Routes.QuickBill) {
                popUpTo(Routes.Home)
                launchSingleTop = true
            }
          },
          onGoItemWise = { 
            navController.navigate(Routes.ItemWiseBill) {
                popUpTo(Routes.Home)
                launchSingleTop = true
            }
          }
        )
      }
      composable(Routes.BluetoothPrinter) {
        val vm: BluetoothPrinterViewModel = viewModel(factory = BluetoothPrinterViewModelFactory(app))
        BluetoothPrinterScreen(contentPadding = padding, vm = vm)
      }
      composable(Routes.PrintSettings) {
        val vm: PrintSettingsViewModel = viewModel(factory = PrintSettingsViewModelFactory(app.container.settingsRepository))
        PrintSettingsScreen(contentPadding = padding, vm = vm)
      }

      composable(Routes.StaffManagement) {
        val vm: StaffManagementViewModel = viewModel(factory = StaffManagementViewModelFactory(app.container.staffRepository))
        StaffManagementScreen(contentPadding = padding, vm = vm)
      }
      composable(Routes.CustomerManagement) {
        val vm: CustomerManagementViewModel = viewModel(factory = CustomerManagementViewModelFactory(app.container.customerRepository))
        CustomerManagementScreen(contentPadding = padding, vm = vm)
      }
      composable(Routes.CreditDetails) {
        val vm: CreditDetailsViewModel = viewModel(factory = CreditDetailsViewModelFactory(app.container.analyticsRepository))
        CreditDetailsScreen(contentPadding = padding, vm = vm)
      }
      composable(Routes.CashManagement) {
        val vm: CashManagementViewModel = viewModel(factory = CashManagementViewModelFactory(app.container.cashRepository))
        CashManagementScreen(contentPadding = padding, vm = vm)
      }
      composable(Routes.ItemWiseSalesReport) {
        val vm: ItemWiseSalesReportViewModel = viewModel(factory = ItemWiseSalesReportViewModelFactory(app.container.analyticsRepository))
        ItemWiseSalesReportScreen(contentPadding = padding, vm = vm)
      }
      composable(Routes.DayReport) {
        val vm: DayReportViewModel = viewModel(factory = DayReportViewModelFactory(app.container.analyticsRepository))
        DayReportScreen(contentPadding = padding, vm = vm)
      }
      composable(Routes.SalesSummary) {
        val vm: SalesSummaryViewModel = viewModel(factory = SalesSummaryViewModelFactory(app.container.analyticsRepository))
        SalesSummaryScreen(contentPadding = padding, vm = vm)
      }
      
      // Placeholder routes for new features
      composable(Routes.UpgradePremium) {
        val vm: UpgradePremiumViewModel = viewModel()
        UpgradePremiumScreen(
          contentPadding = padding,
          vm = vm,
          onBack = { navController.popBackStack() }
        )
      }
      composable(Routes.TrainingVideo) {
        val vm: TrainingVideoViewModel = viewModel()
        TrainingVideoScreen(contentPadding = padding, vm = vm)
      }
      composable(Routes.Feedback) {
        val vm: FeedbackViewModel = viewModel(factory = FeedbackViewModelFactory(app.container.authRepository))
        FeedbackScreen(contentPadding = padding, vm = vm)
      }
      composable(Routes.ContactUs) {
        val vm: ContactUsViewModel = viewModel()
        ContactUsScreen(contentPadding = padding, vm = vm)
      }
      composable(Routes.Subscription) {
        val vm: SubscriptionViewModel = viewModel(factory = SubscriptionViewModelFactory(app.container.authRepository))
        SubscriptionScreen(contentPadding = padding, vm = vm)
      }
      composable(Routes.DeleteAccount) {
        val vm: DeleteAccountViewModel = viewModel(factory = DeleteAccountViewModelFactory(app.container.authRepository))
        DeleteAccountScreen(contentPadding = padding, vm = vm)
      }
      composable(Routes.BuyPrinters) {
        val vm: BuyPrintersViewModel = viewModel()
        BuyPrintersScreen(contentPadding = padding, vm = vm)
      }
      composable(Routes.PrivacyPolicy) {
        PrivacyPolicyScreen(
          onBack = { navController.popBackStack() },
          contentPadding = padding
        )
      }
      composable(Routes.Welcome) {
        com.billsuper.ui.screens.WelcomeScreen(
          onGetStarted = { 
            navController.navigate(Routes.Login) {
              popUpTo(Routes.Welcome) { inclusive = true }
            }
          },
          onGoToLogin = { 
            navController.navigate(Routes.Login) {
              popUpTo(Routes.Welcome) { inclusive = true }
            }
          }
        )
      }
    }
  }
}

@Composable
fun PlaceholderScreen(title: String, contentPadding: androidx.compose.foundation.layout.PaddingValues) {
  com.billsuper.ui.components.ScreenSurface {
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(contentPadding),
      contentAlignment = Alignment.Center
    ) {
      Text(
        text = "$title Feature Coming Soon",
        style = MaterialTheme.typography.headlineMedium,
        color = com.billsuper.ui.theme.BillSuperColors.TextPrimary
      )
    }
  }
}

object Routes {
  const val Home = "home"
  const val QuickBill = "quickBill"
  const val ItemWiseBill = "itemWiseBill"
  const val Inventory = "inventory"
  const val Reports = "reports"
  const val BluetoothPrinter = "bluetoothPrinter"
  const val PrintSettings = "printSettings"
  const val StaffManagement = "staffManagement"
  const val CustomerManagement = "customerManagement"
  const val CreditDetails = "creditDetails"
  const val CashManagement = "cashManagement"
  const val ItemWiseSalesReport = "itemWiseSalesReport"
  const val DayReport = "dayReport"
  const val SalesSummary = "salesSummary"
  const val UpgradePremium = "upgradePremium"
  const val TrainingVideo = "trainingVideo"
  const val Feedback = "feedback"
  const val ContactUs = "contactUs"
  const val Subscription = "subscription"
  const val DeleteAccount = "deleteAccount"
  const val BuyPrinters = "buyPrinters"
  const val PrivacyPolicy = "privacyPolicy"
  const val Login = "login"
  const val Signup = "signup"
  const val OTP = "otp"
  const val OTPSuccess = "otp-success"
  const val Welcome = "welcome"
}


