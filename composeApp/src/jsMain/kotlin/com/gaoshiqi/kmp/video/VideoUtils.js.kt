package com.gaoshiqi.kmp.video

/**
 * Web (JS) 平台的 String.format 实现
 * 手动处理常见的格式化场景
 */
actual fun String.Companion.format(format: String, vararg args: Any): String {
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
