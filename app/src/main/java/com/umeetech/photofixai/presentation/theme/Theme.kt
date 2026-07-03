package com.umeetech.photofixai.presentation.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColors = lightColorScheme(
    primary = BrandPrimary,
    onPrimary = Color.White,
    primaryContainer = BrandPrimaryContainer,
    onPrimaryContainer = BrandPrimaryDark,
    secondary = BrandAccent,
    onSecondary = Color.White,
    secondaryContainer = BrandAccentContainer,
    onSecondaryContainer = Color(0xFF065F46),
    tertiary = BrandPrimaryDark,
    background = BackgroundLight,
    onBackground = TextPrimary,
    surface = SurfaceLight,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = TextSecondary,
    outline = BorderColor,
    outlineVariant = BorderColor,
    error = ErrorColor,
    onError = Color.White,
    // Surface-container tokens MUST be set explicitly: M3 components such as
    // NavigationBar, ModalBottomSheet, dialogs and dropdown menus read these,
    // and their defaults are purple-tinted baseline neutrals that clash with
    // the brand (they made the bottom bar look pink).
    surfaceBright = SurfaceLight,
    surfaceDim = Color(0xFFE2E8F0),
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = Color(0xFFFFFFFF),
    surfaceContainer = Color(0xFFFFFFFF),
    surfaceContainerHigh = Color(0xFFFFFFFF),
    surfaceContainerHighest = SurfaceVariantLight
)

private val DarkColors = darkColorScheme(
    primary = BrandPrimaryLight,
    onPrimary = Color(0xFF0B1120),
    primaryContainer = BrandPrimaryDark,
    onPrimaryContainer = BrandPrimaryContainer,
    secondary = BrandAccent,
    onSecondary = Color(0xFF04231A),
    secondaryContainer = Color(0xFF065F46),
    onSecondaryContainer = BrandAccentContainer,
    tertiary = BrandPrimaryLight,
    background = BackgroundDark,
    onBackground = TextPrimaryDark,
    surface = SurfaceDark,
    onSurface = TextPrimaryDark,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = TextSecondaryDark,
    outline = BorderColorDark,
    outlineVariant = BorderColorDark,
    error = ErrorColor,
    onError = Color.White,
    // Same fix for dark mode: keep containers in the brand slate family.
    surfaceBright = Color(0xFF273449),
    surfaceDim = BackgroundDark,
    surfaceContainerLowest = Color(0xFF060B15),
    surfaceContainerLow = SurfaceDark,
    surfaceContainer = SurfaceDark,
    surfaceContainerHigh = SurfaceVariantDark,
    surfaceContainerHighest = Color(0xFF273449)
)

/**
 * Root theme wrapper for PhotoFix AI.
 *
 * Dynamic color is intentionally disabled so the brand palette stays consistent
 * across every device — a deliberate choice for a strongly branded product.
 */
@Composable
fun PhotoFixTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window ?: return@SideEffect
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars =
                colorScheme.background.luminance() > 0.5f
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
