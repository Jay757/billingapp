package com.aslibill.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
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
import com.aslibill.ui.components.AsliTextField
import com.aslibill.ui.components.ScreenSurface
import com.aslibill.ui.components.DarkCard
import com.aslibill.ui.theme.Brand
import com.aslibill.ui.theme.AppTypography
import com.aslibill.ui.theme.AppSpacing
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.graphics.luminance

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
        val isDark = MaterialTheme.colorScheme.surface.luminance() < 0.5f

        val bgGradient = if (isDark) {
            Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF0F172A),
                    Color(0xFF020617)
                )
            )
        } else {
            Brush.verticalGradient(
                colors = listOf(
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
                    MaterialTheme.colorScheme.background
                )
            )
        }

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
                Spacer(modifier = Modifier.height(64.dp))

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
                            tint = if (isDark) Color(0xFF60A5FA) else MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(42.dp)
                        )
                        Text(
                            text = Brand.AppName,
                            color = if (isDark) Color.White else MaterialTheme.colorScheme.onBackground,
                            style = AppTypography.h1.copy(fontSize = 36.sp, fontWeight = FontWeight.Black)
                        )
                    }
                    Text(
                        text = "Smart Billing for your business",
                        color = (if (isDark) Color.White else MaterialTheme.colorScheme.onBackground).copy(alpha = 0.5f),
                        style = AppTypography.bodySmall
                    )
                }

                Spacer(modifier = Modifier.height(48.dp))

                // Welcome Header
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Create Account",
                        color = if (isDark) Color.White else MaterialTheme.colorScheme.onBackground,
                        style = AppTypography.h1.copy(fontWeight = FontWeight.Bold, fontSize = 28.sp)
                    )
                    Text(
                        text = "Join thousands of businesses today",
                        color = (if (isDark) Color.White else MaterialTheme.colorScheme.onBackground).copy(alpha = 0.6f),
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
                        AsliTextField(
                            value = vm.name,
                            onValueChange = { vm.name = it },
                            label = "Full Name",
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Outlined.Person,
                                    contentDescription = null,
                                    tint = (if (isDark) Color.White else MaterialTheme.colorScheme.onSurface).copy(alpha = 0.6f)
                                )
                            }
                        )

                        AsliTextField(
                            value = vm.phone,
                            onValueChange = { vm.phone = it },
                            label = "Phone Number",
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            leadingIcon = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(start = 12.dp, end = 4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.PhoneAndroid,
                                        contentDescription = null,
                                        tint = (if (isDark) Color.White else MaterialTheme.colorScheme.onSurface).copy(alpha = 0.6f)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "+91", 
                                        color = if (isDark) Color.White else MaterialTheme.colorScheme.onSurface, 
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Box(
                                        modifier = Modifier
                                            .width(1.dp)
                                            .height(20.dp)
                                            .background((if (isDark) Color.White else MaterialTheme.colorScheme.onSurface).copy(alpha = 0.2f))
                                    )
                                }
                            }
                        )

                        AsliTextField(
                            value = vm.password,
                            onValueChange = { vm.password = it },
                            label = "Password",
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Outlined.Lock,
                                    contentDescription = null,
                                    tint = (if (isDark) Color.White else MaterialTheme.colorScheme.onSurface).copy(alpha = 0.6f)
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

                        Button(
                            onClick = { vm.onSignup(onSignupSuccess) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isDark) Color(0xFF3B82F6) else MaterialTheme.colorScheme.primary,
                                contentColor = Color.White
                            )
                        ) {
                            Text(
                                text = if (vm.isLoading) "CREATING ACCOUNT..." else "GET STARTED",
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Black, fontSize = 16.sp, letterSpacing = 1.sp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                TextButton(onClick = onGoToLogin) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Already have an account? ",
                            color = (if (isDark) Color.White else MaterialTheme.colorScheme.onBackground).copy(alpha = 0.6f),
                            style = AppTypography.bodyMedium
                        )
                        Text(
                            text = "Login",
                            color = if (isDark) Color(0xFF60A5FA) else MaterialTheme.colorScheme.primary,
                            style = AppTypography.bodyBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}
