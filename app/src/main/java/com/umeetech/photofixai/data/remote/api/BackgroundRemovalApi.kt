package com.umeetech.photofixai.data.remote.api

import com.umeetech.photofixai.data.remote.dto.BackgroundRemovalRequestDto
import com.umeetech.photofixai.data.remote.dto.BackgroundRemovalResponseDto
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

/**
 * Retrofit contract for the production background-removal endpoint.
 *
 * SECURITY: There are NO API keys here. Authentication (if any) uses a short-lived
 * token issued by your own backend (e.g. a Firebase App Check / Auth token), passed
 * as the [authToken] header. Paid provider secrets stay on the server.
 */
interface BackgroundRemovalApi {

    // TODO(production): point this at your real backend/Firebase Function route.
    @POST("removeBackground")
    suspend fun removeBackground(
        @Body request: BackgroundRemovalRequestDto,
        @Header("Authorization") authToken: String? = null
    ): BackgroundRemovalResponseDto
}
