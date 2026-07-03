package com.umeetech.photofixai.presentation.screens.resize

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.umeetech.photofixai.core.constants.Constants
import com.umeetech.photofixai.core.export.ExportFormat
import com.umeetech.photofixai.core.image.BitmapUtils
import com.umeetech.photofixai.core.image.ImageCompressor
import com.umeetech.photofixai.core.image.ImageResizer
import com.umeetech.photofixai.core.result.AppError
import com.umeetech.photofixai.core.result.Resource
import com.umeetech.photofixai.di.AppContainer
import com.umeetech.photofixai.domain.model.ToolType
import com.umeetech.photofixai.presentation.common.ExportSupport
import com.umeetech.photofixai.presentation.common.ToolStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class ResizeUiState(
    val status: ToolStatus = ToolStatus.Idle,
    val original: Bitmap? = null,
    val originalWidth: Int = 0,
    val originalHeight: Int = 0,
    val targetWidth: Int = 0,
    val targetHeight: Int = 0,
    val keepAspect: Boolean = true,
    val percentage: Int = 100,
    val format: ExportFormat = ExportFormat.JPG,
    val estimatedBytes: Long = 0,
    val exported: Boolean = false
) {
    val hasImage: Boolean get() = original != null
}

class ResizeViewModel(private val container: AppContainer) : ViewModel() {

    private val _state = MutableStateFlow(ResizeUiState())
    val state: StateFlow<ResizeUiState> = _state

    fun onImagePicked(context: Context, uri: Uri?) {
        if (uri == null) return
        viewModelScope.launch {
            _state.value = _state.value.copy(status = ToolStatus.Processing)
            val bmp = withContext(Dispatchers.IO) { BitmapUtils.decodeSampledBitmap(context, uri) }
            if (bmp == null) {
                _state.value = _state.value.copy(status = ToolStatus.Error(AppError.UnsupportedFormat))
                return@launch
            }
            _state.value = ResizeUiState(
                status = ToolStatus.ImageSelected,
                original = bmp,
                originalWidth = bmp.width,
                originalHeight = bmp.height,
                targetWidth = bmp.width,
                targetHeight = bmp.height
            )
            estimateSize()
        }
    }

    fun setWidth(width: Int) {
        val s = _state.value
        val h = if (s.keepAspect) ImageResizer.heightForWidth(width, s.originalWidth, s.originalHeight) else s.targetHeight
        _state.value = s.copy(targetWidth = width.coerceAtLeast(1), targetHeight = h)
        estimateSize()
    }

    fun setHeight(height: Int) {
        val s = _state.value
        val w = if (s.keepAspect) ImageResizer.widthForHeight(height, s.originalWidth, s.originalHeight) else s.targetWidth
        _state.value = s.copy(targetHeight = height.coerceAtLeast(1), targetWidth = w)
        estimateSize()
    }

    fun setPercentage(percent: Int) {
        val s = _state.value
        val w = (s.originalWidth * percent / 100).coerceAtLeast(1)
        val h = (s.originalHeight * percent / 100).coerceAtLeast(1)
        _state.value = s.copy(percentage = percent, targetWidth = w, targetHeight = h)
        estimateSize()
    }

    fun toggleKeepAspect(keep: Boolean) {
        _state.value = _state.value.copy(keepAspect = keep)
        if (keep) setWidth(_state.value.targetWidth)
    }

    fun setFormat(format: ExportFormat) {
        _state.value = _state.value.copy(format = format)
        estimateSize()
    }

    private fun buildResized(): Bitmap? {
        val s = _state.value
        val original = s.original ?: return null
        return ImageResizer.resize(original, s.targetWidth, s.targetHeight)
    }

    private fun estimateSize() {
        viewModelScope.launch(Dispatchers.Default) {
            val resized = buildResized() ?: return@launch
            val bytes = ImageCompressor.compress(resized, _state.value.format.compressFormat(), Constants.DEFAULT_EXPORT_QUALITY)
            _state.value = _state.value.copy(estimatedBytes = bytes.size.toLong())
        }
    }

    fun export(context: Context, onDone: () -> Unit) {
        viewModelScope.launch {
            val resized = withContext(Dispatchers.Default) { buildResized() } ?: return@launch
            _state.value = _state.value.copy(status = ToolStatus.Exporting)
            when (val res = ExportSupport.exportAndRecord(
                context, container, resized, _state.value.format, Constants.DEFAULT_EXPORT_QUALITY, ToolType.RESIZE
            )) {
                is Resource.Success -> {
                    _state.value = _state.value.copy(status = ToolStatus.ExportSuccess, exported = true)
                    onDone()
                }
                is Resource.Error -> _state.value = _state.value.copy(status = ToolStatus.Error(res.error))
                Resource.Loading -> Unit
            }
        }
    }

    fun buildShareIntent(context: Context): Intent? {
        val resized = buildResized() ?: return null
        return ExportSupport.buildShareIntent(context, resized, _state.value.format, Constants.DEFAULT_EXPORT_QUALITY)
    }

    fun dismissError() {
        _state.value = _state.value.copy(status = if (_state.value.hasImage) ToolStatus.ImageSelected else ToolStatus.Idle)
    }

    fun consumeExportSuccess() {
        _state.value = _state.value.copy(status = ToolStatus.ImageSelected)
    }
}
