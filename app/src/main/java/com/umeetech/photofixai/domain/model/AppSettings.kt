package com.umeetech.photofixai.domain.model

import com.umeetech.photofixai.core.export.ExportFormat

enum class ThemeMode(val label: String) { SYSTEM("System default"), LIGHT("Light"), DARK("Dark") }

enum class ExportQuality(val label: String, val quality: Int) {
    STANDARD("Standard", 80),
    HIGH("High", 92),
    MAXIMUM("Maximum", 100)
}

/** User-configurable app settings persisted via DataStore. */
data class AppSettings(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val exportQuality: ExportQuality = ExportQuality.HIGH,
    val defaultFormat: ExportFormat = ExportFormat.PNG,
    val isPremium: Boolean = false
)
