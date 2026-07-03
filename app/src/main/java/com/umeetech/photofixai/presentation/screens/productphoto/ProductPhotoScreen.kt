package com.umeetech.photofixai.presentation.screens.productphoto

import android.content.Intent
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.umeetech.photofixai.di.rememberScopedViewModel
import com.umeetech.photofixai.domain.model.BackgroundOption
import com.umeetech.photofixai.presentation.common.ToolStatus
import com.umeetech.photofixai.presentation.components.AppTopBar
import com.umeetech.photofixai.presentation.components.ColorPickerDialog
import com.umeetech.photofixai.presentation.components.ColorPickerRow
import com.umeetech.photofixai.presentation.components.EmptyStateView
import com.umeetech.photofixai.presentation.components.ErrorDialog
import com.umeetech.photofixai.presentation.components.ImagePreviewCard
import com.umeetech.photofixai.presentation.components.LoadingDialog
import com.umeetech.photofixai.presentation.components.PrimaryButtonFullWidth
import com.umeetech.photofixai.presentation.components.SecondaryButton
import com.umeetech.photofixai.presentation.components.SectionHeader
import com.umeetech.photofixai.presentation.components.SuccessDialog
import com.umeetech.photofixai.presentation.components.rememberImagePicker
import com.umeetech.photofixai.presentation.theme.Dimens

@Composable
fun ProductPhotoScreen(onBack: () -> Unit) {
    val viewModel: ProductPhotoViewModel = rememberScopedViewModel { ProductPhotoViewModel(it) }
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val pickImage = rememberImagePicker { uri -> viewModel.onImagePicked(context, uri) }
    var showColorPicker by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize()) {
        AppTopBar(title = "Product Photo Maker", onBack = onBack)
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Dimens.ScreenPadding)
        ) {
            if (!state.hasImage) {
                EmptyStateView(
                    title = "Select a product photo",
                    subtitle = "Create clean, marketplace-ready product shots.",
                    icon = Icons.Filled.PhotoLibrary,
                    action = { PrimaryButtonFullWidth("Select Image", pickImage, icon = Icons.Filled.PhotoLibrary) }
                )
            } else {
                ImagePreviewCard(model = state.preview, aspectRatio = 1f)

                Spacer(Modifier.height(Dimens.SpaceL))
                SecondaryButton(
                    text = "Remove background",
                    onClick = viewModel::removeBackground,
                    icon = Icons.Filled.AutoFixHigh,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(Dimens.SpaceL))
                SectionHeader(title = "Background")
                Spacer(Modifier.height(Dimens.SpaceM))
                ColorPickerRow(
                    options = BackgroundOption.solids,
                    selectedId = state.backgroundId,
                    onSelect = { if (it.isCustom) showColorPicker = true else viewModel.selectBackground(it) }
                )

                Spacer(Modifier.height(Dimens.SpaceM))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Switch(checked = state.softShadow, onCheckedChange = viewModel::toggleShadow)
                    Text("  Soft shadow", style = MaterialTheme.typography.bodyMedium)
                }

                Spacer(Modifier.height(Dimens.SpaceM))
                SectionHeader(title = "Marketplace preset")
                Spacer(Modifier.height(Dimens.SpaceM))
                Row(
                    Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(Dimens.SpaceS)
                ) {
                    viewModel.presets.forEach { preset ->
                        FilterChip(
                            selected = state.preset.id == preset.id,
                            onClick = { viewModel.selectPreset(preset) },
                            label = { Text(preset.name) }
                        )
                    }
                }
                Spacer(Modifier.height(Dimens.SpaceS))
                Text(
                    state.preset.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(Modifier.height(Dimens.SpaceXL))
                PrimaryButtonFullWidth("Save", { viewModel.export(context) {} })
                Spacer(Modifier.height(Dimens.SpaceS))
                SecondaryButton(
                    text = "Share",
                    onClick = {
                        viewModel.buildShareIntent(context)?.let {
                            context.startActivity(Intent.createChooser(it, "Share product photo"))
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Spacer(Modifier.height(Dimens.Space3XL))
        }
    }

    if (state.status is ToolStatus.Processing && state.hasImage) LoadingDialog("Processing…")
    (state.status as? ToolStatus.Error)?.let { err ->
        ErrorDialog(message = err.error.message, onDismiss = viewModel::dismissError)
    }
    if (showColorPicker) {
        ColorPickerDialog(
            initialColor = state.customColor,
            onDismiss = { showColorPicker = false },
            onColorSelected = { color -> viewModel.selectBackground(BackgroundOption.Custom, color); showColorPicker = false }
        )
    }
    if (state.status is ToolStatus.ExportSuccess) {
        SuccessDialog(title = "Saved!", message = "Product photo saved to your gallery.", onConfirm = viewModel::consumeExportSuccess)
    }
}
