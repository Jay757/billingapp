package com.aslibill.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aslibill.ui.components.OrangeButton
import com.aslibill.ui.components.ScreenSurface
import com.aslibill.ui.components.Chip
import com.aslibill.ui.theme.AsliColors
import com.aslibill.ui.theme.ThemeMode
import com.aslibill.ui.theme.ThemePalette

@Composable
fun PrintSettingsScreen(
    contentPadding: PaddingValues,
    vm: PrintSettingsViewModel
) {
    val settings by vm.settings.collectAsState()
    val uiPreferences by vm.uiPreferences.collectAsState()

    var storeName by remember(settings) { mutableStateOf(settings.storeName) }
    var address1 by remember(settings) { mutableStateOf(settings.addressLines.getOrNull(0) ?: "") }
    var address2 by remember(settings) { mutableStateOf(settings.addressLines.getOrNull(1) ?: "") }
    var phone by remember(settings) { mutableStateOf(settings.phone ?: "") }
    var gst by remember(settings) { mutableStateOf(settings.gstNumber ?: "") }
    var thankYou by remember(settings) { mutableStateOf(settings.thankYouMessage ?: "") }
    var paperWidth by remember(settings) { mutableStateOf(settings.paperWidthChars) }
    var themeMode by remember(uiPreferences) { mutableStateOf(uiPreferences.mode) }
    var palette by remember(uiPreferences) { mutableStateOf(uiPreferences.palette) }

    ScreenSurface {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "App Settings",
                color = AsliColors.TextPrimary,
                style = MaterialTheme.typography.headlineMedium
            )

            OutlinedTextField(
                value = storeName,
                onValueChange = { storeName = it },
                label = { Text("Store Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = address1,
                onValueChange = { address1 = it },
                label = { Text("Address Line 1") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = address2,
                onValueChange = { address2 = it },
                label = { Text("Address Line 2") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Phone") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = gst,
                onValueChange = { gst = it },
                label = { Text("GST Number") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = thankYou,
                onValueChange = { thankYou = it },
                label = { Text("Thank You Message") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Text("Paper Width", color = AsliColors.TextSecondary)
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Chip(text = "58mm (32 chars)", selected = paperWidth == 32, onClick = { paperWidth = 32 })
                Chip(text = "80mm (42 chars)", selected = paperWidth == 42, onClick = { paperWidth = 42 })
            }

            Text("Theme Mode", color = AsliColors.TextSecondary)
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Chip(text = "Light", selected = themeMode == ThemeMode.LIGHT, onClick = { themeMode = ThemeMode.LIGHT })
                Chip(text = "Dark", selected = themeMode == ThemeMode.DARK, onClick = { themeMode = ThemeMode.DARK })
                Chip(text = "System", selected = themeMode == ThemeMode.SYSTEM, onClick = { themeMode = ThemeMode.SYSTEM })
            }

            Text("Color Palette", color = AsliColors.TextSecondary)
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Chip(text = "Blue", selected = palette == ThemePalette.BLUE, onClick = { palette = ThemePalette.BLUE })
                Chip(text = "Orange", selected = palette == ThemePalette.ORANGE, onClick = { palette = ThemePalette.ORANGE })
                Chip(text = "Green", selected = palette == ThemePalette.GREEN, onClick = { palette = ThemePalette.GREEN })
            }

            OrangeButton(
                text = "Save Settings",
                onClick = {
                    vm.saveSettings(
                        storeName = storeName,
                        address1 = address1,
                        address2 = address2,
                        phone = phone,
                        gst = gst,
                        thankYou = thankYou,
                        paperWidth = paperWidth
                    )
                    vm.saveAppearance(themeMode, palette)
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

