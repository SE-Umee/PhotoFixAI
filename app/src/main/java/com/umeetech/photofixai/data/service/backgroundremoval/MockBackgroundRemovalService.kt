package com.umeetech.photofixai.data.service.backgroundremoval

import android.graphics.Bitmap
import android.graphics.Color
import com.umeetech.photofixai.core.result.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

/**
 * MVP / test implementation. Produces a plausible transparent result WITHOUT any
 * network call or ML model, so the whole flow (pick → process → edit → export) can
 * be developed and tested end-to-end.
 *
 * Heuristic: samples the four corners to estimate the dominant background color and
 * makes similar pixels transparent (works well on solid/near-solid backgrounds).
 */
class MockBackgroundRemovalService : BackgroundRemovalService {

    override suspend fun removeBackground(source: Bitmap): Resource<Bitmap> =
        withContext(Dispatchers.Default) {
            try {
                delay(900) // simulate processing so loading states are exercised.
                val result = source.copy(Bitmap.Config.ARGB_8888, true)
                val w = result.width
                val h = result.height
                val pixels = IntArray(w * h)
                result.getPixels(pixels, 0, w, 0, 0, w, h)

                val bg = estimateBackgroundColor(pixels, w, h)
                val tolerance = 48
                for (i in pixels.indices) {
                    if (colorDistance(pixels[i], bg) < tolerance) {
                        pixels[i] = Color.TRANSPARENT
                    }
                }
                result.setPixels(pixels, 0, w, 0, 0, w, h)
                Resource.Success(result)
            } catch (t: Throwable) {
                Resource.Error(com.umeetech.photofixai.core.result.AppError.BackgroundRemovalFailed)
            }
        }

    private fun estimateBackgroundColor(pixels: IntArray, w: Int, h: Int): Int {
        val corners = intArrayOf(
            pixels[0],
            pixels[w - 1],
            pixels[(h - 1) * w],
            pixels[h * w - 1]
        )
        val r = corners.sumOf { Color.red(it) } / corners.size
        val g = corners.sumOf { Color.green(it) } / corners.size
        val b = corners.sumOf { Color.blue(it) } / corners.size
        return Color.rgb(r, g, b)
    }

    private fun colorDistance(a: Int, b: Int): Int {
        val dr = Color.red(a) - Color.red(b)
        val dg = Color.green(a) - Color.green(b)
        val db = Color.blue(a) - Color.blue(b)
        return kotlin.math.sqrt((dr * dr + dg * dg + db * db).toDouble()).toInt()
    }
}
