package com.umeetech.photofixai.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.umeetech.photofixai.presentation.theme.Dimens

/** Blocking loading dialog for long-running operations. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoadingDialog(message: String = "Processing…") {
    BasicAlertDialog(onDismissRequest = {}) {
        Surface(
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.surface
        ) {
            Row(
                Modifier.padding(Dimens.SpaceXXL),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Dimens.SpaceL)
            ) {
                CircularProgressIndicator(modifier = Modifier.size(28.dp), strokeWidth = 3.dp)
                Text(message, style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}

@Composable
fun ErrorDialog(
    message: String,
    onDismiss: () -> Unit,
    onRetry: (() -> Unit)? = null
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { Icon(Icons.Filled.ErrorOutline, null, tint = MaterialTheme.colorScheme.error) },
        title = { Text("Something went wrong") },
        text = { Text(message) },
        confirmButton = {
            if (onRetry != null) {
                TextButton(onClick = onRetry) { Text("Retry") }
            } else {
                TextButton(onClick = onDismiss) { Text("OK") }
            }
        },
        dismissButton = if (onRetry != null) {
            { TextButton(onClick = onDismiss) { Text("Cancel") } }
        } else null
    )
}

@Composable
fun SuccessDialog(
    title: String,
    message: String,
    confirmText: String = "Done",
    onConfirm: () -> Unit,
    secondaryText: String? = null,
    onSecondary: (() -> Unit)? = null
) {
    AlertDialog(
        onDismissRequest = onConfirm,
        icon = { Icon(Icons.Filled.CheckCircle, null, tint = MaterialTheme.colorScheme.secondary) },
        title = { Text(title) },
        text = { Text(message) },
        confirmButton = { TextButton(onClick = onConfirm) { Text(confirmText) } },
        dismissButton = if (secondaryText != null && onSecondary != null) {
            { TextButton(onClick = onSecondary) { Text(secondaryText) } }
        } else null
    )
}
