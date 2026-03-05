package com.gaoshiqi.kmp.ui.flashlight

import androidx.compose.runtime.Composable

/**
 * JVM (Desktop) 平台的返回键处理实现
 * 
 * Desktop 应用通常使用窗口关闭按钮或菜单，不需要特殊的返回键处理。
 * 
 * @param onBack 返回键被按下时的回调（Desktop 上不使用）
 */
@Composable
actual fun HandleBackPress(onBack: () -> Unit) {
    // Desktop 不需要特殊的返回键处理
    // 用户通过 UI 上的退出按钮返回
}
