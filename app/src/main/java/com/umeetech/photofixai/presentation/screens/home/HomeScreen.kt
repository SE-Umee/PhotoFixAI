package com.umeetech.photofixai.presentation.screens.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.umeetech.photofixai.di.rememberScopedViewModel
import com.umeetech.photofixai.domain.model.ToolType
import com.umeetech.photofixai.presentation.components.AppLogoWordmark
import com.umeetech.photofixai.presentation.components.RecentProjectCard
import com.umeetech.photofixai.presentation.components.SectionHeader
import com.umeetech.photofixai.presentation.components.ToolCard
import com.umeetech.photofixai.presentation.theme.BrandPrimary
import com.umeetech.photofixai.presentation.theme.BrandPrimaryDark
import com.umeetech.photofixai.presentation.theme.Dimens
import com.umeetech.photofixai.presentation.theme.WarningColor

@Composable
fun HomeScreen(
    onOpenTool: (ToolType) -> Unit,
    onOpenAllTools: () -> Unit,
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
        SectionHeader(
            title = "Tools",
            action = {
                TextButton(onClick = onOpenAllTools) { Text("View all") }
            }
        )
        Spacer(Modifier.height(Dimens.SpaceS))
        ToolsGrid(tools = viewModel.featuredTools, onOpenTool = onOpenTool)

        Spacer(Modifier.height(Dimens.SpaceXXL))
        SectionHeader(title = "Recent projects")
        Spacer(Modifier.height(Dimens.SpaceM))
        if (state.recentProjects.isEmpty()) {
            RecentProjectsEmpty()
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
        Spacer(Modifier.height(Dimens.Space4XL))
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
        AppLogoWordmark(logoSize = 40.dp)
        Spacer(Modifier.weight(1f))

        // Compact PRO pill — clearer call to action than a lone icon.
        Surface(
            onClick = onOpenPremium,
            shape = RoundedCornerShape(999.dp),
            color = WarningColor.copy(alpha = 0.14f)
        ) {
            Row(
                Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Filled.WorkspacePremium,
                    contentDescription = "Premium",
                    tint = WarningColor,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    "PRO",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFB45309)
                )
            }
        }

        Spacer(Modifier.width(Dimens.SpaceS))
        Surface(
            onClick = onOpenSettings,
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
        ) {
            Box(Modifier.size(38.dp), contentAlignment = Alignment.Center) {
                Icon(
                    Icons.Filled.Settings,
                    contentDescription = "Settings",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun HeroCard(onSelectImage: () -> Unit) {
    Box(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Dimens.RadiusXL))
            .background(Brush.linearGradient(listOf(BrandPrimary, BrandPrimaryDark)))
    ) {
        // Decorative depth: soft translucent circles clipped by the card.
        Box(
            Modifier
                .align(Alignment.TopEnd)
                .size(190.dp)
                .offset(x = 70.dp, y = (-80).dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.08f))
        )
        Box(
            Modifier
                .align(Alignment.BottomEnd)
                .size(130.dp)
                .offset(x = 40.dp, y = 60.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.06f))
        )

        Column(Modifier.padding(Dimens.SpaceXXL)) {
            Row(
                Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .background(Color.White.copy(alpha = 0.16f))
                    .padding(horizontal = 10.dp, vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Filled.AutoAwesome,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(13.dp)
                )
                Spacer(Modifier.width(5.dp))
                Text(
                    "AI-POWERED",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.2.sp,
                    color = Color.White
                )
            }

            Spacer(Modifier.height(Dimens.SpaceM))
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

            Spacer(Modifier.height(Dimens.SpaceXL))
            // White CTA reads far better on the blue gradient than a blue button.
            Button(
                onClick = onSelectImage,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = BrandPrimaryDark
                ),
                shape = MaterialTheme.shapes.small,
                modifier = Modifier.height(48.dp)
            ) {
                Icon(Icons.Filled.AutoFixHigh, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(Dimens.SpaceS))
                Text(
                    "Select Image",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

/**
 * 2-column tool grid built from plain rows — no fixed-height LazyGrid hacks, so
 * cards can never be clipped. IntrinsicSize keeps both cards in a row equal height.
 */
@Composable
private fun ToolsGrid(tools: List<ToolType>, onOpenTool: (ToolType) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(Dimens.SpaceM)) {
        tools.chunked(2).forEach { row ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Max),
                horizontalArrangement = Arrangement.spacedBy(Dimens.SpaceM)
            ) {
                row.forEach { tool ->
                    ToolCard(
                        tool = tool,
                        onClick = { onOpenTool(tool) },
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    )
                }
                if (row.size == 1) Spacer(Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun RecentProjectsEmpty() {
    Surface(
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            Modifier.padding(Dimens.SpaceXXL),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Dimens.SpaceS)
        ) {
            Box(
                Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.10f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Filled.PhotoLibrary,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(26.dp)
                )
            }
            Text(
                "No projects yet",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                "Images you edit and export will show up here.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}
