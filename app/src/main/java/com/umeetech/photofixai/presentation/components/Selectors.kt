package com.umeetech.photofixai.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Colorize
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.umeetech.photofixai.core.export.ExportFormat
import com.umeetech.photofixai.domain.model.BackgroundOption
import com.umeetech.photofixai.presentation.theme.Dimens

/** Horizontal row of background color swatches (incl. transparent + custom). */
@Composable
fun ColorPickerRow(
    options: List<BackgroundOption>,
    selectedId: String,
    onSelect: (BackgroundOption) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(Dimens.SpaceM),
        verticalAlignment = Alignment.CenterVertically
    ) {
        options.forEach { option ->
            val selected = option.id == selectedId
            Box(
                Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(swatchColor(option))
                    .border(
                        BorderStroke(
                            if (selected) 3.dp else 1.dp,
                            if (selected) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.outline
                        ),
                        CircleShape
                    )
                    .clickable { onSelect(option) },
                contentAlignment = Alignment.Center
            ) {
                when {
                    option.isCustom -> Icon(
                        Icons.Filled.Colorize,
                        contentDescription = option.label,
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                    selected -> Icon(
                        Icons.Filled.Check,
                        contentDescription = "Selected",
                        tint = contentColorFor(option)
                    )
                }
            }
        }
    }
}

private fun swatchColor(option: BackgroundOption): Color = when {
    option.isTransparent -> Color(0xFFE2E8F0)
    option.colorArgb != null -> Color(option.colorArgb)
    else -> Color(0xFFF1F5F9)
}

private fun contentColorFor(option: BackgroundOption): Color {
    val c = swatchColor(option)
    val luminance = 0.299f * c.red + 0.587f * c.green + 0.114f * c.blue
    return if (luminance > 0.6f) Color.Black else Color.White
}

/** Segmented selector for output format (PNG / JPG / WebP). */
@Composable
fun FormatSelector(
    selected: ExportFormat,
    onSelect: (ExportFormat) -> Unit,
    modifier: Modifier = Modifier,
    options: List<ExportFormat> = ExportFormat.entries
) {
    SingleChoiceSegmentedButtonRow(modifier = modifier) {
        options.forEachIndexed { index, format ->
            SegmentedButton(
                selected = selected == format,
                onClick = { onSelect(format) },
                shape = SegmentedButtonDefaults.itemShape(index, options.size)
            ) {
                Text(format.label)
            }
        }
    }
}
