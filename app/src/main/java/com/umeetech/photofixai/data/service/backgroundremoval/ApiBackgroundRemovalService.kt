package com.umeetech.photofixai.data.service.backgroundremoval

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import com.umeetech.photofixai.core.result.AppError
import com.umeetech.photofixai.core.result.Resource
import com.umeetech.photofixai.data.remote.api.BackgroundRemovalApi
import com.umeetech.photofixai.data.remote.dto.BackgroundRemovalRequestDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

/**
 * PRODUCTION implementation. Sends the image to YOUR backend / Firebase Function,
 * which holds the paid provider's secret key (remove.bg, PhotoRoom, Replicate,
 * OpenAI, etc.) and returns the processed image.
 *
 * =============================================================================
 * SECURITY — READ BEFORE ENABLING:
 *  - There are NO API keys in this file or anywhere in the app.
 *  - Configure BACKEND_BASE_URL via Gradle (see app/build.gradle.kts), NOT here.
 *  - If your endpoint needs auth, pass a short-lived token (Firebase App Check /
 *    Auth) — never a provider secret.
 *  - Point BackgroundRemovalApi.removeBackground at your real route.
 * =============================================================================
 */
class ApiBackgroundRemovalService(
    private val api: BackgroundRemovalApi,
    private val authTokenProvider: suspend () -> String? = { null }
) : BackgroundRemovalService {

    override suspend fun removeBackground(source: Bitmap): Resource<Bitmap> =
        withContext(Dispatchers.IO) {
            try {
                val base64 = source.toBase64Png()
                val token = authTokenProvider()?.let { "Bearer $it" }
                val response = api.removeBackground(
                    request = BackgroundRemovalRequestDto(imageBase64 = base64),
                    authToken = token
                )
                if (response.success && response.imageBase64 != null) {
                    val bytes = Base64.decode(response.imageBase64, Base64.DEFAULT)
                    val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                        ?: return@withContext Resource.Error(AppError.BackgroundRemovalFailed)
                    Resource.Success(bitmap)
                } else {
                    Resource.Error(AppError.BackgroundRemovalFailed)
                }
            } catch (e: java.io.IOException) {
                Resource.Error(AppError.NoInternet)
            } catch (t: Throwable) {
                Resource.Error(AppError.BackgroundRemovalFailed)
            }
        }

    private fun Bitmap.toBase64Png(): String {
        val out = ByteArrayOutputStream()
        compress(Bitmap.CompressFormat.PNG, 100, out)
        return Base64.encodeToString(out.toByteArray(), Base64.NO_WRAP)
    }
}
