package com.umeetech.photofixai.presentation.screens.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.umeetech.photofixai.core.export.ExportFormat
import com.umeetech.photofixai.di.AppContainer
import com.umeetech.photofixai.domain.model.AppSettings
import com.umeetech.photofixai.domain.model.ExportQuality
import com.umeetech.photofixai.domain.model.ThemeMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SettingsViewModel(private val container: AppContainer) : ViewModel() {

    val settings: StateFlow<AppSettings> = container.settingsRepository.settings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), AppSettings())

    fun setTheme(mode: ThemeMode) = viewModelScope.launch { container.settingsRepository.setThemeMode(mode) }
    fun setExportQuality(q: ExportQuality) = viewModelScope.launch { container.settingsRepository.setExportQuality(q) }
    fun setDefaultFormat(f: ExportFormat) = viewModelScope.launch { container.settingsRepository.setDefaultFormat(f) }

    fun clearHistory() = viewModelScope.launch { container.clearHistoryUseCase() }

    fun clearCache(context: Context, onDone: () -> Unit) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                runCatching { context.cacheDir.deleteRecursively() }
            }
            onDone()
        }
    }
}
