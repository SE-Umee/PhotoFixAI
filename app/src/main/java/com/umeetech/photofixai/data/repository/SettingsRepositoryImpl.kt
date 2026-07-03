package com.umeetech.photofixai.data.repository

import com.umeetech.photofixai.core.export.ExportFormat
import com.umeetech.photofixai.data.local.datastore.PreferencesManager
import com.umeetech.photofixai.domain.model.AppSettings
import com.umeetech.photofixai.domain.model.ExportQuality
import com.umeetech.photofixai.domain.model.ThemeMode
import com.umeetech.photofixai.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow

class SettingsRepositoryImpl(
    private val preferences: PreferencesManager
) : SettingsRepository {

    override val settings: Flow<AppSettings> = preferences.settings
    override val onboardingCompleted: Flow<Boolean> = preferences.onboardingCompleted

    override suspend fun setOnboardingCompleted(completed: Boolean) =
        preferences.setOnboardingCompleted(completed)

    override suspend fun setThemeMode(mode: ThemeMode) = preferences.setThemeMode(mode)

    override suspend fun setExportQuality(quality: ExportQuality) =
        preferences.setExportQuality(quality)

    override suspend fun setDefaultFormat(format: ExportFormat) =
        preferences.setDefaultFormat(format)

    override suspend fun setPremium(isPremium: Boolean) = preferences.setPremium(isPremium)
}
