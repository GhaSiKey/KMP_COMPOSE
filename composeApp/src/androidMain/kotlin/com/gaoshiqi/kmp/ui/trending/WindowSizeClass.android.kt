package com.gaoshiqi.kmp.ui.trending

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Android 平台实现：获取当前窗口宽度
 */
@Composable
actual fun rememberWindowWidth(): Dp {
    val configuration = LocalConfiguration.current
    return configuration.screenWidthDp.dp
}
