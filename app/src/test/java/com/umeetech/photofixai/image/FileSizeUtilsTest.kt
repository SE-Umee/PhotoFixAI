package com.umeetech.photofixai.image

import com.umeetech.photofixai.core.image.FileSizeUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FileSizeUtilsTest {

    @Test
    fun `kb to bytes and back`() {
        assertEquals(100 * 1024L, FileSizeUtils.kbToBytes(100))
        assertEquals(100, FileSizeUtils.bytesToKb(100 * 1024L))
    }

    @Test
    fun `formatBytes renders KB and MB`() {
        assertTrue(FileSizeUtils.formatBytes(500L * 1024).endsWith("KB"))
        assertTrue(FileSizeUtils.formatBytes(5L * 1024 * 1024).endsWith("MB"))
    }

    @Test
    fun `zero bytes is safe`() {
        assertEquals("0 KB", FileSizeUtils.formatBytes(0))
    }
}
