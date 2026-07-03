package com.umeetech.photofixai.core.image

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import androidx.core.graphics.scale
import kotlin.math.ceil
import kotlin.math.sqrt

/**
 * Lays out multiple copies of a single passport photo onto a standard printable
 * 4x6 inch sheet at 300 DPI, ready to be printed at a photo lab.
 */
object PassportSheetGenerator {

    private const val DPI = 300
    private const val SHEET_WIDTH_IN = 6f
    private const val SHEET_HEIGHT_IN = 4f
    private val MARGIN_PX = (0.15f * DPI).toInt()
    private val GUTTER_PX = (0.1f * DPI).toInt()

    private val sheetWidthPx = (SHEET_WIDTH_IN * DPI).toInt()
    private val sheetHeightPx = (SHEET_HEIGHT_IN * DPI).toInt()

    /**
     * @param photo the single, already-cropped passport photo (correct aspect ratio).
     * @param copies number of copies to tile (e.g. 4, 6, 8).
     */
    fun generateSheet(photo: Bitmap, copies: Int): Bitmap {
        val sheet = Bitmap.createBitmap(sheetWidthPx, sheetHeightPx, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(sheet)
        canvas.drawColor(Color.WHITE)

        // Choose a column/row grid that fits [copies] cells.
        val cols = ceil(sqrt(copies.toDouble())).toInt().coerceAtLeast(1)
        val rows = ceil(copies / cols.toDouble()).toInt().coerceAtLeast(1)

        val usableW = sheetWidthPx - MARGIN_PX * 2 - GUTTER_PX * (cols - 1)
        val usableH = sheetHeightPx - MARGIN_PX * 2 - GUTTER_PX * (rows - 1)
        val cellW = usableW / cols
        val cellH = usableH / rows

        // Fit each photo inside a cell while preserving its aspect ratio.
        val photoRatio = photo.width.toFloat() / photo.height
        var drawW = cellW
        var drawH = (drawW / photoRatio).toInt()
        if (drawH > cellH) {
            drawH = cellH
            drawW = (drawH * photoRatio).toInt()
        }
        val scaled = photo.scale(drawW.coerceAtLeast(1), drawH.coerceAtLeast(1))

        val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = 1f
            color = Color.LTGRAY
        }

        var drawn = 0
        for (r in 0 until rows) {
            for (c in 0 until cols) {
                if (drawn >= copies) break
                val cellLeft = MARGIN_PX + c * (cellW + GUTTER_PX)
                val cellTop = MARGIN_PX + r * (cellH + GUTTER_PX)
                val x = cellLeft + (cellW - drawW) / 2f
                val y = cellTop + (cellH - drawH) / 2f
                canvas.drawBitmap(scaled, x, y, null)
                canvas.drawRect(x, y, x + drawW, y + drawH, borderPaint)
                drawn++
            }
        }
        return sheet
    }
}
