package com.umeetech.photofixai.usecase

import com.umeetech.photofixai.domain.model.HistoryItem
import com.umeetech.photofixai.domain.model.ToolType
import com.umeetech.photofixai.domain.usecase.ClearHistoryUseCase
import com.umeetech.photofixai.domain.usecase.DeleteHistoryItemUseCase
import com.umeetech.photofixai.domain.usecase.ObserveHistoryUseCase
import com.umeetech.photofixai.domain.usecase.SaveToHistoryUseCase
import com.umeetech.photofixai.fakes.FakeHistoryRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class HistoryUseCasesTest {

    private fun sampleItem(name: Long) = HistoryItem(
        thumbnailPath = "t", filePath = "f", toolType = ToolType.RESIZE,
        createdAt = name, fileSizeBytes = 1000, outputFormat = "PNG", width = 10, height = 10
    )

    @Test
    fun `save then observe returns item`() = runTest {
        val repo = FakeHistoryRepository()
        SaveToHistoryUseCase(repo)(sampleItem(1))
        val items = ObserveHistoryUseCase(repo)().first()
        assertEquals(1, items.size)
    }

    @Test
    fun `observe orders newest first`() = runTest {
        val repo = FakeHistoryRepository()
        val save = SaveToHistoryUseCase(repo)
        save(sampleItem(100))
        save(sampleItem(300))
        save(sampleItem(200))
        val items = ObserveHistoryUseCase(repo)().first()
        assertEquals(300, items.first().createdAt)
    }

    @Test
    fun `clear removes everything`() = runTest {
        val repo = FakeHistoryRepository()
        SaveToHistoryUseCase(repo)(sampleItem(1))
        ClearHistoryUseCase(repo)()
        assertTrue(ObserveHistoryUseCase(repo)().first().isEmpty())
    }

    @Test
    fun `delete removes single item`() = runTest {
        val repo = FakeHistoryRepository()
        val save = SaveToHistoryUseCase(repo)
        val id = save(sampleItem(1))
        DeleteHistoryItemUseCase(repo)(sampleItem(1).copy(id = id))
        assertTrue(ObserveHistoryUseCase(repo)().first().isEmpty())
    }
}
