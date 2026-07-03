package com.umeetech.photofixai.core.image

import android.graphics.Bitmap
import android.os.Build
import java.io.ByteArrayOutputStream

/**
 * Smart image compression.
 *
 * [compressToTargetSize] performs a binary search over JPEG/WebP quality to hit a
 * target byte budget without over-compressing: if the image already fits under the
 * target at max quality, it is returned as-is.
 */
object ImageCompressor {

    data class CompressionResult(val bytes: ByteArray, val quality: Int, val sizeBytes: Long)

    fun compress(bitmap: Bitmap, format: Bitmap.CompressFormat, quality: Int): ByteArray {
        val out = ByteArrayOutputStream()
        bitmap.compress(format, quality.coerceIn(1, 100), out)
        return out.toByteArray()
    }

    /**
     * Binary-search the highest quality whose output is <= [targetSizeBytes].
     * Returns the best attempt even if the target can't be met (smallest produced).
     */
    fun compressToTargetSize(
        bitmap: Bitmap,
        format: Bitmap.CompressFormat,
        targetSizeBytes: Long,
        minQuality: Int = 10,
        maxQuality: Int = 100
    ): CompressionResult {
        // Smart short-circuit: don't degrade quality if already under target.
        val best = compress(bitmap, format, maxQuality)
        if (best.size <= targetSizeBytes) {
            return CompressionResult(best, maxQuality, best.size.toLong())
        }

        var low = minQuality
        var high = maxQuality
        var bestUnder: ByteArray? = null
        var bestQuality = minQuality
        var smallest: ByteArray = best
        var smallestQuality = maxQuality

        while (low <= high) {
            val mid = (low + high) / 2
            val bytes = compress(bitmap, format, mid)
            if (bytes.size <= targetSizeBytes) {
                bestUnder = bytes
                bestQuality = mid
                low = mid + 1
            } else {
                high = mid - 1
            }
            if (bytes.size < smallest.size) {
                smallest = bytes
                smallestQuality = mid
            }
        }

        return if (bestUnder != null) {
            CompressionResult(bestUnder, bestQuality, bestUnder.size.toLong())
        } else {
            CompressionResult(smallest, smallestQuality, smallest.size.toLong())
        }
    }

    /** WebP compress format that adapts to API level (lossy on 30+). */
    @Suppress("DEPRECATION")
    fun webpFormat(): Bitmap.CompressFormat =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Bitmap.CompressFormat.WEBP_LOSSY
        } else {
            Bitmap.CompressFormat.WEBP
        }
}
