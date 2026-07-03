package com.umeetech.photofixai.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Request/response DTOs for the production background-removal backend.
 *
 * The Android app talks to YOUR backend / Firebase Function — never directly to a
 * paid provider (remove.bg, PhotoRoom, Replicate, OpenAI, ...). The backend holds
 * the secret keys and forwards the request. Image is sent base64-encoded here for
 * simplicity; switch to multipart upload for large files if preferred.
 */
data class BackgroundRemovalRequestDto(
    @SerializedName("image_base64") val imageBase64: String,
    @SerializedName("output_format") val outputFormat: String = "png",
    // Optional hints your backend may forward to the provider.
    @SerializedName("size") val size: String = "auto"
)

data class BackgroundRemovalResponseDto(
    @SerializedName("success") val success: Boolean,
    @SerializedName("image_base64") val imageBase64: String?,
    @SerializedName("error") val error: String?
)
