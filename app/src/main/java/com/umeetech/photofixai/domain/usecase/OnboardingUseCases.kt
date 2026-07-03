package com.umeetech.photofixai.domain.usecase

import com.umeetech.photofixai.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow

class CompleteOnboardingUseCase(private val repository: SettingsRepository) {
    suspend operator fun invoke() = repository.setOnboardingCompleted(true)
}

class IsOnboardingCompletedUseCase(private val repository: SettingsRepository) {
    operator fun invoke(): Flow<Boolean> = repository.onboardingCompleted
}
