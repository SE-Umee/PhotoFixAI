package com.umeetech.photofixai.presentation.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Compress
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.Draw
import androidx.compose.material.icons.filled.FormatColorFill
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PhotoSizeSelectLarge
import androidx.compose.material.icons.filled.Portrait
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.umeetech.photofixai.domain.model.ToolType

/** Maps each [ToolType] to a Material icon and an accent color for its card. */
object ToolVisuals {

    fun icon(tool: ToolType): ImageVector = when (tool) {
        ToolType.BACKGROUND_REMOVER -> Icons.Filled.AutoFixHigh
        ToolType.PASSPORT_PHOTO -> Icons.Filled.Badge
        ToolType.RESIZE -> Icons.Filled.PhotoSizeSelectLarge
        ToolType.COMPRESS -> Icons.Filled.Compress
        ToolType.PRODUCT_PHOTO -> Icons.Filled.ShoppingBag
        ToolType.SIGNATURE -> Icons.Filled.Draw
        ToolType.PROFILE_PICTURE -> Icons.Filled.Portrait
        ToolType.DOCUMENT_RESIZE -> Icons.Filled.Image
        ToolType.TRANSPARENT_PNG -> Icons.Filled.ContentCut
        ToolType.CUSTOM_BACKGROUND -> Icons.Filled.FormatColorFill
    }

    fun accent(tool: ToolType): Color = when (tool) {
        ToolType.BACKGROUND_REMOVER -> Color(0xFF2563EB)
        ToolType.PASSPORT_PHOTO -> Color(0xFF7C3AED)
        ToolType.RESIZE -> Color(0xFF0EA5E9)
        ToolType.COMPRESS -> Color(0xFFF59E0B)
        ToolType.PRODUCT_PHOTO -> Color(0xFF10B981)
        ToolType.SIGNATURE -> Color(0xFFEC4899)
        ToolType.PROFILE_PICTURE -> Color(0xFF6366F1)
        ToolType.DOCUMENT_RESIZE -> Color(0xFF14B8A6)
        ToolType.TRANSPARENT_PNG -> Color(0xFF8B5CF6)
        ToolType.CUSTOM_BACKGROUND -> Color(0xFFEF4444)
    }
}
