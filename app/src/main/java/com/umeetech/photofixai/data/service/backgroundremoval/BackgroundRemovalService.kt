package com.umeetech.photofixai.data.service.backgroundremoval

import android.graphics.Bitmap
import com.umeetech.photofixai.core.result.Resource

/**
 * Strategy interface for background removal. Swappable implementations let us ship
 * an MVP today and plug in production APIs tomorrow without touching the UI:
 *
 *  - [MockBackgroundRemovalService]                — instant, deterministic (MVP/tests)
 *  - [LocalSegmentationBackgroundRemovalService]   — free, on-device (ML Kit)
 *  - [ApiBackgroundRemovalService]                 — production, via YOUR backend
 */
interface BackgroundRemovalService {
    suspend fun removeBackground(source: Bitmap): Resource<Bitmap>
}
