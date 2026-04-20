package com.aslibill.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aslibill.ui.components.ScreenSurface
import com.aslibill.ui.components.DarkCard
import com.aslibill.ui.components.OrangeButton
import com.aslibill.ui.theme.AppTypography
import com.aslibill.ui.theme.AppSpacing
import com.aslibill.ui.theme.Brand
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.graphics.luminance
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.text.style.TextAlign

@Composable
fun OTPScreen(
    vm: OTPViewModel,
    phone: String,
    onVerifySuccess: () -> Unit,
    onBack: () -> Unit,
    contentPadding: PaddingValues
) {
    ScreenSurface {
        val configuration = LocalConfiguration.current
        val isDark = MaterialTheme.colorScheme.surface.luminance() < 0.5f
        val focusRequester = remember { FocusRequester() }

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

        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
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

                // Security Icon & Header
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .background(
                                color = if (isDark) Color(0xFF1E293B) else Color(0xFFDBEAFE),
                                shape = RoundedCornerShape(24.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Shield,
                            contentDescription = null,
                            tint = if (isDark) Color(0xFF60A5FA) else MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                    
                    Text(
                        text = "Verify Phone",
                        color = if (isDark) Color.White else MaterialTheme.colorScheme.onBackground,
                        style = AppTypography.h1.copy(fontWeight = FontWeight.Bold, fontSize = 28.sp)
                    )
                    
                    Text(
                        text = "Enter the 6-digit code sent to\n+91 $phone",
                        color = (if (isDark) Color.White else MaterialTheme.colorScheme.onBackground).copy(alpha = 0.6f),
                        style = AppTypography.bodyMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 32.dp),
                        lineHeight = 22.sp
                    )
                }

                Spacer(modifier = Modifier.height(48.dp))

                // Verification Card
                DarkCard(
                    modifier = Modifier
                        .widthIn(max = 480.dp)
                        .fillMaxWidth()
                        .padding(horizontal = AppSpacing.xl),
                    alpha = if (isDark) 0.4f else 0.9f
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        
                        // Premium Digit-based OTP Input
                        Box(contentAlignment = Alignment.Center) {
                            // Invisible TextField to capture input
                            BasicTextField(
                                value = vm.otpCode,
                                onValueChange = { if (it.length <= 6) vm.otpCode = it },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                                modifier = Modifier
                                    .focusRequester(focusRequester)
                                    .size(1.dp)
                                    .background(Color.Transparent),
                            )

                            // Displayed digit boxes
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                (0 until 6).forEach { index ->
                                    val char = vm.otpCode.getOrNull(index)?.toString() ?: ""
                                    val isFocused = vm.otpCode.length == index
                                    
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .aspectRatio(0.85f)
                                            .background(
                                                color = if (isDark) Color(0xFF1E293B) else Color(0xFFF1F5F9),
                                                shape = RoundedCornerShape(12.dp)
                                            )
                                            .border(
                                                width = if (isFocused) 2.dp else 1.dp,
                                                color = if (isFocused) {
                                                    if (isDark) Color(0xFF60A5FA) else MaterialTheme.colorScheme.primary
                                                } else {
                                                    (if (isDark) Color.White else MaterialTheme.colorScheme.onSurface).copy(alpha = 0.1f)
                                                },
                                                shape = RoundedCornerShape(12.dp)
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = char,
                                            style = MaterialTheme.typography.headlineSmall.copy(
                                                fontWeight = FontWeight.Black,
                                                fontSize = 24.sp
                                            ),
                                            color = if (isDark) Color.White else MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                        }

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

                        OrangeButton(
                            text = if (vm.isLoading) "VERIFYING..." else "CONFIRM",
                            onClick = { vm.onVerify(phone, onVerifySuccess) },
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Resend Section
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            if (!vm.canResend) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Timer,
                                        contentDescription = null,
                                        tint = (if (isDark) Color.White else MaterialTheme.colorScheme.onSurface).copy(alpha = 0.4f),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = "Resend in ${vm.resendTimer}s",
                                        color = (if (isDark) Color.White else MaterialTheme.colorScheme.onSurface).copy(alpha = 0.4f),
                                        style = AppTypography.bodySmall
                                    )
                                }
                            }

                            TextButton(
                                onClick = { vm.onResend(phone) },
                                enabled = vm.canResend
                            ) {
                                Text(
                                    text = "Didn't receive code? Resend",
                                    color = if (vm.canResend) {
                                        if (isDark) Color(0xFF60A5FA) else MaterialTheme.colorScheme.primary
                                    } else {
                                        (if (isDark) Color.White else MaterialTheme.colorScheme.onSurface).copy(alpha = 0.3f)
                                    },
                                    style = AppTypography.bodyBold.copy(fontSize = 14.sp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Test OTP Hint (Premium Style)
                if (vm.generatedOtp != null) {
                    Surface(
                        color = (if (isDark) Color(0xFF1E293B) else Color(0xFFF1F5F9)).copy(alpha = 0.5f),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.padding(horizontal = AppSpacing.xl)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Shield,
                                contentDescription = null,
                                tint = if (isDark) Color(0xFF60A5FA) else MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "Test OTP: ${vm.generatedOtp}",
                                style = AppTypography.bodyBold.copy(fontSize = 14.sp),
                                color = if (isDark) Color.White else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                TextButton(onClick = onBack) {
                    Text(
                        text = "Edit Phone Number",
                        color = (if (isDark) Color.White else MaterialTheme.colorScheme.onBackground).copy(alpha = 0.6f),
                        style = AppTypography.bodyMedium
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}
