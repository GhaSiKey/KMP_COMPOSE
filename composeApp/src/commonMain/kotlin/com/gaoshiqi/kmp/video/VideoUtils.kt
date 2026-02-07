package com.gaoshiqi.kmp.video

/**
 * 视频相关工具函数
 */

/**
 * 将毫秒时间格式化为 mm:ss 或 hh:mm:ss 格式
 *
 * @param milliseconds 毫秒数
 * @return 格式化后的时间字符串
 */
fun formatDuration(milliseconds: Long): String {
    if (milliseconds <= 0) return "00:00"

    val totalSeconds = milliseconds / 1000
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60

    return if (hours > 0) {
        String.format("%02d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format("%02d:%02d", minutes, seconds)
    }
}

/**
 * 将秒数格式化为 mm:ss 或 hh:mm:ss 格式
 *
 * @param seconds 秒数
 * @return 格式化后的时间字符串
 */
fun formatDurationSeconds(seconds: Int): String {
    if (seconds <= 0) return "00:00"

    val hours = seconds / 3600
    val minutes = (seconds % 3600) / 60
    val secs = seconds % 60

    return if (hours > 0) {
        String.format("%02d:%02d:%02d", hours, minutes, secs)
    } else {
        String.format("%02d:%02d", minutes, secs)
    }
}

/**
 * expect 声明：格式化数字为固定位数
 * 不同平台的 String.format 实现不同
 */
expect fun String.Companion.format(format: String, vararg args: Any): String
