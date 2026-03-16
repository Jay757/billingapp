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
import androidx.compose.ui.unit.dp
import com.aslibill.ui.components.OrangeButton
import com.aslibill.ui.components.ScreenSurface
import com.aslibill.ui.components.SectionHeader
import com.aslibill.ui.theme.AsliColors

@Composable
fun FeedbackScreen(
  contentPadding: PaddingValues,
  vm: FeedbackViewModel
) {
  val message by vm.message.collectAsState()
  val contactInfo by vm.contactInfo.collectAsState()
  val isSubmitting by vm.isSubmitting.collectAsState()
  val submissionSuccess by vm.submissionSuccess.collectAsState()

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
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(contentPadding)
        .padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(20.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      SectionHeader("Give Feedback")

      if (submissionSuccess) {
        Text(
          "Thank you for your feedback!",
          color = AsliColors.Green,
          style = MaterialTheme.typography.titleMedium
        )
        OrangeButton("NEW FEEDBACK", onClick = { vm.resetSuccess() })
      } else {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
          OutlinedTextField(
            value = message,
            onValueChange = { vm.onMessageChange(it) },
            label = { Text("Your Message") },
            colors = fieldColors,
            modifier = Modifier.fillMaxWidth().weight(1f, fill = false),
            minLines = 5
          )

          OutlinedTextField(
            value = contactInfo,
            onValueChange = { vm.onContactInfoChange(it) },
            label = { Text("Email or Mobile (Optional)") },
            colors = fieldColors,
            modifier = Modifier.fillMaxWidth()
          )

          if (isSubmitting) {
            CircularProgressIndicator(color = AsliColors.Orange)
          } else {
            OrangeButton(
              "SUBMIT FEEDBACK",
              onClick = { vm.submitFeedback() },
              modifier = Modifier.fillMaxWidth()
            )
          }
        }
      }
    }
  }
}
