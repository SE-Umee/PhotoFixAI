package com.umeetech.photofixai.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val palette = listOf(
    0xFFFFFFFF, 0xFF000000, 0xFF2563EB, 0xFF1D4ED8, 0xFF10B981,
    0xFFEF4444, 0xFFF59E0B, 0xFF8B5CF6, 0xFFEC4899, 0xFF14B8A6,
    0xFF0EA5E9, 0xFF64748B, 0xFFF1F5F9, 0xFFFDE68A, 0xFFBFDBFE
)

/** Simple palette-based custom color picker. */
@Composable
fun ColorPickerDialog(
    initialColor: Long = 0xFFFFFFFF,
    onDismiss: () -> Unit,
    onColorSelected: (Long) -> Unit
) {
    var current by remember { mutableStateOf(initialColor) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Choose a background color") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                palette.chunked(5).forEach { rowColors ->
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        rowColors.forEach { argb ->
                            val selected = argb == current
                            Box(
                                Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(Color(argb))
                                    .border(
                                        width = if (selected) 3.dp else 1.dp,
                                        color = if (selected) MaterialTheme.colorScheme.primary
                                        else MaterialTheme.colorScheme.outline,
                                        shape = CircleShape
                                    )
                                    .clickable { current = argb }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = { onColorSelected(current) }) { Text("Apply") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
