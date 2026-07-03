package com.umeetech.photofixai.presentation.screens.productphoto

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.core.graphics.scale
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.umeetech.photofixai.core.constants.Constants
import com.umeetech.photofixai.core.export.ExportFormat
import com.umeetech.photofixai.core.image.BackgroundComposer
import com.umeetech.photofixai.core.image.BitmapUtils
import com.umeetech.photofixai.core.result.AppError
import com.umeetech.photofixai.core.result.Resource
import com.umeetech.photofixai.di.AppContainer
import com.umeetech.photofixai.domain.model.BackgroundOption
import com.umeetech.photofixai.domain.model.MarketplacePreset
import com.umeetech.photofixai.domain.model.ToolType
import com.umeetech.photofixai.presentation.common.ExportSupport
import com.umeetech.photofixai.presentation.common.ToolStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class ProductUiState(
    val status: ToolStatus = ToolStatus.Idle,
    val original: Bitmap? = null,
    val cutout: Bitmap? = null,
    val preview: Bitmap? = null,
    val backgroundId: String = BackgroundOption.White.id,
    val customColor: Long = 0xFFFFFFFF,
    val softShadow: Boolean = true,
    val preset: MarketplacePreset = MarketplacePreset.presets.first(),
    val exported: Boolean = false
) {
    val hasImage: Boolean get() = original != null
}

class ProductPhotoViewModel(private val container: AppContainer) : ViewModel() {

    private val _state = MutableStateFlow(ProductUiState())
    val state: StateFlow<ProductUiState> = _state

    val presets = MarketplacePreset.presets

    fun onImagePicked(context: Context, uri: Uri?) {
        if (uri == null) return
        viewModelScope.launch {
            _state.value = _state.value.copy(status = ToolStatus.Processing)
            val bmp = withContext(Dispatchers.IO) { BitmapUtils.decodeSampledBitmap(context, uri) }
            if (bmp == null) {
                _state.value = _state.value.copy(status = ToolStatus.Error(AppError.UnsupportedFormat)); return@launch
            }
            _state.value = ProductUiState(status = ToolStatus.ImageSelected, original = bmp)
            recompose()
        }
    }

    fun selectBackground(option: BackgroundOption, customColor: Long? = null) {
        _state.value = _state.value.copy(backgroundId = option.id, customColor = customColor ?: _state.value.customColor)
        recompose()
    }

    fun toggleShadow(enabled: Boolean) { _state.value = _state.value.copy(softShadow = enabled); recompose() }
    fun selectPreset(preset: MarketplacePreset) { _state.value = _state.value.copy(preset = preset); recompose() }

    fun removeBackground() {
        val original = _state.value.original ?: return
        viewModelScope.launch {
            _state.value = _state.value.copy(status = ToolStatus.Processing)
            when (val res = container.removeBackgroundUseCase(original)) {
                is Resource.Success -> { _state.value = _state.value.copy(status = ToolStatus.Success, cutout = res.data); recompose() }
                is Resource.Error -> _state.value = _state.value.copy(status = ToolStatus.Error(res.error))
                Resource.Loading -> Unit
            }
        }
    }

    private fun buildProduct(): Bitmap? {
        val s = _state.value
        val base = s.cutout ?: s.original ?: return null
        val color = resolveColor(s.backgroundId, s.customColor).toInt()
        val composed = when {
            s.cutout != null && s.softShadow -> BackgroundComposer.composeWithSoftShadow(base, color)
            s.cutout != null -> BackgroundComposer.composeOnColor(base, color)
            else -> base
        }
        val square = BitmapUtils.cropToAspectRatio(composed, 1f)
        val size = s.preset.pixelSize.coerceAtMost(Constants.MAX_PROCESSING_DIMENSION)
        return square.scale(size, size)
    }

    private fun recompose() { _state.value = _state.value.copy(preview = buildProduct()) }

    private fun resolveColor(id: String, custom: Long): Long = when (id) {
        BackgroundOption.White.id -> 0xFFFFFFFF
        BackgroundOption.Blue.id -> 0xFF2563EB
        BackgroundOption.Red.id -> 0xFFEF4444
        BackgroundOption.Custom.id -> custom
        else -> 0xFFFFFFFF
    }

    fun export(context: Context, onDone: () -> Unit) {
        viewModelScope.launch {
            _state.value = _state.value.copy(status = ToolStatus.Exporting)
            val bmp = withContext(Dispatchers.Default) { buildProduct() }
            if (bmp == null) { _state.value = _state.value.copy(status = ToolStatus.Error(AppError.ExportFailed)); return@launch }
            val quality = ExportSupport.currentQuality(container)
            when (val res = ExportSupport.exportAndRecord(
                context, container, bmp, ExportFormat.JPG, quality, ToolType.PRODUCT_PHOTO
            )) {
                is Resource.Success -> { _state.value = _state.value.copy(status = ToolStatus.ExportSuccess, exported = true); onDone() }
                is Resource.Error -> _state.value = _state.value.copy(status = ToolStatus.Error(res.error))
                Resource.Loading -> Unit
            }
        }
    }

    fun buildShareIntent(context: Context): Intent? {
        val bmp = buildProduct() ?: return null
        return ExportSupport.buildShareIntent(context, bmp, ExportFormat.JPG, Constants.DEFAULT_EXPORT_QUALITY)
    }

    fun dismissError() {
        _state.value = _state.value.copy(status = if (_state.value.hasImage) ToolStatus.ImageSelected else ToolStatus.Idle)
    }
    fun consumeExportSuccess() { _state.value = _state.value.copy(status = ToolStatus.ImageSelected) }
}
