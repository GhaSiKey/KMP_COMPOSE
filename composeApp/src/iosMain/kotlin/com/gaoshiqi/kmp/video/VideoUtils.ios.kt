package com.gaoshiqi.kmp.video

import platform.Foundation.NSString
import platform.Foundation.stringWithFormat

/**
 * iOS 平台的 String.format 实现
 * 使用 NSString.stringWithFormat
 *
 * 注意：iOS 使用 %d 而非 %02d 的格式化方式有所不同，
 * 这里手动处理常见的时间格式化场景
 */
actual fun String.Companion.format(format: String, vararg args: Any): String {
    // 简单的格式化实现，支持 %02d 格式
    var result = format
    args.forEach { arg ->
        result = when {
            result.contains("%02d") -> {
                val value = (arg as Number).toInt()
                result.replaceFirst("%02d", value.toString().padStart(2, '0'))
            }
            result.contains("%d") -> {
                result.replaceFirst("%d", (arg as Number).toInt().toString())
            }
            result.contains("%s") -> {
                result.replaceFirst("%s", arg.toString())
            }
            else -> result
        }
    }
    return result
}
