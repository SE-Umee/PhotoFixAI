package com.umeetech.photofixai.presentation.screens.signature

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.umeetech.photofixai.core.export.ExportFormat
import com.umeetech.photofixai.core.image.BackgroundComposer
import com.umeetech.photofixai.core.image.BitmapUtils
import com.umeetech.photofixai.core.image.FileSizeUtils
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

data class SignatureUiState(
    val status: ToolStatus = ToolStatus.Idle,
    val original: Bitmap? = null,
    val processed: Bitmap? = null,
    val removeWhite: Boolean = true,
    val targetWidth: Int = 0,
    val targetHeight: Int = 0,
    val targetKb: Int = 50,
    val format: ExportFormat = ExportFormat.PNG,
    val estimatedBytes: Long = 0,
    val exported: Boolean = false
) {
    val hasImage: Boolean get() = original != null
}

class SignatureViewModel(private val container: AppContainer) : ViewModel() {

    private val _state = MutableStateFlow(SignatureUiState())
    val state: StateFlow<SignatureUiState> = _state

    fun onImagePicked(context: Context, uri: Uri?) {
        if (uri == null) return
        viewModelScope.launch {
            _state.value = _state.value.copy(status = ToolStatus.Processing)
            val bmp = withContext(Dispatchers.IO) { BitmapUtils.decodeSampledBitmap(context, uri) }
            if (bmp == null) {
                _state.value = _state.value.copy(status = ToolStatus.Error(AppError.UnsupportedFormat)); return@launch
            }
            _state.value = SignatureUiState(
                status = ToolStatus.ImageSelected,
                original = bmp,
                targetWidth = bmp.width,
                targetHeight = bmp.height
            )
            recompose()
        }
    }

    fun toggleRemoveWhite(enabled: Boolean) { _state.value = _state.value.copy(removeWhite = enabled); recompose() }
    fun setWidth(w: Int) { _state.value = _state.value.copy(targetWidth = w.coerceAtLeast(1)); recompose() }
    fun setHeight(h: Int) { _state.value = _state.value.copy(targetHeight = h.coerceAtLeast(1)); recompose() }
    fun setTargetKb(kb: Int) { _state.value = _state.value.copy(targetKb = kb.coerceAtLeast(1)) }
    fun setFormat(format: ExportFormat) {
        _state.value = _state.value.copy(format = format)
        recompose()
    }

    private fun buildProcessed(): Bitmap? {
        val s = _state.value
        val original = s.original ?: return null
        var bmp = original
        // Only PNG/WebP keep transparency; skip white removal for JPG.
        if (s.removeWhite && s.format.supportsTransparency) {
            bmp = BackgroundComposer.removeWhiteBackground(bmp)
        }
        return ImageResizer.resize(bmp, s.targetWidth, s.targetHeight)
    }

    private fun recompose() {
        viewModelScope.launch(Dispatchers.Default) {
            val processed = buildProcessed() ?: return@launch
            val bytes = ImageCompressor.compressToTargetSize(
                processed, _state.value.format.compressFormat(), FileSizeUtils.kbToBytes(_state.value.targetKb)
            )
            _state.value = _state.value.copy(processed = processed, estimatedBytes = bytes.sizeBytes)
        }
    }

    fun export(context: Context, onDone: () -> Unit) {
        viewModelScope.launch {
            _state.value = _state.value.copy(status = ToolStatus.Exporting)
            val processed = withContext(Dispatchers.Default) { buildProcessed() }
            if (processed == null) { _state.value = _state.value.copy(status = ToolStatus.Error(AppError.ExportFailed)); return@launch }
            // Derive an approximate quality from the target size for JPG/WebP.
            val quality = withContext(Dispatchers.Default) {
                ImageCompressor.compressToTargetSize(
                    processed, _state.value.format.compressFormat(), FileSizeUtils.kbToBytes(_state.value.targetKb)
                ).quality
            }
            when (val res = ExportSupport.exportAndRecord(
                context, container, processed, _state.value.format, quality, ToolType.SIGNATURE
            )) {
                is Resource.Success -> { _state.value = _state.value.copy(status = ToolStatus.ExportSuccess, exported = true); onDone() }
                is Resource.Error -> _state.value = _state.value.copy(status = ToolStatus.Error(res.error))
                Resource.Loading -> Unit
            }
        }
    }

    fun buildShareIntent(context: Context): Intent? {
        val processed = buildProcessed() ?: return null
        return ExportSupport.buildShareIntent(context, processed, _state.value.format, 90)
    }

    fun dismissError() {
        _state.value = _state.value.copy(status = if (_state.value.hasImage) ToolStatus.ImageSelected else ToolStatus.Idle)
    }
    fun consumeExportSuccess() { _state.value = _state.value.copy(status = ToolStatus.ImageSelected) }
}
