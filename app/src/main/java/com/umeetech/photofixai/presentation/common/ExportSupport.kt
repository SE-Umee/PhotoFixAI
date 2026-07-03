package com.umeetech.photofixai.presentation.common

import android.content.Context
import android.graphics.Bitmap
import com.umeetech.photofixai.core.export.ExportFormat
import com.umeetech.photofixai.core.export.ExportResult
import com.umeetech.photofixai.core.image.ImageExporter
import com.umeetech.photofixai.core.result.AppError
import com.umeetech.photofixai.core.result.Resource
import com.umeetech.photofixai.di.AppContainer
import com.umeetech.photofixai.domain.model.HistoryItem
import com.umeetech.photofixai.domain.model.ToolType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

/**
 * Reusable export pipeline for every tool: encode → save to gallery → record in
 * History (Room). Always runs on IO so the main thread stays smooth.
 */
object ExportSupport {

    /**
     * The export quality the user selected in Settings (Standard/High/Maximum).
     * Tools that don't have their own quality logic (e.g. compression targets)
     * should resolve their save quality through this so the setting is honored.
     */
    suspend fun currentQuality(container: AppContainer): Int =
        container.settingsRepository.settings.first().exportQuality.quality

    suspend fun exportAndRecord(
        context: Context,
        container: AppContainer,
        bitmap: Bitmap,
        format: ExportFormat,
        quality: Int,
        tool: ToolType
    ): Resource<ExportResult> = withContext(Dispatchers.IO) {
        try {
            val result = ImageExporter.exportToGallery(
                context = context,
                bitmap = bitmap,
                format = format,
                quality = quality,
                baseName = ImageExporter.defaultName(tool.name.lowercase())
            )
            container.saveToHistoryUseCase(
                HistoryItem(
                    thumbnailPath = result.uri.toString(),
                    filePath = result.uri.toString(),
                    toolType = tool,
                    createdAt = System.currentTimeMillis(),
                    fileSizeBytes = result.sizeBytes,
                    outputFormat = format.label,
                    width = result.width,
                    height = result.height
                )
            )
            Resource.Success(result)
        } catch (t: Throwable) {
            Resource.Error(AppError.ExportFailed)
        }
    }

    /** Builds a share Intent for [bitmap] in [format] (does not save to gallery). */
    fun buildShareIntent(context: Context, bitmap: Bitmap, format: ExportFormat, quality: Int) =
        ImageExporter.buildShareIntent(context, ImageExporter.encode(bitmap, format, quality), format)
}
