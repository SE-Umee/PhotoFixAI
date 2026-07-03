package com.umeetech.photofixai.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.umeetech.photofixai.domain.model.HistoryItem
import com.umeetech.photofixai.domain.model.ToolType

@Entity(tableName = "history")
data class HistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val thumbnailPath: String,
    val filePath: String,
    val toolType: String,
    val createdAt: Long,
    val fileSizeBytes: Long,
    val outputFormat: String,
    val width: Int,
    val height: Int
) {
    fun toDomain(): HistoryItem = HistoryItem(
        id = id,
        thumbnailPath = thumbnailPath,
        filePath = filePath,
        toolType = runCatching { ToolType.valueOf(toolType) }.getOrDefault(ToolType.BACKGROUND_REMOVER),
        createdAt = createdAt,
        fileSizeBytes = fileSizeBytes,
        outputFormat = outputFormat,
        width = width,
        height = height
    )

    companion object {
        fun fromDomain(item: HistoryItem): HistoryEntity = HistoryEntity(
            id = item.id,
            thumbnailPath = item.thumbnailPath,
            filePath = item.filePath,
            toolType = item.toolType.name,
            createdAt = item.createdAt,
            fileSizeBytes = item.fileSizeBytes,
            outputFormat = item.outputFormat,
            width = item.width,
            height = item.height
        )
    }
}
