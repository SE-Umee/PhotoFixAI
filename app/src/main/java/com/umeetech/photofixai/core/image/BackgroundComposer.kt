package com.umeetech.photofixai.core.image

import android.graphics.Bitmap
import android.graphics.BlurMaskFilter
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.RectF

/**
 * Composites a foreground (with transparency) onto solid backgrounds and adds
 * marketplace-style soft shadows for the Product Photo Maker.
 */
object BackgroundComposer {

    /** Draws a transparent foreground over a solid [backgroundColor]. */
    fun composeOnColor(foreground: Bitmap, backgroundColor: Int): Bitmap {
        val result = Bitmap.createBitmap(foreground.width, foreground.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(result)
        canvas.drawColor(backgroundColor)
        canvas.drawBitmap(foreground, 0f, 0f, null)
        return result
    }

    /** Returns a copy on a transparent canvas (no-op guarantee of ARGB_8888). */
    fun ensureTransparent(foreground: Bitmap): Bitmap {
        if (foreground.config == Bitmap.Config.ARGB_8888) return foreground
        return foreground.copy(Bitmap.Config.ARGB_8888, false)
    }

    /**
     * Adds a soft drop shadow beneath the subject on a colored background — gives
     * product photos a premium, floating look.
     */
    fun composeWithSoftShadow(
        foreground: Bitmap,
        backgroundColor: Int,
        shadowRadius: Float = 24f,
        shadowAlpha: Int = 90,
        verticalOffset: Float = 18f
    ): Bitmap {
        val padding = (shadowRadius * 2).toInt()
        val result = Bitmap.createBitmap(
            foreground.width + padding * 2,
            foreground.height + padding * 2,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(result)
        canvas.drawColor(backgroundColor)

        // Build a shadow from the foreground's alpha silhouette.
        val alpha = foreground.extractAlpha()
        val shadowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.argb(shadowAlpha, 0, 0, 0)
            maskFilter = BlurMaskFilter(shadowRadius, BlurMaskFilter.Blur.NORMAL)
        }
        canvas.drawBitmap(alpha, padding.toFloat(), padding + verticalOffset, shadowPaint)
        alpha.recycle()

        canvas.drawBitmap(foreground, padding.toFloat(), padding.toFloat(), null)
        return result
    }

    /**
     * Naive white-background removal for signatures / documents: pixels close to
     * white become transparent. Threshold 0..255 (higher = more aggressive).
     */
    fun removeWhiteBackground(source: Bitmap, threshold: Int = 235): Bitmap {
        val result = source.copy(Bitmap.Config.ARGB_8888, true)
        val width = result.width
        val height = result.height
        val pixels = IntArray(width * height)
        result.getPixels(pixels, 0, width, 0, 0, width, height)
        for (i in pixels.indices) {
            val p = pixels[i]
            val r = Color.red(p)
            val g = Color.green(p)
            val b = Color.blue(p)
            if (r >= threshold && g >= threshold && b >= threshold) {
                pixels[i] = Color.TRANSPARENT
            }
        }
        result.setPixels(pixels, 0, width, 0, 0, width, height)
        return result
    }

    @Suppress("unused")
    private fun drawRoundedRect(canvas: Canvas, rect: RectF, radius: Float, paint: Paint) {
        canvas.drawRoundRect(rect, radius, radius, paint)
    }

    private fun Paint.clearMode() {
        xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
    }
}
