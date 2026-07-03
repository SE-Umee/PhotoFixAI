package com.umeetech.photofixai.domain.repository

import com.umeetech.photofixai.domain.model.HistoryItem
import kotlinx.coroutines.flow.Flow

interface HistoryRepository {
    fun observeHistory(): Flow<List<HistoryItem>>
    suspend fun add(item: HistoryItem): Long
    suspend fun delete(item: HistoryItem)
    suspend fun clearAll()
    suspend fun getById(id: Long): HistoryItem?
}
