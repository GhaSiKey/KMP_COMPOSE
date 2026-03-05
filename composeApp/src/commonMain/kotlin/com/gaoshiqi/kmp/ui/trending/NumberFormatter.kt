package com.gaoshiqi.kmp.ui.trending

import com.gaoshiqi.kmp.video.format

/**
 * 数字格式化器
 * 将大数字转换为易读格式（如 1.2K, 123K, 1.2M）
 */
object NumberFormatter {
    /**
     * 格式化数字
     * @param number 原始数字
     * @return 格式化后的字符串
     * 
     * 规则：
     * - >= 1,000,000: X.XM 格式（如 1.2M）
     * - 10,000-999,999: XXXK 格式（如 123K）
     * - 1,000-9,999: X.XK 格式（如 1.2K）
     * - < 1,000: 保持原样
     */
    fun format(number: Int): String {
        return when {
            number >= 1_000_000 -> {
                val millions = number / 1_000_000.0
                formatDecimal(millions, 1) + "M"
            }
            number >= 10_000 -> {
                val thousands = number / 1000
                "${thousands}K"
            }
            number >= 1_000 -> {
                val thousands = number / 1000.0
                formatDecimal(thousands, 1) + "K"
            }
            else -> number.toString()
        }
    }
    
    /**
     * 格式化小数，保留指定位数
     */
    private fun formatDecimal(value: Double, decimals: Int): String {
        val multiplier = when (decimals) {
            1 -> 10.0
            2 -> 100.0
            else -> 1.0
        }
        val rounded = kotlin.math.round(value * multiplier) / multiplier
        return when (decimals) {
            1 -> String.format("%.1f", rounded)
            2 -> String.format("%.2f", rounded)
            else -> rounded.toInt().toString()
        }
    }
}
