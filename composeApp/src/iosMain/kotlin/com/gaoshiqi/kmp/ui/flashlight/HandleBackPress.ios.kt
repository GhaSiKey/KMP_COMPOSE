package com.gaoshiqi.kmp.ui.flashlight

import androidx.compose.runtime.Composable

/**
 * iOS 平台的返回键处理实现
 * 
 * iOS 使用导航栏的返回按钮，不需要特殊处理。
 * 
 * @param onBack 返回键被按下时的回调（iOS 上不使用）
 */
@Composable
actual fun HandleBackPress(onBack: () -> Unit) {
    // iOS 不需要特殊的返回键处理
    // 用户通过导航栏的返回按钮或手势返回
}
