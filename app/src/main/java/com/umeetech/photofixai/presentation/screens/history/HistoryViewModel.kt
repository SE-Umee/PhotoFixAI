package com.umeetech.photofixai.presentation.screens.history

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.umeetech.photofixai.di.AppContainer
import com.umeetech.photofixai.domain.model.HistoryItem
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class HistoryUiState(
    val items: List<HistoryItem> = emptyList(),
    val loading: Boolean = true
)

class HistoryViewModel(private val container: AppContainer) : ViewModel() {

    val uiState: StateFlow<HistoryUiState> = container.observeHistoryUseCase()
        .map { HistoryUiState(items = it, loading = false) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HistoryUiState())

    fun delete(item: HistoryItem) {
        viewModelScope.launch { container.deleteHistoryItemUseCase(item) }
    }

    fun clearAll() {
        viewModelScope.launch { container.clearHistoryUseCase() }
    }

    fun openIntent(item: HistoryItem): Intent =
        Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(Uri.parse(item.filePath), "image/*")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

    fun shareIntent(item: HistoryItem): Intent =
        Intent(Intent.ACTION_SEND).apply {
            type = "image/*"
            putExtra(Intent.EXTRA_STREAM, Uri.parse(item.filePath))
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
}
