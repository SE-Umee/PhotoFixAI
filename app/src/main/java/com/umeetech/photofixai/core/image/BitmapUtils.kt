package com.umeetech.photofixai.core.image

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import androidx.core.graphics.scale
import com.umeetech.photofixai.core.constants.Constants
import kotlin.math.max
import kotlin.math.roundToInt

/**
 * Core bitmap operations shared by every tool.
 *
 * All decoding is memory-conscious: large images are sub-sampled at decode time
 * and then optionally downscaled, protecting against OutOfMemory crashes. Heavy
 * work here MUST be called from a background dispatcher (see repositories/use cases).
 */
object BitmapUtils {

    data class ImageMeta(
        val width: Int,
        val height: Int,
        val sizeBytes: Long,
        val displayName: String,
        val mimeType: String?
    )

    /**
     * Decodes a [Uri] into a memory-safe [Bitmap] with EXIF orientation applied
     * and the longest edge capped at [maxDimension].
     */
    fun decodeSampledBitmap(
        context: Context,
        uri: Uri,
        maxDimension: Int = Constants.MAX_PROCESSING_DIMENSION
    ): Bitmap? {
        val resolver = context.contentResolver

        // First pass: read bounds only.
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        resolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, bounds) }
        if (bounds.outWidth <= 0 || bounds.outHeight <= 0) return null

        val options = BitmapFactory.Options().apply {
            inSampleSize = calculateInSampleSize(bounds.outWidth, bounds.outHeight, maxDimension)
            inPreferredConfig = Bitmap.Config.ARGB_8888
        }

        var bitmap = resolver.openInputStream(uri)?.use {
            BitmapFactory.decodeStream(it, null, options)
        } ?: return null

        // Apply EXIF orientation from a fresh stream.
        resolver.openInputStream(uri)?.use { stream ->
            bitmap = ExifUtils.applyExifOrientation(bitmap, stream)
        }

        // Final safety clamp in case sub-sampling left it above the cap.
        if (max(bitmap.width, bitmap.height) > maxDimension) {
            bitmap = downscale(bitmap, maxDimension)
        }
        return bitmap
    }

    fun readImageMeta(context: Context, uri: Uri): ImageMeta? {
        val resolver = context.contentResolver
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        resolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, bounds) }
        if (bounds.outWidth <= 0) return null

        var name = "image"
        var size = 0L
        runCatching {
            resolver.query(uri, null, null, null, null)?.use { cursor ->
                val nameIndex = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                val sizeIndex = cursor.getColumnIndex(android.provider.OpenableColumns.SIZE)
                if (cursor.moveToFirst()) {
                    if (nameIndex >= 0) name = cursor.getString(nameIndex) ?: name
                    if (sizeIndex >= 0) size = cursor.getLong(sizeIndex)
                }
            }
        }
        return ImageMeta(
            width = bounds.outWidth,
            height = bounds.outHeight,
            sizeBytes = size,
            displayName = name,
            mimeType = bounds.outMimeType
        )
    }

    private fun calculateInSampleSize(width: Int, height: Int, reqDim: Int): Int {
        var inSampleSize = 1
        var halfW = width / 2
        var halfH = height / 2
        while (halfW / inSampleSize >= reqDim || halfH / inSampleSize >= reqDim) {
            inSampleSize *= 2
        }
        return inSampleSize
    }

    fun downscale(bitmap: Bitmap, maxDimension: Int): Bitmap {
        val longest = max(bitmap.width, bitmap.height)
        if (longest <= maxDimension) return bitmap
        val scale = maxDimension.toFloat() / longest
        val w = (bitmap.width * scale).roundToInt().coerceAtLeast(1)
        val h = (bitmap.height * scale).roundToInt().coerceAtLeast(1)
        return bitmap.scale(w, h)
    }

    fun rotate(bitmap: Bitmap, degrees: Float): Bitmap {
        if (degrees % 360f == 0f) return bitmap
        val matrix = Matrix().apply { postRotate(degrees) }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    /** Crops with a normalized rect (0f..1f) relative to the bitmap size. */
    fun cropNormalized(
        bitmap: Bitmap,
        left: Float,
        top: Float,
        right: Float,
        bottom: Float
    ): Bitmap {
        val x = (left.coerceIn(0f, 1f) * bitmap.width).roundToInt()
        val y = (top.coerceIn(0f, 1f) * bitmap.height).roundToInt()
        val w = ((right - left).coerceIn(0f, 1f) * bitmap.width).roundToInt().coerceAtLeast(1)
        val h = ((bottom - top).coerceIn(0f, 1f) * bitmap.height).roundToInt().coerceAtLeast(1)
        val safeW = w.coerceAtMost(bitmap.width - x)
        val safeH = h.coerceAtMost(bitmap.height - y)
        return Bitmap.createBitmap(bitmap, x, y, safeW, safeH)
    }

    /** Center-crops to a target aspect ratio (width / height). */
    fun cropToAspectRatio(bitmap: Bitmap, ratio: Float): Bitmap {
        val currentRatio = bitmap.width.toFloat() / bitmap.height
        return if (currentRatio > ratio) {
            val targetW = (bitmap.height * ratio).roundToInt()
            val x = (bitmap.width - targetW) / 2
            Bitmap.createBitmap(bitmap, x, 0, targetW, bitmap.height)
        } else {
            val targetH = (bitmap.width / ratio).roundToInt()
            val y = (bitmap.height - targetH) / 2
            Bitmap.createBitmap(bitmap, 0, y, bitmap.width, targetH)
        }
    }
}
