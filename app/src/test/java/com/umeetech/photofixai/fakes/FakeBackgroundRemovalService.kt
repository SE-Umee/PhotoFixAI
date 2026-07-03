package com.umeetech.photofixai.fakes

import android.graphics.Bitmap
import com.umeetech.photofixai.core.result.AppError
import com.umeetech.photofixai.core.result.Resource
import com.umeetech.photofixai.data.service.backgroundremoval.BackgroundRemovalService

/**
 * Deterministic fake background remover for tests: echoes the input bitmap back,
 * or fails on demand to exercise error paths. No Android runtime needed at
 * construction time (only when [removeBackground] is actually invoked with a real
 * Bitmap on an instrumented/Robolectric test).
 */
class FakeBackgroundRemovalService(
    private val shouldFail: Boolean = false
) : BackgroundRemovalService {
    override suspend fun removeBackground(source: Bitmap): Resource<Bitmap> =
        if (shouldFail) Resource.Error(AppError.BackgroundRemovalFailed) else Resource.Success(source)
}
