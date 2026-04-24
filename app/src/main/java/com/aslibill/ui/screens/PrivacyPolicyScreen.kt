package com.aslibill.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aslibill.ui.components.ScreenSurface
import com.aslibill.ui.theme.AppSpacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacyPolicyScreen(
    onBack: () -> Unit,
    contentPadding: PaddingValues
) {
    ScreenSurface {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Privacy Policy", fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = AppSpacing.lg)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(AppSpacing.md)
            ) {
                Spacer(modifier = Modifier.height(AppSpacing.md))
                
                PolicySection(
                    title = "Introduction",
                    content = "Welcome to AsliBill. We value your privacy and are committed to protecting your personal data. This Privacy Policy explains how we collect, use, and safeguard your information when you use our mobile application."
                )

                PolicySection(
                    title = "Information We Collect",
                    content = "1. Personal Information: When you register, we collect your name and phone number.\n" +
                            "2. Business Data: We store information about your products, inventory, customers, and transactions to provide billing services.\n" +
                            "3. Device Information: We may collect information about your device, including model and operating system, for performance and Bluetooth printer connectivity."
                )

                PolicySection(
                    title = "How We Use Your Information",
                    content = "We use the collected information to:\n" +
                            "• Provide and maintain our service\n" +
                            "• Manage your account and subscriptions\n" +
                            "• Process your billing transactions\n" +
                            "• Communicate with you regarding updates or support"
                )

                PolicySection(
                    title = "Data Security",
                    content = "We implement industry-standard security measures to protect your data. Your password is encrypted using secure hashing algorithms, and all communication with our servers is conducted over HTTPS."
                )

                PolicySection(
                    title = "Account Deletion",
                    content = "You have the right to delete your account and all associated data at any time through the 'Delete Account' option in the settings. Upon deletion, all your personal and business data will be permanently removed from our active databases."
                )

                PolicySection(
                    title = "Contact Us",
                    content = "If you have any questions about this Privacy Policy, please contact us through the 'Contact Us' section in the app."
                )

                Spacer(modifier = Modifier.height(48.dp))
            }
        }
    }
}

@Composable
private fun PolicySection(title: String, content: String) {
    Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.xs)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            ),
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = content,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
        )
        HorizontalDivider(
            modifier = Modifier.padding(vertical = AppSpacing.sm),
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
        )
    }
}
