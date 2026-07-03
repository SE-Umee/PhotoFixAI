package com.umeetech.photofixai.core.permissions

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat

/**
 * Modern, privacy-friendly permission helpers.
 *
 * IMPORTANT: The app primarily uses the Android Photo Picker
 * ([androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia]),
 * which requires NO storage permission on any API level. These helpers exist only
 * for legacy fallbacks and saving on very old devices.
 */
object PermissionUtils {

    /** Reading images only needs a permission on API < 33 (and only for direct
     *  MediaStore reads, not the Photo Picker). */
    fun needsReadMediaPermission(): Boolean =
        Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU

    fun readMediaPermission(): String =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

    /** Saving to the gallery needs WRITE_EXTERNAL_STORAGE only on API <= 28. */
    fun needsWritePermission(): Boolean =
        Build.VERSION.SDK_INT <= Build.VERSION_CODES.P

    fun hasPermission(context: Context, permission: String): Boolean =
        ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
}
