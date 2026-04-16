package com.aslibill.ui.screens

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class PremiumFeature(
    val title: String,
    val description: String
)

class UpgradePremiumViewModel : ViewModel() {
    private val _features = MutableStateFlow(
        listOf(
            PremiumFeature("Unlimited Bills", "Generate as many bills as you need without any daily or monthly limits."),
            PremiumFeature("GST/VAT Support", "Add GST numbers and tax breakdowns to your professional receipts."),
            PremiumFeature("Cloud Backup", "Your data is safe in the cloud. Access it even if you lose your device."),
            PremiumFeature("Advanced Analytics", "Deep dive into your sales patterns with interactive charts and reports."),
            PremiumFeature("Multi-device Sync", "Collaborate with your team. Use NovaBill on multiple phones simultaneously."),
            PremiumFeature("Custom Branding", "Remove NovaBill branding and add your own logo to your receipts.")
        )
    )
    val features: StateFlow<List<PremiumFeature>> = _features.asStateFlow()

    fun performUpgrade(onSuccess: () -> Unit) {
        // Simulated upgrade logic
        onSuccess()
    }
}
