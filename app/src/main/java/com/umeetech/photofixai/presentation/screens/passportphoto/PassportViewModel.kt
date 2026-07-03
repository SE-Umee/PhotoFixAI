package com.umeetech.photofixai.presentation.screens.passportphoto

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.umeetech.photofixai.core.constants.Constants
import com.umeetech.photofixai.core.export.ExportFormat
import com.umeetech.photofixai.core.image.BackgroundComposer
import com.umeetech.photofixai.core.image.BitmapUtils
import com.umeetech.photofixai.core.image.PassportSheetGenerator
import com.umeetech.photofixai.core.result.AppError
import com.umeetech.photofixai.core.result.Resource
import com.umeetech.photofixai.di.AppContainer
import com.umeetech.photofixai.domain.model.BackgroundOption
import com.umeetech.photofixai.domain.model.PassportTemplate
import com.umeetech.photofixai.domain.model.PassportTemplates
import com.umeetech.photofixai.domain.model.ToolType
import com.umeetech.photofixai.presentation.common.ExportSupport
import com.umeetech.photofixai.presentation.common.ToolStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class PassportUiState(
    val status: ToolStatus = ToolStatus.Idle,
    val original: Bitmap? = null,
    val cutout: Bitmap? = null,
    val preview: Bitmap? = null,
    val template: PassportTemplate = PassportTemplates.Pakistan,
    val backgroundId: String = BackgroundOption.White.id,
    val customColor: Long = 0xFFFFFFFF,
    val copies: Int = 4,
    val exported: Boolean = false
) {
    val hasImage: Boolean get() = original != null
}

class PassportViewModel(private val container: AppContainer) : ViewModel() {

    private val _state = MutableStateFlow(PassportUiState())
    val state: StateFlow<PassportUiState> = _state

    val templates = PassportTemplates.all

    fun onImagePicked(context: Context, uri: Uri?) {
        if (uri == null) return
        viewModelScope.launch {
            _state.value = _state.value.copy(status = ToolStatus.Processing)
            val bmp = withContext(Dispatchers.IO) { BitmapUtils.decodeSampledBitmap(context, uri) }
            if (bmp == null) {
                _state.value = _state.value.copy(status = ToolStatus.Error(AppError.UnsupportedFormat))
                return@launch
            }
            _state.value = PassportUiState(status = ToolStatus.ImageSelected, original = bmp)
            recompose()
        }
    }

    fun selectTemplate(template: PassportTemplate) {
        _state.value = _state.value.copy(
            template = template,
            backgroundId = template.requiredBackground.id
        )
        recompose()
    }

    fun selectBackground(option: BackgroundOption, customColor: Long? = null) {
        _state.value = _state.value.copy(backgroundId = option.id, customColor = customColor ?: _state.value.customColor)
        recompose()
    }

    fun setCopies(copies: Int) { _state.value = _state.value.copy(copies = copies) }

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

    /** Builds the single, cropped passport photo composited on the chosen bg. */
    private fun buildSinglePhoto(): Bitmap? {
        val s = _state.value
        val base = s.cutout ?: s.original ?: return null
        val color = resolveColor(s.backgroundId, s.customColor).toInt()
        val composed = if (s.cutout != null) BackgroundComposer.composeOnColor(base, color) else base
        return BitmapUtils.cropToAspectRatio(composed, s.template.aspectRatio)
    }

    private fun recompose() {
        _state.value = _state.value.copy(preview = buildSinglePhoto())
    }

    private fun resolveColor(id: String, custom: Long): Long = when (id) {
        BackgroundOption.White.id -> 0xFFFFFFFF
        BackgroundOption.Blue.id -> 0xFF2563EB
        BackgroundOption.Red.id -> 0xFFEF4444
        BackgroundOption.Custom.id -> custom
        else -> 0xFFFFFFFF
    }

    fun exportSingle(context: Context, onDone: () -> Unit) = doExport(context, sheet = false, onDone)
    fun exportSheet(context: Context, onDone: () -> Unit) = doExport(context, sheet = true, onDone)

    private fun doExport(context: Context, sheet: Boolean, onDone: () -> Unit) {
        viewModelScope.launch {
            _state.value = _state.value.copy(status = ToolStatus.Exporting)
            val bitmap = withContext(Dispatchers.Default) {
                val single = buildSinglePhoto() ?: return@withContext null
                if (sheet) PassportSheetGenerator.generateSheet(single, _state.value.copies) else single
            }
            if (bitmap == null) {
                _state.value = _state.value.copy(status = ToolStatus.Error(AppError.ExportFailed)); return@launch
            }
            when (val res = ExportSupport.exportAndRecord(
                context, container, bitmap, ExportFormat.JPG, Constants.DEFAULT_EXPORT_QUALITY, ToolType.PASSPORT_PHOTO
            )) {
                is Resource.Success -> { _state.value = _state.value.copy(status = ToolStatus.ExportSuccess, exported = true); onDone() }
                is Resource.Error -> _state.value = _state.value.copy(status = ToolStatus.Error(res.error))
                Resource.Loading -> Unit
            }
        }
    }

    fun buildShareIntent(context: Context): Intent? {
        val bitmap = buildSinglePhoto() ?: return null
        return ExportSupport.buildShareIntent(context, bitmap, ExportFormat.JPG, Constants.DEFAULT_EXPORT_QUALITY)
    }

    fun dismissError() {
        _state.value = _state.value.copy(status = if (_state.value.hasImage) ToolStatus.ImageSelected else ToolStatus.Idle)
    }
    fun consumeExportSuccess() { _state.value = _state.value.copy(status = ToolStatus.ImageSelected) }
}
