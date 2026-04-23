package com.aslibill.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.aslibill.data.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class PlanType {
    FREE, PREMIUM
}

data class PlanFeature(
    val feature: String,
    val isIncluded: Boolean
)

class SubscriptionViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

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

    init {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val plan = authRepository.getSubscriptionPlan()
                _currentPlan.value = if (plan.equals("PREMIUM", ignoreCase = true)) PlanType.PREMIUM else PlanType.FREE
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun upgrade() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val ok = authRepository.upgradeSubscription()
                if (ok) _currentPlan.value = PlanType.PREMIUM
            } finally {
                _isLoading.value = false
            }
        }
    }
}

class SubscriptionViewModelFactory(
    private val authRepository: AuthRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SubscriptionViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SubscriptionViewModel(authRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
