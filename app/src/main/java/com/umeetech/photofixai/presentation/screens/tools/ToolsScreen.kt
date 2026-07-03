package com.umeetech.photofixai.presentation.screens.tools

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.umeetech.photofixai.domain.model.ToolType
import com.umeetech.photofixai.presentation.components.AppTopBar
import com.umeetech.photofixai.presentation.components.FeatureCard
import com.umeetech.photofixai.presentation.components.ToolVisuals
import com.umeetech.photofixai.presentation.theme.Dimens

/** Full catalog of tools rendered as professional cards. */
@Composable
fun ToolsScreen(onOpenTool: (ToolType) -> Unit) {
    val tools = ToolType.entries
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = Dimens.ScreenPadding,
            end = Dimens.ScreenPadding,
            bottom = Dimens.Space3XL
        ),
        verticalArrangement = Arrangement.spacedBy(Dimens.SpaceM)
    ) {
        item { AppTopBar(title = "All Tools") }
        items(tools) { tool ->
            FeatureCard(
                title = tool.title,
                description = tool.shortDescription,
                icon = ToolVisuals.icon(tool),
                accent = ToolVisuals.accent(tool),
                onOpen = { onOpenTool(tool) }
            )
        }
    }
}
