package com.gaoshiqi.kmp.video

/**
 * Android 平台的 String.format 实现
 * 直接使用 Java 的 String.format
 */
actual fun String.Companion.format(format: String, vararg args: Any): String =
    java.lang.String.format(format, *args)
