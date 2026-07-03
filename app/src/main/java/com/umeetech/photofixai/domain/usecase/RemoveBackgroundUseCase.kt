package com.umeetech.photofixai.domain.usecase

import android.graphics.Bitmap
import com.umeetech.photofixai.core.result.Resource
import com.umeetech.photofixai.domain.repository.BackgroundRemovalRepository

/** Removes the background from [source]. Thin wrapper keeping ViewModels clean. */
class RemoveBackgroundUseCase(
    private val repository: BackgroundRemovalRepository
) {
    suspend operator fun invoke(source: Bitmap): Resource<Bitmap> =
        repository.removeBackground(source)
}
