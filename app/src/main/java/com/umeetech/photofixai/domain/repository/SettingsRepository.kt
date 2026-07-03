package com.umeetech.photofixai.domain.repository

import com.umeetech.photofixai.domain.model.AppSettings
import com.umeetech.photofixai.domain.model.ExportQuality
import com.umeetech.photofixai.domain.model.ThemeMode
import com.umeetech.photofixai.core.export.ExportFormat
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val settings: Flow<AppSettings>
    val onboardingCompleted: Flow<Boolean>

    suspend fun setOnboardingCompleted(completed: Boolean)
    suspend fun setThemeMode(mode: ThemeMode)
    suspend fun setExportQuality(quality: ExportQuality)
    suspend fun setDefaultFormat(format: ExportFormat)
    suspend fun setPremium(isPremium: Boolean)
}
