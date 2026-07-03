package com.umeetech.photofixai.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material.icons.filled.GridView
import androidx.compose.ui.graphics.vector.ImageVector

/** Bottom navigation tabs shown on the main scaffold. */
enum class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector
) {
    HOME(Routes.HOME, "Home", Icons.Filled.Home),
    TOOLS(Routes.TOOLS, "Tools", Icons.Filled.GridView),
    HISTORY(Routes.HISTORY, "History", Icons.Filled.History),
    PREMIUM(Routes.PREMIUM, "Premium", Icons.Filled.WorkspacePremium);

    companion object {
        val items = entries
    }
}
