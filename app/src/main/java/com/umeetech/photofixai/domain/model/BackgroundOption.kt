package com.umeetech.photofixai.domain.model

/**
 * A background choice in editors. [colorArgb] is null for the transparent option;
 * [isCustom] marks the entry that opens the custom color picker.
 */
data class BackgroundOption(
    val id: String,
    val label: String,
    val colorArgb: Long?,
    val isTransparent: Boolean = false,
    val isCustom: Boolean = false
) {
    companion object {
        val Transparent = BackgroundOption("transparent", "Transparent", null, isTransparent = true)
        val White = BackgroundOption("white", "White", 0xFFFFFFFF)
        val Blue = BackgroundOption("blue", "Blue", 0xFF2563EB)
        val Red = BackgroundOption("red", "Red", 0xFFEF4444)
        val Custom = BackgroundOption("custom", "Custom", null, isCustom = true)

        /** Standard set shown in the Background Remover editor. */
        val defaults = listOf(Transparent, White, Blue, Red, Custom)

        /** Solid-only set (no transparency) for passport/product photos. */
        val solids = listOf(White, Blue, Red, Custom)
    }
}
