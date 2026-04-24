package com.aslibill.ui.screens

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

data class PremiumFeature(
    val title: String,
    val description: String
)

data class SubscriptionPlan(
    val id: String,
    val name: String,
    val duration: String,
    val price: Int,
    val priceLabel: String,
    val tag: String? = null
)

class UpgradePremiumViewModel : ViewModel() {
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _plans = MutableStateFlow(
        listOf(
            SubscriptionPlan("1_month", "1 Month", "Standard", 149, "₹149/mo"),
            SubscriptionPlan("6_month", "6 Months", "Popular", 699, "₹699/6mo", "SAVE 20%"),
            SubscriptionPlan("1_year", "1 Year", "Best Value", 999, "₹999/yr", "SAVE 45%")
        )
    )
    val plans = _plans.asStateFlow()

    private val _selectedPlan = MutableStateFlow(_plans.value.last()) // Default to 1 Year
    val selectedPlan = _selectedPlan.asStateFlow()

    fun selectPlan(plan: SubscriptionPlan) {
        _selectedPlan.value = plan
    }

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
        viewModelScope.launch {
            _isLoading.value = true
            delay(1500) // Simulate network delay
            _isLoading.value = false
            onSuccess()
        }
    }
}
