package com.umeetech.photofixai.domain.model

/** Square/product presets for the Product Photo Maker. */
data class MarketplacePreset(
    val id: String,
    val name: String,
    val pixelSize: Int,
    val aspectRatio: Float,
    val description: String
) {
    companion object {
        val presets = listOf(
            MarketplacePreset("daraz", "Daraz", 1000, 1f, "1000 × 1000, white background."),
            MarketplacePreset("amazon", "Amazon", 2000, 1f, "2000 × 2000, pure white background."),
            MarketplacePreset("shopify", "Shopify", 2048, 1f, "2048 × 2048 square."),
            MarketplacePreset("instagram", "Instagram Shop", 1080, 1f, "1080 × 1080 square."),
            MarketplacePreset("custom_square", "Custom Square", 1200, 1f, "Custom square export.")
        )
    }
}
