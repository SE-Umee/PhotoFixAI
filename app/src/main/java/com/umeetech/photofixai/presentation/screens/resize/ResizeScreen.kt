package com.umeetech.photofixai.presentation.screens.resize

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
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
fun ResizeScreen(onBack: () -> Unit) {
    val viewModel: ResizeViewModel = rememberScopedViewModel { ResizeViewModel(it) }
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val pickImage = rememberImagePicker { uri -> viewModel.onImagePicked(context, uri) }

    Column(Modifier.fillMaxSize()) {
        AppTopBar(title = "Resize Image", onBack = onBack)
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Dimens.ScreenPadding)
        ) {
            if (!state.hasImage) {
                EmptyStateView(
                    title = "Select an image",
                    subtitle = "Choose a photo to resize with precise dimensions.",
                    icon = Icons.Filled.PhotoLibrary,
                    action = { PrimaryButtonFullWidth("Select Image", pickImage, icon = Icons.Filled.PhotoLibrary) }
                )
            } else {
                ImagePreviewCard(model = state.original, aspectRatio = 1f)
                Spacer(Modifier.height(Dimens.SpaceS))
                Text(
                    "Original: ${state.originalWidth} × ${state.originalHeight} px",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(Modifier.height(Dimens.SpaceL))
                SectionHeader(title = "Dimensions")
                Spacer(Modifier.height(Dimens.SpaceM))
                Row(horizontalArrangement = Arrangement.spacedBy(Dimens.SpaceM)) {
                    OutlinedTextField(
                        value = state.targetWidth.toString(),
                        onValueChange = { it.toIntOrNull()?.let(viewModel::setWidth) },
                        label = { Text("Width (px)") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = state.targetHeight.toString(),
                        onValueChange = { it.toIntOrNull()?.let(viewModel::setHeight) },
                        label = { Text("Height (px)") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(Modifier.height(Dimens.SpaceM))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Switch(checked = state.keepAspect, onCheckedChange = viewModel::toggleKeepAspect)
                    Spacer(Modifier.height(Dimens.SpaceS))
                    Text("  Keep aspect ratio", style = MaterialTheme.typography.bodyMedium)
                }

                Spacer(Modifier.height(Dimens.SpaceM))
                Text("Scale: ${state.percentage}%", style = MaterialTheme.typography.bodyMedium)
                Slider(
                    value = state.percentage.toFloat(),
                    onValueChange = { viewModel.setPercentage(it.toInt()) },
                    valueRange = 10f..200f
                )

                Spacer(Modifier.height(Dimens.SpaceM))
                SectionHeader(title = "Format")
                Spacer(Modifier.height(Dimens.SpaceM))
                FormatSelector(selected = state.format, onSelect = viewModel::setFormat, modifier = Modifier.fillMaxWidth())

                Spacer(Modifier.height(Dimens.SpaceM))
                Text(
                    "Estimated size: ${FileSizeUtils.formatBytes(state.estimatedBytes)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(Modifier.height(Dimens.SpaceXL))
                PrimaryButtonFullWidth(
                    text = "Save",
                    onClick = { viewModel.export(context) {} },
                    loading = state.status is ToolStatus.Exporting
                )
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
            Spacer(Modifier.height(Dimens.Space3XL))
        }
    }

    (state.status as? ToolStatus.Error)?.let { err ->
        ErrorDialog(message = err.error.message, onDismiss = viewModel::dismissError)
    }
    if (state.status is ToolStatus.ExportSuccess) {
        SuccessDialog(
            title = "Saved!",
            message = "Resized image saved to your gallery.",
            onConfirm = viewModel::consumeExportSuccess
        )
    }
}
