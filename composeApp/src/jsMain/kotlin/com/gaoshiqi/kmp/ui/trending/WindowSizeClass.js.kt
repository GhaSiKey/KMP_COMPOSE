package com.gaoshiqi.kmp.ui.trending

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.browser.window

/**
 * JS/Web 平台实现：获取当前窗口宽度
 */
@Composable
actual fun rememberWindowWidth(): Dp {
    // 使用浏览器窗口宽度
    val widthPx = remember { window.innerWidth }
    // 假设 1dp = 1px（简化处理，实际应该考虑设备像素比）
    return widthPx.dp
}
