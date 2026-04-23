package com.aslibill.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aslibill.ui.components.AsliTextField
import com.aslibill.ui.components.Chip
import com.aslibill.ui.components.OrangeButton
import com.aslibill.ui.components.ScreenSurface
import com.aslibill.ui.components.SectionHeader
import com.aslibill.ui.theme.AppSpacing
import com.aslibill.ui.theme.AppTypography
import com.aslibill.ui.theme.AsliColors
import com.aslibill.ui.theme.ThemeMode

@Composable
fun PrintSettingsScreen(
    contentPadding: PaddingValues,
    vm: PrintSettingsViewModel
) {
    val settings by vm.settings.collectAsState()
    val uiPreferences by vm.uiPreferences.collectAsState()
    val userSession by vm.userSession.collectAsState()

    var storeName by remember(settings) { mutableStateOf(settings.storeName) }
    var address by remember(settings) { mutableStateOf(settings.address.takeIf { it != "null" } ?: "") }
    var phone by remember(settings, userSession) { 
        val p = settings.phone?.takeIf { it != "null" && it.isNotBlank() }
        mutableStateOf(p ?: userSession?.phone ?: "") 
    }
    var gst by remember(settings) { 
        val g = settings.gstNumber?.takeIf { it != "null" && it.isNotBlank() }
        mutableStateOf(g ?: "0") 
    }
    var thankYou by remember(settings) { mutableStateOf(settings.thankYouMessage?.takeIf { it != "null" } ?: "") }
    var paperWidth by remember(settings) { mutableStateOf(settings.paperWidthChars) }
    var themeMode by remember(uiPreferences) { mutableStateOf(uiPreferences.mode) }

    val isLoading by vm.isLoading.collectAsState()
    
    ScreenSurface {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding)
                    .padding(horizontal = AppSpacing.md)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(AppSpacing.md)
            ) {
                Text(
                    "App Settings",
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.Black),
                    modifier = Modifier.padding(top = AppSpacing.md)
                )


                SectionHeader("STORE INFORMATION")

                AsliTextField(
                    value = storeName,
                    onValueChange = { storeName = it },
                    label = "Store Name"
                )

                AsliTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = "Address"
                )

                AsliTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = "Phone"
                )

                AsliTextField(
                    value = gst,
                    onValueChange = { gst = it },
                    label = "GST Number"
                )

                AsliTextField(
                    value = thankYou,
                    onValueChange = { thankYou = it },
                    label = "Thank You Message"
                )

                SectionHeader("PRINTER PREFERENCES")
                Text("PAPER WIDTH", style = MaterialTheme.typography.labelMedium.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.Bold), color = MaterialTheme.colorScheme.primary)

                Row(horizontalArrangement = Arrangement.spacedBy(AppSpacing.md)) {
                    Chip(text = "58mm", selected = paperWidth == 32, onSelected = { paperWidth = 32 })
                    Chip(text = "80mm", selected = paperWidth == 42, onSelected = { paperWidth = 42 })
                }

                SectionHeader("APPEARANCE")
                Text("THEME MODE", style = MaterialTheme.typography.labelMedium.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.Bold), color = MaterialTheme.colorScheme.primary)

                Row(horizontalArrangement = Arrangement.spacedBy(AppSpacing.md)) {
                    Chip(text = "Light", selected = themeMode == ThemeMode.LIGHT, onSelected = { themeMode = ThemeMode.LIGHT })
                    Chip(text = "Dark", selected = themeMode == ThemeMode.DARK, onSelected = { themeMode = ThemeMode.DARK })
                    Chip(text = "System", selected = themeMode == ThemeMode.SYSTEM, onSelected = { themeMode = ThemeMode.SYSTEM })
                }

                Spacer(Modifier.height(AppSpacing.md))
                OrangeButton(
                    text = "SAVE SETTINGS",
                    onClick = {
                        vm.saveSettings(
                            storeName = storeName,
                            address = address,
                            phone = phone,
                            gst = gst,
                            thankYou = thankYou,
                            paperWidth = paperWidth
                        )
                        vm.saveAppearance(themeMode)
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(AppSpacing.xl))
            }

            if (isLoading) {
                com.aslibill.ui.components.AsliLoader()
            }
        }
    }
}
