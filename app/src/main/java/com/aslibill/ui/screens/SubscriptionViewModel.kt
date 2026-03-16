package com.aslibill.ui.screens

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class PlanType {
    FREE, PREMIUM
}

data class PlanFeature(
    val feature: String,
    val isIncluded: Boolean
)

class SubscriptionViewModel : ViewModel() {
    private val _currentPlan = MutableStateFlow(PlanType.FREE)
    val currentPlan: StateFlow<PlanType> = _currentPlan.asStateFlow()

    private val _premiumFeatures = MutableStateFlow(
        listOf(
            PlanFeature("Unlimited Bills", true),
            PlanFeature("Cloud Data Backup", true),
            PlanFeature("Multi-device Sync", true),
            PlanFeature("Export to Excel/PDF", true),
            PlanFeature("24/7 Priority Support", true),
            PlanFeature("Custom Business Branding", true)
        )
    )
    val premiumFeatures: StateFlow<List<PlanFeature>> = _premiumFeatures.asStateFlow()

    fun upgrade() {
        // In a real app, this would trigger a payment gateway or play store billing
        println("User requested Upgrade to Premium")
    }
}
