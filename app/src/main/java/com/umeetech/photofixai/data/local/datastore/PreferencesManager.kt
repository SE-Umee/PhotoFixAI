package com.umeetech.photofixai.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.umeetech.photofixai.core.constants.Constants
import com.umeetech.photofixai.core.export.ExportFormat
import com.umeetech.photofixai.domain.model.AppSettings
import com.umeetech.photofixai.domain.model.ExportQuality
import com.umeetech.photofixai.domain.model.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = Constants.PREFERENCES_NAME)

/** Single source of truth for persisted preferences (onboarding + settings). */
class PreferencesManager(private val context: Context) {

    private object Keys {
        val ONBOARDING_DONE = booleanPreferencesKey("onboarding_done")
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val EXPORT_QUALITY = stringPreferencesKey("export_quality")
        val DEFAULT_FORMAT = stringPreferencesKey("default_format")
        val IS_PREMIUM = booleanPreferencesKey("is_premium")
    }

    val onboardingCompleted: Flow<Boolean> =
        context.dataStore.data.map { it[Keys.ONBOARDING_DONE] ?: false }

    val settings: Flow<AppSettings> = context.dataStore.data.map { prefs ->
        AppSettings(
            themeMode = prefs[Keys.THEME_MODE]?.let { runCatching { ThemeMode.valueOf(it) }.getOrNull() }
                ?: ThemeMode.SYSTEM,
            exportQuality = prefs[Keys.EXPORT_QUALITY]?.let { runCatching { ExportQuality.valueOf(it) }.getOrNull() }
                ?: ExportQuality.HIGH,
            defaultFormat = prefs[Keys.DEFAULT_FORMAT]?.let { runCatching { ExportFormat.valueOf(it) }.getOrNull() }
                ?: ExportFormat.PNG,
            isPremium = prefs[Keys.IS_PREMIUM] ?: false
        )
    }

    suspend fun setOnboardingCompleted(completed: Boolean) {
        context.dataStore.edit { it[Keys.ONBOARDING_DONE] = completed }
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        context.dataStore.edit { it[Keys.THEME_MODE] = mode.name }
    }

    suspend fun setExportQuality(quality: ExportQuality) {
        context.dataStore.edit { it[Keys.EXPORT_QUALITY] = quality.name }
    }

    suspend fun setDefaultFormat(format: ExportFormat) {
        context.dataStore.edit { it[Keys.DEFAULT_FORMAT] = format.name }
    }

    suspend fun setPremium(isPremium: Boolean) {
        context.dataStore.edit { it[Keys.IS_PREMIUM] = isPremium }
    }
}
