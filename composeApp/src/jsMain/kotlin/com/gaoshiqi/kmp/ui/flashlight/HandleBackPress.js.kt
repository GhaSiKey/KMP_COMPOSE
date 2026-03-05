package com.gaoshiqi.kmp.ui.flashlight

import androidx.compose.runtime.Composable

/**
 * JS (Web) 平台的返回键处理实现
 * 
 * Web 应用通常使用浏览器的返回按钮，不需要特殊处理。
 * 
 * @param onBack 返回键被按下时的回调（Web 上不使用）
 */
@Composable
actual fun HandleBackPress(onBack: () -> Unit) {
    // Web 不需要特殊的返回键处理
    // 用户通过 UI 上的退出按钮或浏览器返回按钮返回
}
