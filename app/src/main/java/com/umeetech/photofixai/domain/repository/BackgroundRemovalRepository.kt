package com.umeetech.photofixai.domain.repository

import android.graphics.Bitmap
import com.umeetech.photofixai.core.result.Resource

/**
 * Abstraction over background removal. The concrete implementation delegates to a
 * swappable strategy (mock / on-device / production API) selected in the DI graph.
 */
interface BackgroundRemovalRepository {
    /** Returns a bitmap with the background removed (transparent). */
    suspend fun removeBackground(source: Bitmap): Resource<Bitmap>
}
