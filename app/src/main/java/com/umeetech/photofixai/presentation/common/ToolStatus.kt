package com.umeetech.photofixai.presentation.common

import com.umeetech.photofixai.core.result.AppError

/**
 * Generic processing status shared by the image tools. Bitmap/result data is held
 * separately in each feature's UI state; this models the lifecycle only.
 */
sealed interface ToolStatus {
    data object Idle : ToolStatus
    data object ImageSelected : ToolStatus
    data object Processing : ToolStatus
    data object Success : ToolStatus
    data object Exporting : ToolStatus
    data object ExportSuccess : ToolStatus
    data class Error(val error: AppError) : ToolStatus
}
