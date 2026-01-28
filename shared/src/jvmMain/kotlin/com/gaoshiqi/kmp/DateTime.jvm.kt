package com.gaoshiqi.kmp

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

actual fun getCurrentDateTime(): String {
    val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    return formatter.format(Date())
}
