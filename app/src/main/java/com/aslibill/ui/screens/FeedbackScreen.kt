package com.aslibill.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.foundation.layout.height
import androidx.compose.ui.unit.dp

import com.aslibill.ui.components.OrangeButton
import com.aslibill.ui.components.ScreenSurface
import com.aslibill.ui.components.SectionHeader
import com.aslibill.ui.theme.AsliColors
import com.aslibill.ui.theme.AppSpacing

@Composable
fun FeedbackScreen(
  contentPadding: PaddingValues,
  vm: FeedbackViewModel
) {
  val message by vm.message.collectAsState()
  val contactInfo by vm.contactInfo.collectAsState()
  val isSubmitting by vm.isSubmitting.collectAsState()
  val submissionSuccess by vm.submissionSuccess.collectAsState()



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
        SectionHeader("Give Feedback")

        if (submissionSuccess) {
          Text(
            "Thank you for your feedback!",
            color = AsliColors.SuccessGreen,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
          )

          OrangeButton("NEW FEEDBACK", onClick = { vm.resetSuccess() })
        } else {
          Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.md)) {
            com.aslibill.ui.components.AsliTextField(
              value = message,
              onValueChange = { vm.onMessageChange(it) },
              label = "Your Message",
              modifier = Modifier.fillMaxWidth().height(160.dp)
            )

            com.aslibill.ui.components.AsliTextField(
              value = contactInfo,
              onValueChange = { vm.onContactInfoChange(it) },
              label = "Email or Mobile (Optional)",
              modifier = Modifier.fillMaxWidth()
            )

            OrangeButton(
              "SUBMIT FEEDBACK",
              onClick = { vm.submitFeedback() },
              modifier = Modifier.fillMaxWidth()
            )
          }
        }
      }

      if (isSubmitting) {
        com.aslibill.ui.components.AsliLoader()
      }
    }
  }
}
