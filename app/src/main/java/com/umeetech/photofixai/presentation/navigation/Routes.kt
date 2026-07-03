package com.umeetech.photofixai.presentation.navigation

import com.umeetech.photofixai.domain.model.ToolType

/** All navigation destinations. Tool screens accept an optional template/preset id. */
object Routes {
    const val SPLASH = "splash"
    const val ONBOARDING = "onboarding"

    // Bottom-nav tabs
    const val HOME = "home"
    const val TOOLS = "tools"
    const val HISTORY = "history"
    const val PREMIUM = "premium"

    const val SETTINGS = "settings"

    // Feature tool screens
    const val BACKGROUND_REMOVER = "background_remover"
    const val PASSPORT_PHOTO = "passport_photo"
    const val RESIZE = "resize"
    const val COMPRESS = "compress"
    const val PRODUCT_PHOTO = "product_photo"
    const val SIGNATURE = "signature"

    /** Resolves the route for a given tool. Tools without a dedicated editor open
     *  the Background Remover (their behavior is a variant of it). */
    fun forTool(tool: ToolType): String = when (tool) {
        ToolType.BACKGROUND_REMOVER -> BACKGROUND_REMOVER
        ToolType.PASSPORT_PHOTO -> PASSPORT_PHOTO
        ToolType.RESIZE -> RESIZE
        ToolType.COMPRESS -> COMPRESS
        ToolType.PRODUCT_PHOTO -> PRODUCT_PHOTO
        ToolType.SIGNATURE -> SIGNATURE
        ToolType.PROFILE_PICTURE -> BACKGROUND_REMOVER
        ToolType.DOCUMENT_RESIZE -> RESIZE
        ToolType.TRANSPARENT_PNG -> BACKGROUND_REMOVER
        ToolType.CUSTOM_BACKGROUND -> BACKGROUND_REMOVER
    }
}
