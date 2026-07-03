package com.umeetech.photofixai.data.repository

import com.umeetech.photofixai.data.local.dao.HistoryDao
import com.umeetech.photofixai.data.local.entity.HistoryEntity
import com.umeetech.photofixai.domain.model.HistoryItem
import com.umeetech.photofixai.domain.repository.HistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class HistoryRepositoryImpl(private val dao: HistoryDao) : HistoryRepository {

    override fun observeHistory(): Flow<List<HistoryItem>> =
        dao.observeAll().map { list -> list.map { it.toDomain() } }

    override suspend fun add(item: HistoryItem): Long =
        dao.insert(HistoryEntity.fromDomain(item))

    override suspend fun delete(item: HistoryItem) =
        dao.delete(HistoryEntity.fromDomain(item))

    override suspend fun clearAll() = dao.clearAll()

    override suspend fun getById(id: Long): HistoryItem? =
        dao.getById(id)?.toDomain()
}
