package com.gaoshiqi.kmp.ui.trending

import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * iOS 平台实现：获取当前窗口宽度
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
actual fun rememberWindowWidth(): Dp {
    val windowInfo = LocalWindowInfo.current
    // iOS 默认使用手机屏幕宽度
    // 实际宽度可以通过 UIScreen 获取，这里使用一个合理的默认值
    return 390.dp // iPhone 14 Pro 的宽度
}
