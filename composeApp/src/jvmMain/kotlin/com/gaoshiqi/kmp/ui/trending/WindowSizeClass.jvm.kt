package com.gaoshiqi.kmp.ui.trending

import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * JVM/Desktop 平台实现：获取当前窗口宽度
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
actual fun rememberWindowWidth(): Dp {
    val windowInfo = LocalWindowInfo.current
    // Desktop 默认使用较大的宽度（假设为桌面环境）
    // 实际宽度可以通过 windowInfo.containerSize.width 获取，但需要转换为 dp
    // 这里使用一个合理的默认值
    return 1200.dp
}
