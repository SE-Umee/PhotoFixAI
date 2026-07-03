package com.umeetech.photofixai.presentation.screens.history

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.umeetech.photofixai.core.image.FileSizeUtils
import com.umeetech.photofixai.core.utils.toReadableDateTime
import com.umeetech.photofixai.di.rememberScopedViewModel
import com.umeetech.photofixai.domain.model.HistoryItem
import com.umeetech.photofixai.presentation.components.AppTopBar
import com.umeetech.photofixai.presentation.components.EmptyStateView
import com.umeetech.photofixai.presentation.components.SecondaryButton
import com.umeetech.photofixai.presentation.theme.Dimens

@Composable
fun HistoryScreen(onBrowseTools: () -> Unit) {
    val viewModel: HistoryViewModel = rememberScopedViewModel { HistoryViewModel(it) }
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    Column(Modifier.fillMaxSize()) {
        AppTopBar(
            title = "History",
            actions = {
                if (state.items.isNotEmpty()) {
                    IconButton(onClick = viewModel::clearAll) {
                        Icon(Icons.Filled.DeleteSweep, contentDescription = "Clear all")
                    }
                }
            }
        )

        if (state.items.isEmpty() && !state.loading) {
            EmptyStateView(
                title = "No history yet",
                subtitle = "Images you export will be saved here for quick access.",
                icon = Icons.Filled.History,
                action = { SecondaryButton(text = "Browse tools", onClick = onBrowseTools) }
            )
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(Dimens.ScreenPadding),
                horizontalArrangement = Arrangement.spacedBy(Dimens.SpaceM),
                verticalArrangement = Arrangement.spacedBy(Dimens.SpaceM),
                modifier = Modifier.fillMaxSize()
            ) {
                items(state.items, key = { it.id }) { item ->
                    HistoryCard(
                        item = item,
                        onOpen = { runCatching { context.startActivity(viewModel.openIntent(item)) } },
                        onShare = { runCatching { context.startActivity(Intent.createChooser(viewModel.shareIntent(item), "Share")) } },
                        onDelete = { viewModel.delete(item) }
                    )
                }
            }
        }
    }
}

@Composable
private fun HistoryCard(
    item: HistoryItem,
    onOpen: () -> Unit,
    onShare: () -> Unit,
    onDelete: () -> Unit
) {
    var menuOpen by remember { mutableStateOf(false) }
    Card(onClick = onOpen, shape = MaterialTheme.shapes.medium) {
        Column {
            Box {
                AsyncImage(
                    model = item.filePath,
                    contentDescription = item.toolType.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                )
                IconButton(
                    onClick = { menuOpen = true },
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Icon(
                        Icons.Filled.MoreVert,
                        contentDescription = "Options",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                    DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
                        DropdownMenuItem(text = { Text("Open") }, onClick = { menuOpen = false; onOpen() })
                        DropdownMenuItem(text = { Text("Share") }, onClick = { menuOpen = false; onShare() })
                        DropdownMenuItem(text = { Text("Delete") }, onClick = { menuOpen = false; onDelete() })
                    }
                }
            }
            Column(Modifier.padding(Dimens.SpaceM)) {
                Text(item.toolType.title, style = MaterialTheme.typography.titleSmall, maxLines = 1)
                Text(
                    "${item.outputFormat} · ${FileSizeUtils.formatBytes(item.fileSizeBytes)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    item.createdAt.toReadableDateTime(),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
