package com.aslibill.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aslibill.ui.components.AsliTextField
import com.aslibill.ui.components.OrangeButton
import com.aslibill.ui.components.ScreenSurface
import com.aslibill.ui.theme.AsliColors
import com.aslibill.ui.theme.Brand
import com.aslibill.ui.theme.AppTypography
import com.aslibill.ui.theme.AppSpacing

import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import com.aslibill.R

@Composable
fun LoginScreen(
    vm: LoginViewModel,
    onLoginSuccess: () -> Unit,
    onGoToSignup: () -> Unit,
    contentPadding: PaddingValues
) {
    ScreenSurface {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // High-Fidelity Hero Illustration
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                val heroHeight = if (maxWidth > 600.dp) 320.dp else 240.dp
                Image(
                    painter = painterResource(id = R.drawable.header),
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth().height(heroHeight),
                    contentScale = ContentScale.Fit
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = AppSpacing.xl, vertical = AppSpacing.lg),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(AppSpacing.md)
            ) {
                Text(
                    text = "Welcome Back",
                    color = AsliColors.TextPrimary,
                    style = AppTypography.h1
                )
                Text(
                    text = "Sign in to ${Brand.AppName}",
                    color = AsliColors.TextSecondary,
                    style = AppTypography.bodyMedium
                )

                Spacer(modifier = Modifier.height(AppSpacing.lg))

                AsliTextField(
                    value = vm.phone,
                    onValueChange = { vm.phone = it },
                    label = "Phone Number",
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                )

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
                        style = AppTypography.bodySmall
                    )
                }

                Spacer(modifier = Modifier.height(AppSpacing.lg))

                OrangeButton(
                    text = if (vm.isLoading) "LOGGING IN..." else "LOGIN",
                    onClick = { vm.onLogin(onLoginSuccess) },
                    modifier = Modifier.fillMaxWidth()
                )

                TextButton(onClick = onGoToSignup) {
                    Text(
                        text = "Don't have an account? Sign Up",
                        color = AsliColors.PrimaryBlue,
                        style = AppTypography.bodyBold
                    )
                }
            }
        }
    }
}
