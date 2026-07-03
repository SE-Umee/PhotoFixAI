package com.umeetech.photofixai.core.export

import android.graphics.Bitmap
import android.os.Build

/** Output image formats supported by the reusable export system. */
enum class ExportFormat(
    val label: String,
    val extension: String,
    val mimeType: String
) {
    PNG("PNG", "png", "image/png"),
    JPG("JPG", "jpg", "image/jpeg"),
    WEBP("WebP", "webp", "image/webp");

    val supportsTransparency: Boolean get() = this == PNG || this == WEBP

    @Suppress("DEPRECATION")
    fun compressFormat(): Bitmap.CompressFormat = when (this) {
        PNG -> Bitmap.CompressFormat.PNG
        JPG -> Bitmap.CompressFormat.JPEG
        WEBP -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Bitmap.CompressFormat.WEBP_LOSSY
        } else {
            Bitmap.CompressFormat.WEBP
        }
    }
}
