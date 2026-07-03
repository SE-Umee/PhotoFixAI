package com.umeetech.photofixai.presentation.screens.backgroundremover

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.umeetech.photofixai.core.export.ExportFormat
import com.umeetech.photofixai.core.export.ExportResult
import com.umeetech.photofixai.core.image.BackgroundComposer
import com.umeetech.photofixai.core.image.BitmapUtils
import com.umeetech.photofixai.core.result.AppError
import com.umeetech.photofixai.core.result.Resource
import com.umeetech.photofixai.di.AppContainer
import com.umeetech.photofixai.domain.model.BackgroundOption
import com.umeetech.photofixai.domain.model.ToolType
import com.umeetech.photofixai.presentation.common.ExportSupport
import com.umeetech.photofixai.presentation.common.ToolStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class BgRemoverUiState(
    val status: ToolStatus = ToolStatus.Idle,
    val originalBitmap: Bitmap? = null,
    val cutoutBitmap: Bitmap? = null,
    val previewBitmap: Bitmap? = null,
    val selectedBackgroundId: String = BackgroundOption.Transparent.id,
    val customColor: Long = 0xFFFFFFFF,
    val showBefore: Boolean = false,
    val meta: BitmapUtils.ImageMeta? = null,
    val exportFormat: ExportFormat = ExportFormat.PNG,
    val lastExport: ExportResult? = null,
    val isPremium: Boolean = false
) {
    val hasImage: Boolean get() = originalBitmap != null
    val hasCutout: Boolean get() = cutoutBitmap != null
}

class BackgroundRemoverViewModel(private val container: AppContainer) : ViewModel() {

    private val _state = MutableStateFlow(BgRemoverUiState())
    val state: StateFlow<BgRemoverUiState> = _state

    init {
        container.settingsRepository.settings
            .onEach { s -> _state.value = _state.value.copy(isPremium = s.isPremium) }
            .launchIn(viewModelScope)
    }

    fun onImagePicked(context: Context, uri: Uri?) {
        if (uri == null) return
        viewModelScope.launch {
            _state.value = _state.value.copy(status = ToolStatus.Processing)
            val bitmap = withContext(Dispatchers.IO) { BitmapUtils.decodeSampledBitmap(context, uri) }
            val meta = withContext(Dispatchers.IO) { BitmapUtils.readImageMeta(context, uri) }
            if (bitmap == null) {
                _state.value = _state.value.copy(status = ToolStatus.Error(AppError.UnsupportedFormat))
                return@launch
            }
            _state.value = BgRemoverUiState(
                status = ToolStatus.ImageSelected,
                originalBitmap = bitmap,
                previewBitmap = bitmap,
                meta = meta,
                isPremium = _state.value.isPremium
            )
        }
    }

    fun removeBackground() {
        val original = _state.value.originalBitmap ?: return
        viewModelScope.launch {
            _state.value = _state.value.copy(status = ToolStatus.Processing)
            when (val result = container.removeBackgroundUseCase(original)) {
                is Resource.Success -> {
                    _state.value = _state.value.copy(
                        status = ToolStatus.Success,
                        cutoutBitmap = result.data
                    )
                    recomposePreview()
                }
                is Resource.Error -> _state.value = _state.value.copy(status = ToolStatus.Error(result.error))
                Resource.Loading -> Unit
            }
        }
    }

    fun selectBackground(option: BackgroundOption, customColor: Long? = null) {
        _state.value = _state.value.copy(
            selectedBackgroundId = option.id,
            customColor = customColor ?: _state.value.customColor
        )
        recomposePreview()
    }

    fun toggleBeforeAfter(show: Boolean) {
        _state.value = _state.value.copy(showBefore = show)
    }

    fun rotate() {
        val current = _state.value
        val rotatedOriginal = current.originalBitmap?.let { BitmapUtils.rotate(it, 90f) }
        val rotatedCutout = current.cutoutBitmap?.let { BitmapUtils.rotate(it, 90f) }
        _state.value = current.copy(originalBitmap = rotatedOriginal, cutoutBitmap = rotatedCutout)
        recomposePreview()
    }

    fun reset() {
        val original = _state.value.originalBitmap
        _state.value = _state.value.copy(
            status = if (original != null) ToolStatus.ImageSelected else ToolStatus.Idle,
            cutoutBitmap = null,
            previewBitmap = original,
            selectedBackgroundId = BackgroundOption.Transparent.id,
            showBefore = false
        )
    }

    fun setFormat(format: ExportFormat) {
        _state.value = _state.value.copy(exportFormat = format)
    }

    private fun recomposePreview() {
        val s = _state.value
        val base = s.cutoutBitmap ?: s.originalBitmap ?: return
        val preview = when {
            s.cutoutBitmap == null -> base
            s.selectedBackgroundId == BackgroundOption.Transparent.id -> base
            else -> {
                val color = resolveColor(s.selectedBackgroundId, s.customColor)
                BackgroundComposer.composeOnColor(base, color.toInt())
            }
        }
        _state.value = _state.value.copy(previewBitmap = preview)
    }

    private fun resolveColor(id: String, custom: Long): Long = when (id) {
        BackgroundOption.White.id -> 0xFFFFFFFF
        BackgroundOption.Blue.id -> 0xFF2563EB
        BackgroundOption.Red.id -> 0xFFEF4444
        BackgroundOption.Custom.id -> custom
        else -> 0xFFFFFFFF
    }

    /** The bitmap that should be exported (respects transparency vs colored bg). */
    private fun exportBitmap(): Bitmap? = _state.value.previewBitmap ?: _state.value.cutoutBitmap

    fun export(context: Context, onDone: (ExportResult) -> Unit) {
        val bitmap = exportBitmap() ?: return
        viewModelScope.launch {
            _state.value = _state.value.copy(status = ToolStatus.Exporting)
            val quality = ExportSupport.currentQuality(container)
            when (val res = ExportSupport.exportAndRecord(
                context, container, bitmap, _state.value.exportFormat, quality, ToolType.BACKGROUND_REMOVER
            )) {
                is Resource.Success -> {
                    _state.value = _state.value.copy(status = ToolStatus.ExportSuccess, lastExport = res.data)
                    onDone(res.data)
                }
                is Resource.Error -> _state.value = _state.value.copy(status = ToolStatus.Error(res.error))
                Resource.Loading -> Unit
            }
        }
    }

    fun buildShareIntent(context: Context): Intent? {
        val bitmap = exportBitmap() ?: return null
        return ExportSupport.buildShareIntent(
            context, bitmap, _state.value.exportFormat,
            com.umeetech.photofixai.core.constants.Constants.DEFAULT_EXPORT_QUALITY
        )
    }

    fun dismissError() {
        _state.value = _state.value.copy(
            status = if (_state.value.hasImage) ToolStatus.ImageSelected else ToolStatus.Idle
        )
    }
}
