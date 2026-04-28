package com.billsuper.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.Icon

import com.billsuper.ui.components.BillSuperTextField
import com.billsuper.ui.components.OrangeButton
import com.billsuper.ui.components.ScreenSurface
import com.billsuper.ui.components.SectionHeader
import com.billsuper.ui.theme.BillSuperColors
import com.billsuper.ui.theme.AppSpacing

@Composable
fun FeedbackScreen(
  contentPadding: PaddingValues,
  vm: FeedbackViewModel
) {
  val message by vm.message.collectAsState()
  val contactInfo by vm.contactInfo.collectAsState()
  val isSubmitting by vm.isSubmitting.collectAsState()
  val submissionSuccess by vm.submissionSuccess.collectAsState()

  val scrollState = rememberScrollState()

  ScreenSurface {
    Box(modifier = Modifier.fillMaxSize()) {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(contentPadding)
          .padding(horizontal = AppSpacing.lg)
          .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        SectionHeader("Give Feedback")

        Spacer(modifier = Modifier.height(AppSpacing.lg))

        if (submissionSuccess) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 60.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(AppSpacing.lg)
          ) {
            Icon(
              imageVector = Icons.Outlined.CheckCircle,
              contentDescription = null,
              tint = BillSuperColors.SuccessGreen,
              modifier = Modifier.size(80.dp)
            )
            
            Text(
              "Thank you for your feedback!",
              color = BillSuperColors.SuccessGreen,
              style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Black,
                fontSize = 24.sp
              ),
              textAlign = TextAlign.Center
            )

            Text(
              "We appreciate your time and effort in helping us improve billsuper. Your feedback helps us build a better experience for everyone.",
              textAlign = TextAlign.Center,
              color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
              style = MaterialTheme.typography.bodyLarge,
              modifier = Modifier.padding(horizontal = AppSpacing.md)
            )

            Spacer(modifier = Modifier.height(AppSpacing.lg))

            OrangeButton(
              "SUBMIT ANOTHER",
              onClick = { vm.resetSuccess() },
              modifier = Modifier.fillMaxWidth(0.8f)
            )
          }
        } else {
          Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.xl)
          ) {
            Text(
              "Help us improve! Share your thoughts, suggestions, or report issues below.",
              color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
              style = MaterialTheme.typography.bodyLarge,
              lineHeight = 22.sp
            )

            BillSuperTextField(
              value = message,
              onValueChange = { vm.onMessageChange(it) },
              label = "Your Message",
              modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 180.dp),
              singleLine = false,
              minLines = 6
            )

            BillSuperTextField(
              value = contactInfo,
              onValueChange = { vm.onContactInfoChange(it) },
              label = "Email or Mobile (Optional)",
              modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(AppSpacing.md))

            OrangeButton(
              "SUBMIT FEEDBACK",
              onClick = { vm.submitFeedback() },
              modifier = Modifier.fillMaxWidth(),
              enabled = message.isNotBlank() && !isSubmitting
            )
          }
        }
        
        Spacer(modifier = Modifier.height(AppSpacing.xl))
      }

      if (isSubmitting) {
        com.billsuper.ui.components.BillSuperLoader()
      }
    }
  }
}


