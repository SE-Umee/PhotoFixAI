package com.umeetech.photofixai.presentation.screens.backgroundremover

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.Compare
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Rotate90DegreesCcw
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.umeetech.photofixai.di.rememberScopedViewModel
import com.umeetech.photofixai.domain.model.BackgroundOption
import com.umeetech.photofixai.presentation.common.ToolStatus
import com.umeetech.photofixai.presentation.components.AppTopBar
import com.umeetech.photofixai.presentation.components.ColorPickerDialog
import com.umeetech.photofixai.presentation.components.ColorPickerRow
import com.umeetech.photofixai.presentation.components.EmptyStateView
import com.umeetech.photofixai.presentation.components.ErrorDialog
import com.umeetech.photofixai.presentation.components.ExportBottomSheet
import com.umeetech.photofixai.presentation.components.ImagePreviewCard
import com.umeetech.photofixai.presentation.components.LoadingDialog
import com.umeetech.photofixai.presentation.components.PrimaryButtonFullWidth
import com.umeetech.photofixai.presentation.components.SecondaryButton
import com.umeetech.photofixai.presentation.components.SectionHeader
import com.umeetech.photofixai.presentation.components.SuccessDialog
import com.umeetech.photofixai.presentation.components.rememberImagePicker
import com.umeetech.photofixai.presentation.theme.Dimens

@Composable
fun BackgroundRemoverScreen(onBack: () -> Unit) {
    val viewModel: BackgroundRemoverViewModel = rememberScopedViewModel { BackgroundRemoverViewModel(it) }
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val pickImage = rememberImagePicker { uri -> viewModel.onImagePicked(context, uri) }
    var showExportSheet by remember { mutableStateOf(false) }
    var showColorPicker by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize()) {
        AppTopBar(title = "Background Remover", onBack = onBack)

        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Dimens.ScreenPadding)
        ) {
            if (!state.hasImage) {
                EmptyStateView(
                    title = "Select an image",
                    subtitle = "Pick a photo and we'll remove the background automatically.",
                    icon = Icons.Filled.PhotoLibrary,
                    action = {
                        PrimaryButtonFullWidth(
                            text = "Select Image",
                            onClick = pickImage,
                            icon = Icons.Filled.PhotoLibrary
                        )
                    }
                )
            } else {
                val previewModel = if (state.showBefore) state.originalBitmap else state.previewBitmap
                ImagePreviewCard(
                    model = previewModel,
                    checkeredBackground = state.selectedBackgroundId == BackgroundOption.Transparent.id && state.hasCutout,
                    aspectRatio = 1f
                )

                state.meta?.let { meta ->
                    Spacer(Modifier.height(Dimens.SpaceS))
                    Text(
                        "${meta.displayName} · ${meta.width}×${meta.height}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(Modifier.height(Dimens.SpaceL))

                if (!state.hasCutout) {
                    PrimaryButtonFullWidth(
                        text = "Remove Background",
                        onClick = viewModel::removeBackground,
                        icon = Icons.Filled.AutoFixHigh,
                        loading = state.status is ToolStatus.Processing
                    )
                    Spacer(Modifier.height(Dimens.SpaceS))
                    SecondaryButton(text = "Choose different image", onClick = pickImage, modifier = Modifier.fillMaxWidth())
                } else {
                    EditorControls(
                        state = state,
                        onSelectBackground = { option ->
                            if (option.isCustom) showColorPicker = true
                            else viewModel.selectBackground(option)
                        },
                        onToggleBefore = viewModel::toggleBeforeAfter,
                        onRotate = viewModel::rotate,
                        onReset = viewModel::reset,
                        onExport = { showExportSheet = true }
                    )
                }
            }
            Spacer(Modifier.height(Dimens.Space3XL))
        }
    }

    // Loading / error overlays.
    if (state.status is ToolStatus.Processing && state.hasImage && !state.hasCutout) {
        LoadingDialog("Removing background…")
    }
    (state.status as? ToolStatus.Error)?.let { err ->
        ErrorDialog(
            message = err.error.message,
            onDismiss = viewModel::dismissError,
            onRetry = if (state.hasImage && !state.hasCutout) viewModel::removeBackground else null
        )
    }

    if (showColorPicker) {
        ColorPickerDialog(
            initialColor = state.customColor,
            onDismiss = { showColorPicker = false },
            onColorSelected = { color ->
                viewModel.selectBackground(BackgroundOption.Custom, color)
                showColorPicker = false
            }
        )
    }

    if (showExportSheet) {
        ExportBottomSheet(
            selectedFormat = state.exportFormat,
            onFormatChange = viewModel::setFormat,
            onSave = {
                showExportSheet = false
                viewModel.export(context) {}
            },
            onShare = {
                showExportSheet = false
                viewModel.buildShareIntent(context)?.let {
                    context.startActivity(Intent.createChooser(it, "Share image"))
                }
            },
            onHdExport = {
                showExportSheet = false
                // HD export is gated for premium; free users can be offered a rewarded ad.
                viewModel.export(context) {}
            },
            isPremium = state.isPremium,
            onDismiss = { showExportSheet = false }
        )
    }

    if (state.status is ToolStatus.ExportSuccess) {
        val export = state.lastExport
        SuccessDialog(
            title = "Saved!",
            message = "Your image was saved to ${export?.displayPath ?: "your gallery"}.",
            confirmText = "Done",
            onConfirm = viewModel::reset,
            secondaryText = "Share",
            onSecondary = {
                viewModel.buildShareIntent(context)?.let {
                    context.startActivity(Intent.createChooser(it, "Share image"))
                }
            }
        )
    }
}

@Composable
private fun EditorControls(
    state: BgRemoverUiState,
    onSelectBackground: (BackgroundOption) -> Unit,
    onToggleBefore: (Boolean) -> Unit,
    onRotate: () -> Unit,
    onReset: () -> Unit,
    onExport: () -> Unit
) {
    SectionHeader(title = "Background")
    Spacer(Modifier.height(Dimens.SpaceM))
    ColorPickerRow(
        options = BackgroundOption.defaults,
        selectedId = state.selectedBackgroundId,
        onSelect = onSelectBackground
    )

    Spacer(Modifier.height(Dimens.SpaceL))
    Row(horizontalArrangement = Arrangement.spacedBy(Dimens.SpaceS), verticalAlignment = Alignment.CenterVertically) {
        FilterChip(
            selected = state.showBefore,
            onClick = { onToggleBefore(!state.showBefore) },
            label = { Text("Compare") },
            leadingIcon = { Icon(Icons.Filled.Compare, null, modifier = Modifier.height(18.dp)) }
        )
        FilterChip(
            selected = false,
            onClick = onRotate,
            label = { Text("Rotate") },
            leadingIcon = { Icon(Icons.Filled.Rotate90DegreesCcw, null, modifier = Modifier.height(18.dp)) }
        )
        FilterChip(
            selected = false,
            onClick = onReset,
            label = { Text("Reset") },
            leadingIcon = { Icon(Icons.Filled.Restore, null, modifier = Modifier.height(18.dp)) }
        )
    }

    Spacer(Modifier.height(Dimens.SpaceXL))
    PrimaryButtonFullWidth(text = "Export", onClick = onExport)
}
