package com.umeetech.photofixai.data.repository

import android.graphics.Bitmap
import com.umeetech.photofixai.core.result.Resource
import com.umeetech.photofixai.data.service.backgroundremoval.BackgroundRemovalService
import com.umeetech.photofixai.domain.repository.BackgroundRemovalRepository

/**
 * Delegates to whichever [BackgroundRemovalService] strategy the DI graph provides.
 * Swap the injected service (mock / local / api) in one place — see AppContainer.
 */
class BackgroundRemovalRepositoryImpl(
    private val service: BackgroundRemovalService
) : BackgroundRemovalRepository {

    override suspend fun removeBackground(source: Bitmap): Resource<Bitmap> =
        service.removeBackground(source)
}
