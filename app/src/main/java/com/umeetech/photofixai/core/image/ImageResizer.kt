package com.umeetech.photofixai.core.image

import android.graphics.Bitmap
import androidx.core.graphics.scale
import kotlin.math.roundToInt

/** Pure resize logic (aspect-ratio aware + percentage based). */
object ImageResizer {

    fun resize(source: Bitmap, targetWidth: Int, targetHeight: Int): Bitmap {
        val w = targetWidth.coerceAtLeast(1)
        val h = targetHeight.coerceAtLeast(1)
        return source.scale(w, h)
    }

    fun resizeByPercentage(source: Bitmap, percent: Int): Bitmap {
        val factor = percent.coerceIn(1, 500) / 100f
        val w = (source.width * factor).roundToInt().coerceAtLeast(1)
        val h = (source.height * factor).roundToInt().coerceAtLeast(1)
        return source.scale(w, h)
    }

    /** Given a new width and the original dimensions, returns the aspect-locked height. */
    fun heightForWidth(width: Int, originalWidth: Int, originalHeight: Int): Int {
        if (originalWidth == 0) return width
        return (width.toFloat() * originalHeight / originalWidth).roundToInt().coerceAtLeast(1)
    }

    fun widthForHeight(height: Int, originalWidth: Int, originalHeight: Int): Int {
        if (originalHeight == 0) return height
        return (height.toFloat() * originalWidth / originalHeight).roundToInt().coerceAtLeast(1)
    }
}
