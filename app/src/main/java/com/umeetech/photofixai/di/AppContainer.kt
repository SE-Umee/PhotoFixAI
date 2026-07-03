package com.umeetech.photofixai.di

import android.content.Context
import com.umeetech.photofixai.core.ads.AdsManager
import com.umeetech.photofixai.core.billing.BillingManager
import com.umeetech.photofixai.data.local.database.AppDatabase
import com.umeetech.photofixai.data.local.datastore.PreferencesManager
import com.umeetech.photofixai.data.remote.api.ApiClient
import com.umeetech.photofixai.data.repository.BackgroundRemovalRepositoryImpl
import com.umeetech.photofixai.data.repository.HistoryRepositoryImpl
import com.umeetech.photofixai.data.repository.SettingsRepositoryImpl
import com.umeetech.photofixai.data.service.backgroundremoval.ApiBackgroundRemovalService
import com.umeetech.photofixai.data.service.backgroundremoval.BackgroundRemovalService
import com.umeetech.photofixai.data.service.backgroundremoval.LocalSegmentationBackgroundRemovalService
import com.umeetech.photofixai.data.service.backgroundremoval.MockBackgroundRemovalService
import com.umeetech.photofixai.domain.repository.BackgroundRemovalRepository
import com.umeetech.photofixai.domain.repository.HistoryRepository
import com.umeetech.photofixai.domain.repository.SettingsRepository
import com.umeetech.photofixai.domain.usecase.ClearHistoryUseCase
import com.umeetech.photofixai.domain.usecase.CompleteOnboardingUseCase
import com.umeetech.photofixai.domain.usecase.DeleteHistoryItemUseCase
import com.umeetech.photofixai.domain.usecase.IsOnboardingCompletedUseCase
import com.umeetech.photofixai.domain.usecase.ObserveHistoryUseCase
import com.umeetech.photofixai.domain.usecase.RemoveBackgroundUseCase
import com.umeetech.photofixai.domain.usecase.SaveToHistoryUseCase

/**
 * Lightweight manual dependency-injection container.
 *
 * Kept intentionally framework-free (no Hilt/Dagger) so the project compiles with
 * minimal setup. Everything is lazily constructed and lives for the app's lifetime.
 * To migrate to Hilt later, replace these `lazy` providers with @Module/@Provides.
 */
class AppContainer(private val appContext: Context) {

    // --- Strategy selection ---------------------------------------------------
    /**
     * Which background-removal strategy to use.
     *
     *  MVP / testing              -> [BackgroundRemovalStrategy.MOCK]   (default)
     *  Free, on-device (people)   -> [BackgroundRemovalStrategy.LOCAL]
     *  Production (via backend)   -> [BackgroundRemovalStrategy.API]
     *
     * Flip this single value to change the behavior app-wide.
     */
    enum class BackgroundRemovalStrategy { MOCK, LOCAL, API }

    var backgroundRemovalStrategy: BackgroundRemovalStrategy = BackgroundRemovalStrategy.MOCK

    // --- Infrastructure -------------------------------------------------------
    private val database: AppDatabase by lazy { AppDatabase.getInstance(appContext) }
    private val preferencesManager: PreferencesManager by lazy { PreferencesManager(appContext) }

    val adsManager: AdsManager by lazy { AdsManager(appContext) }
    val billingManager: BillingManager by lazy { BillingManager(appContext) }

    // --- Services -------------------------------------------------------------
    private val mockService: BackgroundRemovalService by lazy { MockBackgroundRemovalService() }
    private val localService: BackgroundRemovalService by lazy { LocalSegmentationBackgroundRemovalService() }
    private val apiService: BackgroundRemovalService by lazy {
        ApiBackgroundRemovalService(
            api = ApiClient.backgroundRemovalApi,
            // PRODUCTION: supply a short-lived auth token from your backend here.
            authTokenProvider = { null }
        )
    }

    private fun selectedService(): BackgroundRemovalService = when (backgroundRemovalStrategy) {
        BackgroundRemovalStrategy.MOCK -> mockService
        BackgroundRemovalStrategy.LOCAL -> localService
        BackgroundRemovalStrategy.API -> apiService
    }

    // --- Repositories ---------------------------------------------------------
    val historyRepository: HistoryRepository by lazy { HistoryRepositoryImpl(database.historyDao()) }
    val settingsRepository: SettingsRepository by lazy { SettingsRepositoryImpl(preferencesManager) }
    val backgroundRemovalRepository: BackgroundRemovalRepository by lazy {
        BackgroundRemovalRepositoryImpl(selectedService())
    }

    // --- Use cases ------------------------------------------------------------
    val removeBackgroundUseCase by lazy { RemoveBackgroundUseCase(backgroundRemovalRepository) }
    val observeHistoryUseCase by lazy { ObserveHistoryUseCase(historyRepository) }
    val saveToHistoryUseCase by lazy { SaveToHistoryUseCase(historyRepository) }
    val deleteHistoryItemUseCase by lazy { DeleteHistoryItemUseCase(historyRepository) }
    val clearHistoryUseCase by lazy { ClearHistoryUseCase(historyRepository) }
    val completeOnboardingUseCase by lazy { CompleteOnboardingUseCase(settingsRepository) }
    val isOnboardingCompletedUseCase by lazy { IsOnboardingCompletedUseCase(settingsRepository) }

    val appContextRef: Context get() = appContext
}
