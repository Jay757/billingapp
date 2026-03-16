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
                .padding(contentPadding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Welcome Back",
                color = AsliColors.TextPrimary,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Sign in to continue",
                color = AsliColors.TextSecondary,
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(40.dp))

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
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )

            if (vm.error != null) {
                Text(
                    text = vm.error!!,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            OrangeButton(
                text = if (vm.isLoading) "Logging in..." else "Login",
                onClick = { vm.onLogin(onLoginSuccess) },
                modifier = Modifier.fillMaxWidth().height(50.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = onGoToSignup) {
                Text("Don't have an account? Sign Up", color = AsliColors.Orange)
            }
        }
    }
}
