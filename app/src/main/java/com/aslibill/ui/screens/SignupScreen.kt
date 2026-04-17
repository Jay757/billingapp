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
        val screenHeight = configuration.screenHeightDp.dp
        val isTablet = screenWidth >= 600.dp
        
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            AsliColors.Primary.copy(alpha = 0.05f),
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
                    .background(AsliColors.Primary.copy(alpha = 0.03f), CircleShape)
            )
            
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding)
                    .padding(horizontal = (if (isTablet) screenWidth * 0.1f else 24.dp).coerceAtLeast(24.dp))
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = Brand.AppName,
                    color = AsliColors.Primary,
                    style = (if (isTablet) MaterialTheme.typography.displayLarge else MaterialTheme.typography.displaySmall).copy(
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = (-2).sp
                    )
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "Smart Billing for your business",
                    color = AsliColors.TextSecondary,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(if (isTablet) 64.dp else 48.dp))

                DarkCard(
                    modifier = Modifier.widthIn(max = 500.dp).fillMaxWidth(),
                    alpha = 0.9f
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Create Account",
                            color = AsliColors.TextPrimary,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        
                        Text(
                            text = "Join thousands of businesses today",
                            color = AsliColors.TextSecondary,
                            style = MaterialTheme.typography.bodySmall
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        AsliTextField(
                            value = vm.name,
                            onValueChange = { vm.name = it },
                            label = "Full Name"
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        AsliTextField(
                            value = vm.phone,
                            onValueChange = { vm.phone = it },
                            label = "Phone Number",
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        AsliTextField(
                            value = vm.password,
                            onValueChange = { vm.password = it },
                            label = "Password",
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                        )

                        if (vm.error != null) {
                            Text(
                                text = vm.error!!,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(top = 12.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        OrangeButton(
                            text = if (vm.isLoading) "Creating Account..." else "Get Started",
                            onClick = { vm.onSignup(onSignupSuccess) },
                            modifier = Modifier.fillMaxWidth().height(56.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                TextButton(onClick = onGoToLogin) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Already have an account? ", color = AsliColors.TextSecondary)
                        Text("Login", color = AsliColors.Primary, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
