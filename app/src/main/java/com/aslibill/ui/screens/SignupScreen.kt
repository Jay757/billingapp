package com.aslibill.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.animation.*
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.aslibill.ui.components.AsliTextField
import com.aslibill.ui.components.OrangeButton
import com.aslibill.ui.components.ScreenSurface
import com.aslibill.ui.components.DarkCard
import com.aslibill.ui.theme.AsliColors
import com.aslibill.ui.theme.Brand
import com.aslibill.ui.theme.AppTypography
import com.aslibill.ui.theme.AppSpacing
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp

@Composable
fun SignupScreen(
    vm: SignupViewModel,
    onSignupSuccess: () -> Unit,
    onGoToLogin: () -> Unit,
    contentPadding: PaddingValues
) {
    ScreenSurface {
        val configuration = LocalConfiguration.current
        val screenWidth = configuration.screenWidthDp.dp
        val isTablet = screenWidth >= 600.dp
        
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            AsliColors.PrimaryBlue.copy(alpha = 0.05f),
                            AsliColors.Bg
                        )
                    )
                )
        ) {
            // Background Decorative Circles - Scaled by screen size
            Box(
                modifier = Modifier
                    .size(screenWidth * 0.8f)
                    .offset(x = (-screenWidth * 0.3f), y = (-screenWidth * 0.3f))
                    .background(AsliColors.PrimaryBlue.copy(alpha = 0.03f), CircleShape)
            )
            
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding)
                    .padding(horizontal = (if (isTablet) screenWidth * 0.1f else AppSpacing.xl).coerceAtLeast(AppSpacing.xl))
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = Brand.AppName,
                    color = AsliColors.PrimaryBlue,
                    style = if (isTablet) AppTypography.h1.copy(fontSize = 40.sp) else AppTypography.h1
                )
                
                Spacer(modifier = Modifier.height(AppSpacing.sm))
                
                Text(
                    text = "Smart Billing for your business",
                    color = AsliColors.TextSecondary,
                    style = AppTypography.bodyMedium,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(if (isTablet) 64.dp else 48.dp))

                DarkCard(
                    modifier = Modifier.widthIn(max = 500.dp).fillMaxWidth(),
                    alpha = 0.9f
                ) {
                    Column(
                        modifier = Modifier.padding(AppSpacing.xl),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Create Account",
                            color = AsliColors.TextPrimary,
                            style = AppTypography.h2
                        )
                        
                        Text(
                            text = "Join thousands of businesses today",
                            color = AsliColors.TextSecondary,
                            style = AppTypography.bodySmall
                        )

                        Spacer(modifier = Modifier.height(AppSpacing.xl))

                        AsliTextField(
                            value = vm.name,
                            onValueChange = { vm.name = it },
                            label = "Full Name",
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        AsliTextField(
                            value = vm.phone,
                            onValueChange = { vm.phone = it },
                            label = "Phone Number",
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        AsliTextField(
                            value = vm.password,
                            onValueChange = { vm.password = it },
                            label = "Password",
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                        )

                        if (vm.error != null) {
                            Text(
                                text = vm.error!!,
                                color = AsliColors.AlertOrange,
                                style = AppTypography.bodySmall,
                                modifier = Modifier.padding(top = AppSpacing.md)
                            )
                        }

                        Spacer(modifier = Modifier.height(AppSpacing.xl))

                        OrangeButton(
                            text = if (vm.isLoading) "CREATING ACCOUNT..." else "GET STARTED",
                            onClick = { vm.onSignup(onSignupSuccess) },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                TextButton(onClick = onGoToLogin) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Already have an account? ", color = AsliColors.TextSecondary, style = AppTypography.bodyMedium)
                        Text("Login", color = AsliColors.PrimaryBlue, style = AppTypography.bodyBold)
                    }
                }
            }
        }
    }
}
