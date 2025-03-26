// util/Extensions.kt
package com.example.skypass.util

import android.content.Context
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.*

// String extensions
fun String.capitalize(): String {
    return this.replaceFirstChar {
        if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
    }
}

// Date extensions
fun Long.toReadableDate(pattern: String = "EEE, d MMM"): String {
    val date = Date(this * 1000) // Convert seconds to milliseconds
    val formatter = SimpleDateFormat(pattern, Locale.getDefault())
    return formatter.format(date)
}

// Context extensions
fun Context.showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, duration).show()
}