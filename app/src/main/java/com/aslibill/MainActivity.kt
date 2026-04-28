package com.aslibill

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import com.aslibill.ui.theme.ThemeMode
import com.aslibill.ui.theme.UiPreferences
import com.aslibill.ui.BillSuperApp
import com.aslibill.ui.theme.BillSuperTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      val app = application as BillSuperApplication
      val uiPreferences = app.container.settingsRepository.uiPreferences
        .collectAsState(initial = UiPreferences()).value
      BillSuperTheme(
        mode = uiPreferences.mode
      ) {
        BillSuperApp()
      }
    }
  }
}
