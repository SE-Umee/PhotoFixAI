package com.umeetech.photofixai.core.image

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import androidx.core.content.FileProvider
import com.umeetech.photofixai.core.constants.Constants
import com.umeetech.photofixai.core.export.ExportFormat
import com.umeetech.photofixai.core.export.ExportResult
import java.io.ByteArrayOutputStream

/**
 * High-level, reusable export pipeline used by every tool: encode a bitmap into
 * the requested [ExportFormat], then save it to the gallery / app storage and/or
 * hand it to the Android share sheet.
 *
 * Call from a background coroutine — encoding + IO must not run on the main thread.
 */
object ImageExporter {

    fun encode(bitmap: Bitmap, format: ExportFormat, quality: Int): ByteArray {
        val out = ByteArrayOutputStream()
        // Flatten transparency for JPG (which has no alpha channel) onto white.
        val toEncode = if (format == ExportFormat.JPG && bitmap.hasAlpha()) {
            BackgroundComposer.composeOnColor(bitmap, android.graphics.Color.WHITE)
        } else {
            bitmap
        }
        toEncode.compress(format.compressFormat(), quality.coerceIn(1, 100), out)
        return out.toByteArray()
    }

    /** Encodes and saves to the shared gallery, returning an [ExportResult]. */
    fun exportToGallery(
        context: Context,
        bitmap: Bitmap,
        format: ExportFormat,
        quality: Int = Constants.DEFAULT_EXPORT_QUALITY,
        baseName: String = defaultName()
    ): ExportResult {
        val bytes = encode(bitmap, format, quality)
        val fileName = "$baseName.${format.extension}"
        val uri = MediaStoreSaver.saveToGallery(context, bytes, fileName, format)
        // Keep an app-private copy so history thumbnails survive gallery deletion.
        val appFile = MediaStoreSaver.saveToAppStorage(context, bytes, fileName)
        return ExportResult(
            uri = uri,
            displayPath = "Pictures/${Constants.GALLERY_ALBUM}/$fileName",
            format = format,
            sizeBytes = appFile.length(),
            width = bitmap.width,
            height = bitmap.height,
            savedToGallery = true
        )
    }

    /** Creates a share Intent backed by a FileProvider Uri for [bytes]. */
    fun buildShareIntent(context: Context, bytes: ByteArray, format: ExportFormat): Intent {
        val file = MediaStoreSaver.writeShareCache(
            context,
            bytes,
            "photofix_share_${System.currentTimeMillis()}.${format.extension}"
        )
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
        return Intent(Intent.ACTION_SEND).apply {
            type = format.mimeType
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
    }

    fun defaultName(prefix: String = "photofix"): String =
        "${prefix}_${System.currentTimeMillis()}"
}
