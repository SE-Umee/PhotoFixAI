package com.umeetech.photofixai.presentation.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

/**
 * Wraps the modern Android Photo Picker (ActivityResultContracts.PickVisualMedia).
 *
 * The Photo Picker requires NO runtime permission on any supported API level and
 * only exposes the single image the user chooses — the most privacy-friendly and
 * Play-Store-friendly way to select media.
 *
 * Returns a lambda that launches the picker; the selected [Uri] (or null) is
 * delivered to [onImagePicked].
 */
@Composable
fun rememberImagePicker(onImagePicked: (Uri?) -> Unit): () -> Unit {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri -> onImagePicked(uri) }

    return remember(launcher) {
        {
            launcher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        }
    }
}
