package com.umeetech.photofixai.core.export

import android.net.Uri

/** Outcome of a successful export/save operation. */
data class ExportResult(
    val uri: Uri,
    val displayPath: String,
    val format: ExportFormat,
    val sizeBytes: Long,
    val width: Int,
    val height: Int,
    val savedToGallery: Boolean
)
