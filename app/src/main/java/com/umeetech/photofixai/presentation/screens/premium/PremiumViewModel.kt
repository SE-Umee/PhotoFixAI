package com.umeetech.photofixai.presentation.screens.premium

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.umeetech.photofixai.core.billing.BillingManager
import com.umeetech.photofixai.di.AppContainer
import com.umeetech.photofixai.domain.model.PremiumPlan
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

data class PremiumUiState(
    val plans: List<PremiumPlan> = PremiumPlan.plans,
    val benefits: List<String> = PremiumPlan.benefits,
    val selectedPlanId: String = PremiumPlan.plans.first { it.highlighted }.id,
    val isPremium: Boolean = false,
    val showComingSoon: Boolean = false
)

class PremiumViewModel(private val container: AppContainer) : ViewModel() {

    private val _state = MutableStateFlow(PremiumUiState())
    val state: StateFlow<PremiumUiState> = _state

    init {
        container.settingsRepository.settings
            .onEach { _state.value = _state.value.copy(isPremium = it.isPremium) }
            .launchIn(viewModelScope)
    }

    fun selectPlan(id: String) { _state.value = _state.value.copy(selectedPlanId = id) }

    /**
     * Placeholder purchase flow. Google Play Billing is NOT wired yet — see
     * [BillingManager]. This surfaces a "coming soon" dialog instead of charging.
     */
    fun startPurchase() {
        val plan = _state.value.selectedPlanId
        val productId = when (plan) {
            "monthly" -> BillingManager.Products.MONTHLY
            "yearly" -> BillingManager.Products.YEARLY
            else -> BillingManager.Products.LIFETIME
        }
        container.billingManager.launchPurchaseFlow(productId)
        _state.value = _state.value.copy(showComingSoon = true)
    }

    fun dismissComingSoon() { _state.value = _state.value.copy(showComingSoon = false) }
}
