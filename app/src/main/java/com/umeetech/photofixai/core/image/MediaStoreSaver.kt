package com.umeetech.photofixai.core.image

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import com.umeetech.photofixai.core.constants.Constants
import com.umeetech.photofixai.core.export.ExportFormat
import java.io.File
import java.io.FileOutputStream

/**
 * Saves image bytes to the shared gallery using scoped storage on API 29+ and
 * a legacy MediaStore path on older devices.
 *
 * On API <= 28 the caller must hold WRITE_EXTERNAL_STORAGE (declared with
 * maxSdkVersion=28 in the manifest); on API 29+ no runtime permission is needed.
 */
object MediaStoreSaver {

    /** Saves [bytes] to Pictures/PhotoFix AI and returns the content Uri. */
    fun saveToGallery(
        context: Context,
        bytes: ByteArray,
        fileName: String,
        format: ExportFormat
    ): Uri {
        val resolver = context.contentResolver
        val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        } else {
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }

        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
            put(MediaStore.Images.Media.MIME_TYPE, format.mimeType)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(
                    MediaStore.Images.Media.RELATIVE_PATH,
                    "${Environment.DIRECTORY_PICTURES}/${Constants.GALLERY_ALBUM}"
                )
                put(MediaStore.Images.Media.IS_PENDING, 1)
            }
        }

        val uri = resolver.insert(collection, values)
            ?: throw IllegalStateException("MediaStore insert returned null")

        resolver.openOutputStream(uri)?.use { it.write(bytes) }
            ?: throw IllegalStateException("Unable to open output stream")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            values.clear()
            values.put(MediaStore.Images.Media.IS_PENDING, 0)
            resolver.update(uri, values, null, null)
        }
        return uri
    }

    /** Persists [bytes] to an app-private file (used for history + share source). */
    fun saveToAppStorage(context: Context, bytes: ByteArray, fileName: String): File {
        val dir = File(context.filesDir, "exports").apply { if (!exists()) mkdirs() }
        val file = File(dir, fileName)
        FileOutputStream(file).use { it.write(bytes) }
        return file
    }

    /** Writes [bytes] to a cache file for FileProvider-based sharing. */
    fun writeShareCache(context: Context, bytes: ByteArray, fileName: String): File {
        val dir = File(context.cacheDir, Constants.SHARE_CACHE_DIR).apply { if (!exists()) mkdirs() }
        val file = File(dir, fileName)
        FileOutputStream(file).use { it.write(bytes) }
        return file
    }
}
