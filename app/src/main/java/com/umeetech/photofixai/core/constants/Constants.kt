package com.umeetech.photofixai.core.constants

/** App-wide constant values. Avoid scattering magic numbers/strings elsewhere. */
object Constants {

    /** Max pixel dimension we downscale very large images to before processing,
     *  protecting low-RAM devices from OutOfMemory crashes. */
    const val MAX_PROCESSING_DIMENSION = 2048

    /** Directory (inside Pictures/) used when saving to the shared gallery. */
    const val GALLERY_ALBUM = "PhotoFix AI"

    /** Subfolder inside cache used for share-sheet temp files. */
    const val SHARE_CACHE_DIR = "shared"

    /** Default JPEG/WebP export quality (0-100). */
    const val DEFAULT_EXPORT_QUALITY = 92

    /** Room database name. */
    const val DATABASE_NAME = "photofix.db"

    /** DataStore file name for preferences. */
    const val PREFERENCES_NAME = "photofix_prefs"

    /** Bitmap segmentation confidence threshold for on-device background removal. */
    const val SEGMENTATION_CONFIDENCE_THRESHOLD = 0.6f
}
