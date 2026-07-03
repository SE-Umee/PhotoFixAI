package com.umeetech.photofixai.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.umeetech.photofixai.di.AppContainer
import com.umeetech.photofixai.domain.model.HistoryItem
import com.umeetech.photofixai.domain.model.ToolType
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class HomeUiState(
    val recentProjects: List<HistoryItem> = emptyList(),
    val isPremium: Boolean = false
)

class HomeViewModel(container: AppContainer) : ViewModel() {

    /** The 6 primary tools shown in the Home grid. */
    val featuredTools = listOf(
        ToolType.BACKGROUND_REMOVER,
        ToolType.PASSPORT_PHOTO,
        ToolType.RESIZE,
        ToolType.COMPRESS,
        ToolType.PRODUCT_PHOTO,
        ToolType.SIGNATURE
    )

    val uiState: StateFlow<HomeUiState> =
        combineHomeState(container).stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            HomeUiState()
        )

    private fun combineHomeState(container: AppContainer) =
        kotlinx.coroutines.flow.combine(
            container.observeHistoryUseCase(),
            container.settingsRepository.settings
        ) { history, settings ->
            HomeUiState(
                recentProjects = history.take(10),
                isPremium = settings.isPremium
            )
        }
}
