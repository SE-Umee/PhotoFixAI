package com.umeetech.photofixai.domain.usecase

import com.umeetech.photofixai.domain.model.HistoryItem
import com.umeetech.photofixai.domain.repository.HistoryRepository
import kotlinx.coroutines.flow.Flow

class ObserveHistoryUseCase(private val repository: HistoryRepository) {
    operator fun invoke(): Flow<List<HistoryItem>> = repository.observeHistory()
}

class SaveToHistoryUseCase(private val repository: HistoryRepository) {
    suspend operator fun invoke(item: HistoryItem): Long = repository.add(item)
}

class DeleteHistoryItemUseCase(private val repository: HistoryRepository) {
    suspend operator fun invoke(item: HistoryItem) = repository.delete(item)
}

class ClearHistoryUseCase(private val repository: HistoryRepository) {
    suspend operator fun invoke() = repository.clearAll()
}
