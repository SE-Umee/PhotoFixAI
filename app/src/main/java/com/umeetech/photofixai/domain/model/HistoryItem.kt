package com.umeetech.photofixai.domain.model

/** A single completed export shown in the History screen. */
data class HistoryItem(
    val id: Long = 0,
    val thumbnailPath: String,
    val filePath: String,
    val toolType: ToolType,
    val createdAt: Long,
    val fileSizeBytes: Long,
    val outputFormat: String,
    val width: Int,
    val height: Int
)
