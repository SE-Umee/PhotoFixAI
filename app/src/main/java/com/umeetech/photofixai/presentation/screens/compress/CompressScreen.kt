package com.umeetech.photofixai.presentation.screens.compress

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Compress
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.umeetech.photofixai.core.image.FileSizeUtils
import com.umeetech.photofixai.di.rememberScopedViewModel
import com.umeetech.photofixai.presentation.common.ToolStatus
import com.umeetech.photofixai.presentation.components.AppTopBar
import com.umeetech.photofixai.presentation.components.EmptyStateView
import com.umeetech.photofixai.presentation.components.ErrorDialog
import com.umeetech.photofixai.presentation.components.FormatSelector
import com.umeetech.photofixai.presentation.components.ImagePreviewCard
import com.umeetech.photofixai.presentation.components.PrimaryButtonFullWidth
import com.umeetech.photofixai.presentation.components.SecondaryButton
import com.umeetech.photofixai.presentation.components.SectionHeader
import com.umeetech.photofixai.presentation.components.SuccessDialog
import com.umeetech.photofixai.presentation.components.rememberImagePicker
import com.umeetech.photofixai.presentation.theme.Dimens

@Composable
fun CompressScreen(onBack: () -> Unit) {
    val viewModel: CompressViewModel = rememberScopedViewModel { CompressViewModel(it) }
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val pickImage = rememberImagePicker { uri -> viewModel.onImagePicked(context, uri) }

    Column(Modifier.fillMaxSize()) {
        AppTopBar(title = "Compress Image", onBack = onBack)
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Dimens.ScreenPadding)
        ) {
            if (!state.hasImage) {
                EmptyStateView(
                    title = "Select an image",
                    subtitle = "Shrink file size while keeping great quality.",
                    icon = Icons.Filled.PhotoLibrary,
                    action = { PrimaryButtonFullWidth("Select Image", pickImage, icon = Icons.Filled.PhotoLibrary) }
                )
            } else {
                ImagePreviewCard(model = state.original, aspectRatio = 1f)
                Spacer(Modifier.height(Dimens.SpaceS))
                Text(
                    "Original size: ${FileSizeUtils.formatBytes(state.originalSizeBytes)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(Modifier.height(Dimens.SpaceL))
                SectionHeader(title = "Target size")
                Spacer(Modifier.height(Dimens.SpaceM))
                Row(
                    Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(Dimens.SpaceS)
                ) {
                    CompressTarget.entries.forEach { target ->
                        FilterChip(
                            selected = state.target == target,
                            onClick = { viewModel.setTarget(target) },
                            label = { Text(target.label) }
                        )
                    }
                }
                if (state.target == CompressTarget.CUSTOM) {
                    Spacer(Modifier.height(Dimens.SpaceM))
                    OutlinedTextField(
                        value = state.customKb.toString(),
                        onValueChange = { it.toIntOrNull()?.let(viewModel::setCustomKb) },
                        label = { Text("Custom size (KB)") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(Modifier.height(Dimens.SpaceM))
                Text("Quality: ${state.quality}%", style = MaterialTheme.typography.bodyMedium)
                Slider(
                    value = state.quality.toFloat(),
                    onValueChange = { viewModel.setQuality(it.toInt()) },
                    valueRange = 10f..100f
                )

                Spacer(Modifier.height(Dimens.SpaceS))
                SectionHeader(title = "Format")
                Spacer(Modifier.height(Dimens.SpaceM))
                FormatSelector(
                    selected = state.format,
                    onSelect = viewModel::setFormat,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(Dimens.SpaceL))
                PrimaryButtonFullWidth(
                    text = "Compress",
                    onClick = viewModel::compress,
                    icon = Icons.Filled.Compress,
                    loading = state.status is ToolStatus.Processing
                )

                if (state.compressedBytes > 0) {
                    Spacer(Modifier.height(Dimens.SpaceL))
                    Text(
                        "Compressed: ${FileSizeUtils.formatBytes(state.compressedBytes)}  ${state.savingText}",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(Modifier.height(Dimens.SpaceM))
                    PrimaryButtonFullWidth("Save", { viewModel.export(context) {} })
                    Spacer(Modifier.height(Dimens.SpaceS))
                    SecondaryButton(
                        text = "Share",
                        onClick = {
                            viewModel.buildShareIntent(context)?.let {
                                context.startActivity(Intent.createChooser(it, "Share image"))
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            Spacer(Modifier.height(Dimens.Space3XL))
        }
    }

    (state.status as? ToolStatus.Error)?.let { err ->
        ErrorDialog(message = err.error.message, onDismiss = viewModel::dismissError)
    }
    if (state.status is ToolStatus.ExportSuccess) {
        SuccessDialog(title = "Saved!", message = "Compressed image saved to your gallery.", onConfirm = viewModel::consumeExportSuccess)
    }
}
