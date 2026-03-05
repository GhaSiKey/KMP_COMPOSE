package com.gaoshiqi.kmp.video

import platform.Foundation.NSString
import platform.Foundation.stringWithFormat
import kotlin.math.round

/**
 * iOS 平台的 String.format 实现
 * 使用 NSString.stringWithFormat 或手动格式化
 */
actual fun String.Companion.format(format: String, vararg args: Any): String {
    // 简单的格式化实现，支持 %02d, %d, %s, %.1f, %.2f 格式
    var result = format
    args.forEach { arg ->
        result = when {
            result.contains("%.1f") -> {
                val value = (arg as Number).toDouble()
                val rounded = round(value * 10) / 10
                val intPart = rounded.toInt()
                val decimalPart = ((rounded - intPart) * 10).toInt()
                result.replaceFirst("%.1f", "$intPart.$decimalPart")
            }
            result.contains("%.2f") -> {
                val value = (arg as Number).toDouble()
                val rounded = round(value * 100) / 100
                val intPart = rounded.toInt()
                val decimalPart = ((rounded - intPart) * 100).toInt()
                val decimalStr = decimalPart.toString().padStart(2, '0')
                result.replaceFirst("%.2f", "$intPart.$decimalStr")
            }
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
