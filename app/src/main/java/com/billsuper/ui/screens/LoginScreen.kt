package com.billsuper.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.PhoneAndroid
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.billsuper.ui.components.BillSuperTextField
import com.billsuper.ui.components.ScreenSurface
import com.billsuper.ui.components.DarkCard
import com.billsuper.ui.components.OrangeButton
import com.billsuper.ui.theme.Brand
import com.billsuper.ui.theme.AppTypography
import com.billsuper.ui.theme.AppSpacing
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.graphics.luminance

@Composable
fun LoginScreen(
    vm: LoginViewModel,
    onLoginSuccess: () -> Unit,
    onGoToSignup: () -> Unit,
    contentPadding: PaddingValues
) {
    ScreenSurface {
        val configuration = LocalConfiguration.current
        val isDark = MaterialTheme.colorScheme.surface.luminance() < 0.5f

        val bgGradient = Brush.verticalGradient(
            colors = listOf(
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                MaterialTheme.colorScheme.background
            )
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(bgGradient)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(80.dp))

                // Brand Header Section
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Verified,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(42.dp)
                        )
                        Text(
                            text = Brand.AppName,
                            color = MaterialTheme.colorScheme.onBackground,
                            style = AppTypography.h1.copy(fontSize = 36.sp, fontWeight = FontWeight.Black)
                        )
                    }
                    Text(
                        text = "Smart Billing for your business",
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                        style = AppTypography.bodySmall
                    )
                }

                Spacer(modifier = Modifier.height(56.dp))

                // Welcome Header
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Welcome Back",
                        color = MaterialTheme.colorScheme.onBackground,
                        style = AppTypography.h1.copy(fontWeight = FontWeight.Bold, fontSize = 28.sp)
                    )
                    Text(
                        text = "Sign in to continue",
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                        style = AppTypography.bodyMedium
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Form Card
                DarkCard(
                    modifier = Modifier
                        .widthIn(max = 480.dp)
                        .fillMaxWidth()
                        .padding(horizontal = AppSpacing.xl),
                    alpha = if (isDark) 0.4f else 0.9f
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        BillSuperTextField(
                            value = vm.phone,
                            onValueChange = { vm.phone = it },
                            label = "Phone Number",
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Outlined.PhoneAndroid,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            }
                        )

                        BillSuperTextField(
                            value = vm.password,
                            onValueChange = { vm.password = it },
                            label = "Password",
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Outlined.Lock,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            }
                        )

                        if (vm.error != null) {
                            Surface(
                                color = MaterialTheme.colorScheme.errorContainer,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = vm.error!!,
                                    color = MaterialTheme.colorScheme.onErrorContainer,
                                    style = AppTypography.bodySmall,
                                    modifier = Modifier.padding(AppSpacing.md),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        OrangeButton(
                            text = if (vm.isLoading) "LOGGING IN..." else "LOGIN",
                            onClick = { vm.onLogin(onLoginSuccess) },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                TextButton(onClick = onGoToSignup) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Don't have an account? ",
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                            style = AppTypography.bodyMedium
                        )
                        Text(
                            text = "Sign Up",
                            color = MaterialTheme.colorScheme.primary,
                            style = AppTypography.bodyBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }

            if (vm.isLoading) {
                com.billsuper.ui.components.BillSuperLoader()
            }
        }
    }
}


