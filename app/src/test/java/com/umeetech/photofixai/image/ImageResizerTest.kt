package com.umeetech.photofixai.image

import com.umeetech.photofixai.core.image.ImageResizer
import org.junit.Assert.assertEquals
import org.junit.Test

/** Pure-logic tests for aspect-ratio calculations (no Android runtime needed). */
class ImageResizerTest {

    @Test
    fun `heightForWidth preserves aspect ratio`() {
        // 1000x500 (2:1) resized to width 400 -> height 200
        assertEquals(200, ImageResizer.heightForWidth(400, 1000, 500))
    }

    @Test
    fun `widthForHeight preserves aspect ratio`() {
        assertEquals(400, ImageResizer.widthForHeight(200, 1000, 500))
    }

    @Test
    fun `heightForWidth never returns zero`() {
        assertEquals(1, ImageResizer.heightForWidth(1, 1000, 1))
    }
}
