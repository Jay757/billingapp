package com.aslibill.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.aslibill.ui.components.OrangeButton
import com.aslibill.ui.components.ScreenSurface
import com.aslibill.ui.components.SectionHeader
import com.aslibill.ui.theme.AppSpacing
import com.aslibill.ui.theme.AsliColors

@Composable
fun DeleteAccountScreen(
    contentPadding: PaddingValues,
    vm: DeleteAccountViewModel
) {
    val reason by vm.reason.collectAsState()
    val isDeleting by vm.isDeleting.collectAsState()
    val deletionRequested by vm.deletionRequested.collectAsState()

    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = AsliColors.Orange,
        unfocusedBorderColor = AsliColors.TextSecondary,
        focusedLabelColor = AsliColors.Orange,
        unfocusedLabelColor = AsliColors.TextSecondary,
        focusedTextColor = AsliColors.TextPrimary,
        unfocusedTextColor = AsliColors.TextPrimary,
        cursorColor = AsliColors.Orange
    )

    ScreenSurface {
        androidx.compose.foundation.layout.Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding)
                    .padding(AppSpacing.lg),
                verticalArrangement = Arrangement.spacedBy(AppSpacing.lg),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                SectionHeader("Delete Account")

                if (deletionRequested) {
                    Text(
                        "Account Deletion Requested!",
                        color = AsliColors.Orange,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        "Your request is being processed. Our team will contact you within 24-48 hours to confirm the deletion.",
                        color = AsliColors.TextPrimary,
                        textAlign = TextAlign.Center
                    )
                } else {
                    Icon(
                        Icons.Outlined.Warning,
                        contentDescription = null,
                        tint = AsliColors.Orange,
                        modifier = Modifier.size(64.dp)
                    )

                    Text(
                        "Warning: Permanent Action",
                        color = AsliColors.Orange,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        "Deleting your account will permanently remove all your data, including bills, inventory, and reports. This action cannot be undone.",
                        color = AsliColors.TextPrimary,
                        textAlign = TextAlign.Center
                    )

                    OutlinedTextField(
                        value = reason,
                        onValueChange = { vm.onReasonChange(it) },
                        label = { Text("Reason for Deletion (Optional)") },
                        colors = fieldColors,
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )

                    OrangeButton(
                        "DELETE MY ACCOUNT",
                        onClick = { vm.requestDeletion() },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            if (isDeleting) {
                com.aslibill.ui.components.AsliLoader()
            }
        }
    }
}
