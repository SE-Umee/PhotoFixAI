package com.umeetech.photofixai.data.service.backgroundremoval

import android.graphics.Bitmap
import android.graphics.Color
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.segmentation.Segmentation
import com.google.mlkit.vision.segmentation.selfie.SelfieSegmenterOptions
import com.umeetech.photofixai.core.constants.Constants
import com.umeetech.photofixai.core.result.AppError
import com.umeetech.photofixai.core.result.Resource
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/**
 * Free, on-device background removal using ML Kit Selfie Segmentation. Great for
 * portraits/people and works fully offline — no API key, no cost.
 *
 * For non-people subjects (products, objects) prefer the production API service,
 * which can route to a general-purpose provider via your backend.
 */
class LocalSegmentationBackgroundRemovalService : BackgroundRemovalService {

    private val segmenter by lazy {
        val options = SelfieSegmenterOptions.Builder()
            .setDetectorMode(SelfieSegmenterOptions.SINGLE_IMAGE_MODE)
            .build()
        Segmentation.getClient(options)
    }

    override suspend fun removeBackground(source: Bitmap): Resource<Bitmap> =
        suspendCancellableCoroutine { cont ->
            try {
                val input = InputImage.fromBitmap(source, 0)
                segmenter.process(input)
                    .addOnSuccessListener { mask ->
                        try {
                            val output = applyMask(source, mask.buffer, mask.width, mask.height)
                            cont.resume(Resource.Success(output))
                        } catch (t: Throwable) {
                            cont.resume(Resource.Error(AppError.BackgroundRemovalFailed))
                        }
                    }
                    .addOnFailureListener {
                        cont.resume(Resource.Error(AppError.BackgroundRemovalFailed))
                    }
            } catch (t: Throwable) {
                cont.resume(Resource.Error(AppError.BackgroundRemovalFailed))
            }
        }

    private fun applyMask(
        source: Bitmap,
        maskBuffer: java.nio.ByteBuffer,
        maskWidth: Int,
        maskHeight: Int
    ): Bitmap {
        val result = source.copy(Bitmap.Config.ARGB_8888, true)
        val width = result.width
        val height = result.height
        val pixels = IntArray(width * height)
        result.getPixels(pixels, 0, width, 0, 0, width, height)

        maskBuffer.rewind()
        // Mask usually matches the input size; guard against mismatches by scaling.
        for (y in 0 until height) {
            for (x in 0 until width) {
                val mx = if (maskWidth == width) x else x * maskWidth / width
                val my = if (maskHeight == height) y else y * maskHeight / height
                val confidence = maskBuffer.getFloat((my * maskWidth + mx) * 4)
                if (confidence < Constants.SEGMENTATION_CONFIDENCE_THRESHOLD) {
                    pixels[y * width + x] = Color.TRANSPARENT
                }
            }
        }
        result.setPixels(pixels, 0, width, 0, 0, width, height)
        return result
    }
}
