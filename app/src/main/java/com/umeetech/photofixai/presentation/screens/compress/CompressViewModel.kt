package com.umeetech.photofixai.presentation.screens.compress

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.umeetech.photofixai.core.export.ExportFormat
import com.umeetech.photofixai.core.image.BitmapUtils
import com.umeetech.photofixai.core.image.FileSizeUtils
import com.umeetech.photofixai.core.image.ImageCompressor
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

/** Preset target sizes; [customKb] used when [CustomTarget] is selected. */
enum class CompressTarget(val label: String, val kb: Int?) {
    KB_50("Under 50KB", 50),
    KB_100("Under 100KB", 100),
    KB_200("Under 200KB", 200),
    KB_500("Under 500KB", 500),
    CUSTOM("Custom", null)
}

data class CompressUiState(
    val status: ToolStatus = ToolStatus.Idle,
    val original: Bitmap? = null,
    val originalSizeBytes: Long = 0,
    val compressedBytes: Long = 0,
    val target: CompressTarget = CompressTarget.KB_100,
    val customKb: Int = 100,
    val quality: Int = 80,
    val useQualityMode: Boolean = false,
    val format: ExportFormat = ExportFormat.JPG,
    val compressedResult: ByteArray? = null,
    val exported: Boolean = false
) {
    val hasImage: Boolean get() = original != null
    val savingText: String
        get() = if (originalSizeBytes > 0 && compressedBytes in 1 until originalSizeBytes) {
            val pct = 100 - (compressedBytes * 100 / originalSizeBytes)
            "$pct% smaller"
        } else ""
}

class CompressViewModel(private val container: AppContainer) : ViewModel() {

    private val _state = MutableStateFlow(CompressUiState())
    val state: StateFlow<CompressUiState> = _state

    fun onImagePicked(context: Context, uri: Uri?) {
        if (uri == null) return
        viewModelScope.launch {
            _state.value = _state.value.copy(status = ToolStatus.Processing)
            val bmp = withContext(Dispatchers.IO) { BitmapUtils.decodeSampledBitmap(context, uri) }
            val meta = withContext(Dispatchers.IO) { BitmapUtils.readImageMeta(context, uri) }
            if (bmp == null) {
                _state.value = _state.value.copy(status = ToolStatus.Error(AppError.UnsupportedFormat))
                return@launch
            }
            _state.value = CompressUiState(
                status = ToolStatus.ImageSelected,
                original = bmp,
                originalSizeBytes = meta?.sizeBytes ?: 0
            )
        }
    }

    fun setTarget(target: CompressTarget) { _state.value = _state.value.copy(target = target) }
    fun setCustomKb(kb: Int) { _state.value = _state.value.copy(customKb = kb.coerceAtLeast(1)) }
    fun setQuality(quality: Int) { _state.value = _state.value.copy(quality = quality, useQualityMode = true) }
    fun setFormat(format: ExportFormat) { _state.value = _state.value.copy(format = format) }

    fun compress() {
        val original = _state.value.original ?: return
        viewModelScope.launch {
            _state.value = _state.value.copy(status = ToolStatus.Processing)
            val s = _state.value
            val bytes = withContext(Dispatchers.Default) {
                if (s.useQualityMode) {
                    ImageCompressor.compress(original, s.format.compressFormat(), s.quality)
                } else {
                    val kb = if (s.target == CompressTarget.CUSTOM) s.customKb else s.target.kb ?: 100
                    ImageCompressor.compressToTargetSize(
                        original, s.format.compressFormat(), FileSizeUtils.kbToBytes(kb)
                    ).bytes
                }
            }
            _state.value = _state.value.copy(
                status = ToolStatus.Success,
                compressedResult = bytes,
                compressedBytes = bytes.size.toLong()
            )
        }
    }

    fun export(context: Context, onDone: () -> Unit) {
        val original = _state.value.original ?: return
        viewModelScope.launch {
            _state.value = _state.value.copy(status = ToolStatus.Exporting)
            // Re-decode compressed bytes into a bitmap for the reusable export pipeline.
            val bytes = _state.value.compressedResult
            val quality = _state.value.quality
            val bmp = if (bytes != null) {
                withContext(Dispatchers.Default) {
                    android.graphics.BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                } ?: original
            } else original
            when (val res = ExportSupport.exportAndRecord(
                context, container, bmp, _state.value.format, quality, ToolType.COMPRESS
            )) {
                is Resource.Success -> { _state.value = _state.value.copy(status = ToolStatus.ExportSuccess, exported = true); onDone() }
                is Resource.Error -> _state.value = _state.value.copy(status = ToolStatus.Error(res.error))
                Resource.Loading -> Unit
            }
        }
    }

    fun buildShareIntent(context: Context): Intent? {
        val original = _state.value.original ?: return null
        return ExportSupport.buildShareIntent(context, original, _state.value.format, _state.value.quality)
    }

    fun dismissError() {
        _state.value = _state.value.copy(status = if (_state.value.hasImage) ToolStatus.ImageSelected else ToolStatus.Idle)
    }
    fun consumeExportSuccess() { _state.value = _state.value.copy(status = ToolStatus.Success) }
}
