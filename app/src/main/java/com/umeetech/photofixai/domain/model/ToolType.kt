package com.umeetech.photofixai.domain.model

/**
 * The catalog of tools offered by PhotoFix AI. Presentation maps each entry to an
 * icon + route; history records which tool produced an output.
 */
enum class ToolType(
    val title: String,
    val shortDescription: String
) {
    BACKGROUND_REMOVER("Background Remover", "Remove or replace image backgrounds instantly."),
    PASSPORT_PHOTO("Passport Photo Maker", "Create passport, visa & ID photos in correct sizes."),
    RESIZE("Resize Image", "Change dimensions with pixel-perfect control."),
    COMPRESS("Compress Image", "Shrink file size while keeping quality."),
    PRODUCT_PHOTO("Product Photo Maker", "Marketplace-ready product shots with clean backgrounds."),
    SIGNATURE("Signature Resize", "Clean up and resize signatures for forms."),
    PROFILE_PICTURE("Profile Picture Maker", "Crop and style perfect profile pictures."),
    DOCUMENT_RESIZE("Document Photo Resize", "Resize document photos to required specs."),
    TRANSPARENT_PNG("Transparent PNG Maker", "Export crisp transparent PNGs."),
    CUSTOM_BACKGROUND("Custom Background Color", "Apply any solid background color.");
}
