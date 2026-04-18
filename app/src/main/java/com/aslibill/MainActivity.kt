package com.aslibill

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import com.aslibill.ui.theme.ThemeMode
import com.aslibill.ui.theme.UiPreferences
import com.aslibill.ui.NovaBillApp
import com.aslibill.ui.theme.NovaBillTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      val app = application as NovaBillApplication
      val uiPreferences = app.container.settingsRepository.uiPreferences
        .collectAsState(initial = UiPreferences()).value
      NovaBillTheme(
        mode = uiPreferences.mode
      ) {
        NovaBillApp()
      }
    }
  }
}
