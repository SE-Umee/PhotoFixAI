package com.umeetech.photofixai.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HighQuality
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.umeetech.photofixai.core.export.ExportFormat
import com.umeetech.photofixai.presentation.theme.Dimens

/**
 * Reusable export sheet: pick a format, then Save to gallery or Share. Includes an
 * "HD export" entry that is gated for premium (rewarded-ad unlock hook lives in the
 * caller / AdsManager).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExportBottomSheet(
    selectedFormat: ExportFormat,
    onFormatChange: (ExportFormat) -> Unit,
    onSave: () -> Unit,
    onShare: () -> Unit,
    onHdExport: () -> Unit,
    isPremium: Boolean,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState) {
        Column(
            Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = Dimens.ScreenPadding)
                .padding(bottom = Dimens.SpaceXXL),
            verticalArrangement = Arrangement.spacedBy(Dimens.SpaceL)
        ) {
            Text("Export image", style = MaterialTheme.typography.titleLarge)

            Text("Format", style = MaterialTheme.typography.labelLarge)
            FormatSelector(selected = selectedFormat, onSelect = onFormatChange, modifier = Modifier.fillMaxWidth())

            ExportActionRow(Icons.Filled.Save, "Save to gallery", "Store in Pictures/PhotoFix AI", onSave)
            ExportActionRow(Icons.Filled.Share, "Share", "Send via other apps", onShare)
            ExportActionRow(
                Icons.Filled.HighQuality,
                if (isPremium) "HD export" else "HD export (Premium)",
                if (isPremium) "Full-resolution export" else "Watch an ad or go Premium",
                onHdExport
            )
        }
    }
}

@Composable
private fun ExportActionRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    androidx.compose.material3.Surface(
        onClick = onClick,
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            Modifier.padding(Dimens.CardPadding),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Dimens.SpaceL)
        ) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
            Column {
                Text(title, style = MaterialTheme.typography.titleSmall)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
