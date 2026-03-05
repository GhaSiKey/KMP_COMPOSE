package com.gaoshiqi.kmp.video

import kotlin.math.round

/**
 * Web (JS) 平台的 String.format 实现
 * 手动处理常见的格式化场景
 */
actual fun String.Companion.format(format: String, vararg args: Any): String {
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
