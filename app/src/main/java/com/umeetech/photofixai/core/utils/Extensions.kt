package com.umeetech.photofixai.core.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** Formats an epoch-millis timestamp into a readable date/time string. */
fun Long.toReadableDateTime(pattern: String = "dd MMM yyyy, HH:mm"): String {
    return SimpleDateFormat(pattern, Locale.getDefault()).format(Date(this))
}

/** Rounds a float to [decimals] decimal places. */
fun Float.roundTo(decimals: Int): Float {
    var multiplier = 1f
    repeat(decimals) { multiplier *= 10 }
    return kotlin.math.round(this * multiplier) / multiplier
}

/** Safely coerces an Int into a positive value (>= [min]). */
fun Int.atLeast(min: Int): Int = if (this < min) min else this
