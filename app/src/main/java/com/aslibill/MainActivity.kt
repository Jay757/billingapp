package com.aslibill

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.aslibill.ui.AsliBillApp
import com.aslibill.ui.theme.AsliBillTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      AsliBillTheme {
        AsliBillApp()
      }
    }
  }
}
