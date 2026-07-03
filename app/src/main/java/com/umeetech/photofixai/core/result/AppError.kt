package com.umeetech.photofixai.core.result

/**
 * Domain-level error taxonomy. Every failure surfaced to the UI maps to one of
 * these, so screens can show consistent, user-friendly messages + retry options.
 */
sealed class AppError(open val message: String) {
    data object NoImageSelected : AppError("Please select an image to continue.")
    data object UnsupportedFormat : AppError("This image format isn't supported. Try a JPG, PNG or WebP.")
    data object ImageTooLarge : AppError("This image is too large to process on this device.")
    data object BackgroundRemovalFailed : AppError("We couldn't remove the background. Please try again.")
    data object NoInternet : AppError("No internet connection. Connect and retry, or use offline mode.")
    data object SaveFailed : AppError("Saving the image failed. Please try again.")
    data object PermissionDenied : AppError("Permission is required to complete this action.")
    data object CompressionFailed : AppError("Compression failed. Try a different target size.")
    data object ExportFailed : AppError("Export failed. Please try again.")
    data class Unknown(override val message: String = "Something went wrong. Please try again.") : AppError(message)
}
