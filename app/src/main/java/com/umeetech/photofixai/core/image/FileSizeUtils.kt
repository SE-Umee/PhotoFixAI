package com.umeetech.photofixai.core.image

import java.util.Locale

/** Helpers for human-readable file sizes and size math. */
object FileSizeUtils {

    /** Formats a byte count as a human-readable string, e.g. "1.2 MB". */
    fun formatBytes(bytes: Long): String {
        if (bytes <= 0) return "0 KB"
        val kb = bytes / 1024.0
        return if (kb < 1024) {
            String.format(Locale.US, "%.0f KB", kb)
        } else {
            String.format(Locale.US, "%.2f MB", kb / 1024.0)
        }
    }

    fun bytesToKb(bytes: Long): Int = (bytes / 1024).toInt()

    fun kbToBytes(kb: Int): Long = kb.toLong() * 1024L
}
