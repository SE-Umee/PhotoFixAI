package com.umeetech.photofixai.presentation.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items as gridItems
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.umeetech.photofixai.di.rememberScopedViewModel
import com.umeetech.photofixai.domain.model.ToolType
import com.umeetech.photofixai.presentation.components.EmptyStateView
import com.umeetech.photofixai.presentation.components.PrimaryButton
import com.umeetech.photofixai.presentation.components.RecentProjectCard
import com.umeetech.photofixai.presentation.components.SectionHeader
import com.umeetech.photofixai.presentation.components.ToolCard
import com.umeetech.photofixai.presentation.theme.BrandPrimary
import com.umeetech.photofixai.presentation.theme.BrandPrimaryDark
import com.umeetech.photofixai.presentation.theme.Dimens

@Composable
fun HomeScreen(
    onOpenTool: (ToolType) -> Unit,
    onOpenSettings: () -> Unit,
    onOpenPremium: () -> Unit,
    onOpenHistoryItem: () -> Unit
) {
    val viewModel: HomeViewModel = rememberScopedViewModel { HomeViewModel(it) }
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Dimens.ScreenPadding)
    ) {
        HomeTopBar(onOpenSettings = onOpenSettings, onOpenPremium = onOpenPremium)

        Spacer(Modifier.height(Dimens.SpaceS))
        HeroCard(onSelectImage = { onOpenTool(ToolType.BACKGROUND_REMOVER) })

        Spacer(Modifier.height(Dimens.SpaceXXL))
        SectionHeader(title = "Tools")
        Spacer(Modifier.height(Dimens.SpaceM))
        ToolsGrid(tools = viewModel.featuredTools, onOpenTool = onOpenTool)

        Spacer(Modifier.height(Dimens.SpaceXXL))
        SectionHeader(title = "Recent projects")
        Spacer(Modifier.height(Dimens.SpaceM))
        if (state.recentProjects.isEmpty()) {
            EmptyStateView(
                title = "No projects yet",
                subtitle = "Your edited images will appear here. Start by removing a background!"
            )
        } else {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(Dimens.SpaceM),
                contentPadding = PaddingValues(vertical = Dimens.SpaceXS)
            ) {
                items(state.recentProjects, key = { it.id }) { item ->
                    RecentProjectCard(item = item, onClick = onOpenHistoryItem)
                }
            }
        }
        Spacer(Modifier.height(Dimens.Space3XL))
    }
}

@Composable
private fun HomeTopBar(onOpenSettings: () -> Unit, onOpenPremium: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = Dimens.SpaceM),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Filled.AutoFixHigh, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
        }
        Spacer(Modifier.width(Dimens.SpaceM))
        Text("PhotoFix AI", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(Modifier.weight(1f))
        IconButton(onClick = onOpenPremium) {
            Icon(Icons.Filled.WorkspacePremium, contentDescription = "Premium", tint = MaterialTheme.colorScheme.secondary)
        }
        IconButton(onClick = onOpenSettings) {
            Icon(Icons.Filled.Settings, contentDescription = "Settings")
        }
    }
}

@Composable
private fun HeroCard(onSelectImage: () -> Unit) {
    Box(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Dimens.RadiusL))
            .background(Brush.linearGradient(listOf(BrandPrimary, BrandPrimaryDark)))
            .padding(Dimens.SpaceXXL)
    ) {
        Column {
            Text(
                "Remove Background\nInstantly",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(Dimens.SpaceS))
            Text(
                "AI-powered cutouts, passport photos, resizing and more.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.9f)
            )
            Spacer(Modifier.height(Dimens.SpaceL))
            PrimaryButton(
                text = "Select Image",
                onClick = onSelectImage,
                icon = Icons.Filled.AutoFixHigh
            )
        }
    }
}

@Composable
private fun ToolsGrid(tools: List<ToolType>, onOpenTool: (ToolType) -> Unit) {
    // Non-scrolling grid embedded in a scrolling column: fixed height per row.
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        horizontalArrangement = Arrangement.spacedBy(Dimens.SpaceM),
        verticalArrangement = Arrangement.spacedBy(Dimens.SpaceM),
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 1000.dp)
            .height(((tools.size + 2) / 3 * 128).dp),
        userScrollEnabled = false
    ) {
        gridItems(tools) { tool ->
            ToolCard(tool = tool, onClick = { onOpenTool(tool) })
        }
    }
}
