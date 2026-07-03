package com.umeetech.photofixai.fakes

import com.umeetech.photofixai.domain.model.HistoryItem
import com.umeetech.photofixai.domain.repository.HistoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

/** In-memory fake used by use-case / repository unit tests. */
class FakeHistoryRepository : HistoryRepository {
    private val items = MutableStateFlow<List<HistoryItem>>(emptyList())
    private var nextId = 1L

    override fun observeHistory() = items.map { list -> list.sortedByDescending { it.createdAt } }

    override suspend fun add(item: HistoryItem): Long {
        val id = nextId++
        items.value = items.value + item.copy(id = id)
        return id
    }

    override suspend fun delete(item: HistoryItem) {
        items.value = items.value.filterNot { it.id == item.id }
    }

    override suspend fun clearAll() { items.value = emptyList() }

    override suspend fun getById(id: Long): HistoryItem? = items.value.firstOrNull { it.id == id }
}
